package dean.weather;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.FirebaseApp;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Dean on 12/25/2016.
 */

public class summaryNotifService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    private Double latitude;
    private Double longitude;
    private Location lastLocation;
    private String todaySummary;

    public summaryNotifService() {
        super(null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Creates a summary weather summary notification at a set time everyday
        Log.i("summaryNotifService", "started");
        FirebaseApp.initializeApp(this);

        //Instantiate GoogleAPIClient
        if(googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();
    }

    //Location

    /**
     * Creates location request.
     */
    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(600000);//10 minutes
        locationRequest.setFastestInterval(300000);//5 minutes
        //City block accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("NotifService", "GoogleAPIClient connected");
        //TODO - CHECK TO SEE IF THE USER HAS A DEFAULT LOCATION SET/IF THE USER WANTS A LOCATION REPORT
        int locationPermissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        //If we have location permissions, check for location settings, if not, then end the service
        if (locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.i("Permission", "Location access granted");
            //Create location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(createLocationRequest());

            //Create location settings request to make sure the request is permitted
            final PendingResult<LocationSettingsResult> locationSettingsResultPendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

            //Check location settings
            locationSettingsResultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    Log.i("LocationSettings", "Callback received");
                    final Status locationStatus = locationSettingsResult.getStatus();

                    switch (locationStatus.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            //All location requirements are satisfied, request location
                            try {
                                Log.i("requestLocation", "pulling");
                                lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                if (lastLocation != null) {
                                    //Get latitude and longitude of last known location
                                    latitude = lastLocation.getLatitude();
                                    longitude = lastLocation.getLongitude();
                                    Log.i("Latitude", String.valueOf(latitude));
                                    Log.i("Longitude", String.valueOf(longitude));
                                    //Determine if a geocoder is available
                                    if (!Geocoder.isPresent()) {
                                        Log.i("Geocoder", "Unavailable");
                                    }

                                    //Pull initial data
                                    pullForecast();
                                } else {
                                    Log.i("notifService", "Unable to gather location");
                                }
                            } catch (SecurityException e) {
                                Log.e("LocationPermission", "Permission denied");
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied
                            Log.i("notifService", "Location settings not satisfied");
                            if (googleApiClient.isConnected()) {
                                googleApiClient.disconnect();
                            }
                            //Change the preference to reflect the service being disabled
                            SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = mySPrefs.edit();
                            editor.putBoolean(getString(R.string.summary_notif_key), false);
                            editor.apply();

                            //End the repeating preference alarm
                            Intent stopAlarm = new Intent(getApplicationContext(), alarmInterfaceService.class);
                            stopAlarm.putExtra("summaryNotif", false);
                            startService(stopAlarm);

                            stopSelf();
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Location settings aren't satisfied, but there is no way to fix them. Do not show dialog.
                            Log.i("notifService", "Location settings not satisfied");
                            if (googleApiClient.isConnected()) {
                                googleApiClient.disconnect();
                            }
                            //Change the preference to reflect the service being disabled
                            SharedPreferences mySPrefs1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor1 = mySPrefs1.edit();
                            editor1.putBoolean(getString(R.string.summary_notif_key), false);
                            editor1.apply();

                            //End the repeating preference alarm
                            Intent stopAlarm1 = new Intent(getApplicationContext(), alarmInterfaceService.class);
                            stopAlarm1.putExtra("summaryNotif", false);
                            startService(stopAlarm1);

                            stopSelf();
                            break;
                    }
                }
            });
        } else {
            Log.i("notifService", "Location permission missing");
            if (googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
            //Change the preference to reflect the service being disabled
            SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = mySPrefs.edit();
            editor.putBoolean(getString(R.string.summary_notif_key), false);
            editor.apply();

            //End the repeating preference alarm
            Intent stopAlarm = new Intent(this, alarmInterfaceService.class);
            stopAlarm.putExtra("summaryNotif", false);
            startService(stopAlarm);

            stopSelf();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Let the user know the operation failed
        Log.i("summaryNotif", "connectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Let the user know the operation failed
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        createErrorNotif();
        Log.i("summaryNotif", "connectionSuspended");
        stopSelf();
    }

    //Dark Sky API
    /**
     * Initializes API Wrapper Lib, and forms pull request to receive weather data.
     */
    private void pullForecast() {
        Log.i("forecastRequest", "pullingForecast");
        //TODO - CHECK FOR THE UNITS AND STUFF, AS WELL AS WHEN THE PREFERENCES CHANGE
        //Get the Dark Sky Wrapper API ready
        ForecastApi.create("331ebe65d3032e48b3c603c113435992");

        //Form a pull request
        RequestBuilder weather = new RequestBuilder();
        final Request request = new Request();
        request.setLat(String.valueOf(latitude));
        request.setLng(String.valueOf(longitude));
        request.setUnits(Request.Units.US);
        request.setLanguage(Request.Language.ENGLISH);

        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                Log.i("DarkSky API", "Pull request successful");
                //Pull and parse weather summary
                todaySummary = weatherResponse.getDaily().getData().get(0).getSummary();

                //Create/update notification
                //Test to see which one to make
                createNotification();

                //Kill the connection
                if(googleApiClient.isConnected()){
                    googleApiClient.disconnect();
                }
                clearData();
                stopSelf();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("DarkSky API", "Error while calling: " + retrofitError.getUrl());
                Log.i("DarkSky API", retrofitError.getMessage());
                //Kill the connection
                if(googleApiClient.isConnected()){
                    googleApiClient.disconnect();
                }
                clearData();
                createErrorNotif();
                stopSelf();
            }
        });
    }

    //Notifications

    /**
     * Creates notification letting the user know that we were unable to get their summary.
     */
    private void createErrorNotif(){
        //Create notification asking the user to try again
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Unable to sync weather")
                        .setContentText("Tap to try again now.");
        Intent serviceIntent = new Intent(this, summaryNotifService.class);
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
        notifBuilder.setContentIntent(servicePendingIntent);
        notifBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MainActivity.SUMMARY_NOTIF_ID, notifBuilder.build());
    }

    /**
     * Creates summary notification for Lollipop through Marshmallow.
     */
    private void createNotification(){
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Today's summary")
                        .setContentText(todaySummary);
        Intent serviceIntent = new Intent(this, notificationIntentHandler.class);
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
        notifBuilder.setContentIntent(servicePendingIntent);
        notifBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MainActivity.SUMMARY_NOTIF_ID, notifBuilder.build());
    }

    /**
     * Clears data stored in variables after notif update/creation.
     */
    private void clearData(){
        lastLocation = null;
        todaySummary = null;
        latitude = 0.0;
        longitude = 0.0;
    }
}