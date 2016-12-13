package dean.weather;

import android.*;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

/**
 * Created by DeanF on 12/11/2016.
 */

public class notificationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
    //Location settings change
    private final int REQUEST_CHANGE_SETTINGS = 15;

    //Address receiver
    protected Location lastLocation;//Location to pass to the address method
    private double latitude;
    private double longitude;

    //Location request
    LocationRequest locationRequest;

    //Google APIs
    private GoogleApiClient googleApiClient;

    //WeatherResponse
    private WeatherResponse pulledWeatherResponse;

    public notificationService() {
        super(null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("notifService", "Started");
        //Setup GoogleAPIClient
        if (googleApiClient == null) {
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
    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(600000);//10 Minutes
        locationRequest.setFastestInterval(60000);//One minute
        //City block accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    //GoogleAPI
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("NotifService", "GoogleAPIClient connected");
        //TODO - CHECK TO SEE IF THE USER HAS A DEFAULT LOCATION SET/IF THE USER WANTS A LOCATION REPORT
        int locationPermissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        //If we have location permissions, check for location settings, if not, then end the service
        if(locationPermissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.i("Permission", "Location access granted");
            //Create location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(createLocationRequest());

            //Create location request
            locationRequest = createLocationRequest();

            //Create location settings request to make sure the request is permitted
            final PendingResult<LocationSettingsResult> locationSettingsResultPendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

            //Check location settings
            locationSettingsResultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    Log.i("LocationSettings", "Callback received");
                    final Status locationStatus = locationSettingsResult.getStatus();
                    final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                    switch (locationStatus.getStatusCode()){
                        case LocationSettingsStatusCodes.SUCCESS:
                            //All location requirements are satisfied, request location
                            try{
                                Log.i("requestLocation", "pulling");
                                lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                if(lastLocation != null){
                                    //Get latitude and longitude of last known location
                                    latitude = lastLocation.getLatitude();
                                    longitude = lastLocation.getLongitude();
                                    Log.i("Latitude", String.valueOf(latitude));
                                    Log.i("Longitude", String.valueOf(longitude));
                                    //Determine if a geocoder is available
                                    if(!Geocoder.isPresent()){
                                        Log.i("Geocoder", "Unavailable");
                                    }
                                    //Pull initial data


                                    //Receive location updates
                                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            lastLocation = location;
                                            //Pull new data
                                            
                                        }
                                    });
                                }
                                else{
                                    //TODO - Wait 10 mins, and check for location again

                                }
                            }
                            catch (SecurityException e){
                                Log.e("LocationPermission", "Permission denied");
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied
                            stopSelf();
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Location settings aren't satisfied, but there is no way to fix them. Do not show dialog.
                            stopSelf();
                            break;
                    }
                }
            });
        }
        else{
            stopSelf();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //For now, kill service when connection is lost
        stopSelf();
    }

    //Notification
    /**
     * Creates notification with current weather data.
     */
    private void createNotification(){
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_collapsed);
        notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_cloudy_white);
        notificationView.setTextViewText(R.id.notifCondition, "44\u00B0 - Cloudy");
        notificationView.setTextViewText(R.id.notifLocation, "Boston");
        notificationView.setTextViewText(R.id.notifBody, "Hi - 40° Lo - 22°");
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContent(notificationView)
                        .setSmallIcon(R.drawable.ic_cloudy_white);
        //Intent to go to main activity
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.putExtra("NotificationMsg", "Clicked");//Let onResume know to refresh data
        //Create backstack for intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //Add activity to the top of stack
        stackBuilder.addNextIntent(mainIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        notificationManager.notify(MainActivity.NOTIF_ID, notificationBuilder.build());
    }

}
