package dean.weather;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.FirebaseApp;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.AlertsBlock;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Dean on 12/25/2016.
 */

public class alertNotifService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , LocationListener{
    private GoogleApiClient googleApiClient;
    private Double latitude;
    private Double longitude;
    private Location lastLocation;
//    private String currentAddress;
//    private String todaySummary;
    private String todaySunrise = "---";
    private String todaySunset = "---";
//    private boolean hasLocation;
    private boolean createNewNotif;
    private ArrayList<AlertsBlock> newAlerts = new ArrayList<>();

    public alertNotifService() {
        super(null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Creates a weather alert notif if one is present
        Log.i("AlertNotifService", "started");
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

        //Determine build version
        //Lollipop to Marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            createNewNotif = false;
        }
        //Nougat
        else{
            createNewNotif = true;
        }
    }

    //Location

    /**
     * Creates location request.
     */
    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);//10 seconds
        locationRequest.setFastestInterval(5000);//5 seconds
        //City block accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    /**
     * Starts location updates.
     */
    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, createLocationRequest(), this);
        }
    }

    /**
     * Stops location updates.
     */
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
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
//                                    getAddresses();
                                    //Pull initial data and disconnect from GoogleAPIClient
                                    pullForecast();
                                    googleApiClient.disconnect();
                                } else {
                                    //Request for a location update, and execute rest of logic when a new location is returned
                                    startLocationUpdates();

                                }
                            } catch (SecurityException e) {
                                Log.e("LocationPermission", "Permission denied");
                                googleApiClient.disconnect();
                                stopSelf();
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
                            editor.putBoolean(getString(R.string.alert_notif_key), false);
                            editor.apply();

                            //End the repeating preference alarm
                            Intent stopAlarm = new Intent(getApplicationContext(), alarmInterfaceService.class);
                            stopAlarm.putExtra("alertNotif", false);
                            startService(stopAlarm);

                            //Cancel notifs
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);

                            //Notify the user
//                            Toast.makeText(alertNotifService.this, "Please enable location services to use this service", Toast.LENGTH_SHORT).show();

                            stopSelf();
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Check for airplane mode
                            if(Settings.System.getInt(alertNotifService.this.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0){
                                Log.i("alertNotifService", "Airplane mode on");
                                //Do nothing
                                if (googleApiClient.isConnected()) {
                                    googleApiClient.disconnect();
                                }
                                stopSelf();
                            }
                            else{
                                //Airplane mode is off
                                Log.i("notifService", "Location settings not satisfied");
                                if (googleApiClient.isConnected()) {
                                    googleApiClient.disconnect();
                                }
                                //Change the preference to reflect the service being disabled
                                SharedPreferences mySPrefs1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor1 = mySPrefs1.edit();
                                editor1.putBoolean(getString(R.string.alert_notif_key), false);
                                editor1.apply();

                                //End the repeating preference alarm
                                Intent stopAlarm1 = new Intent(getApplicationContext(), alarmInterfaceService.class);
                                stopAlarm1.putExtra("alertNotif", false);
                                startService(stopAlarm1);

                                //Cancel notifs
                                NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager1.cancel(MainActivity.FOLLOW_NOTIF_ID);

                                //Notify the user
//                                Toast.makeText(alertNotifService.this, "Please enable location services to use this service", Toast.LENGTH_SHORT).show();

                                stopSelf();
                            }
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
            editor.putBoolean(getString(R.string.alert_notif_key), false);
            editor.apply();

            //End the repeating preference alarm
            Intent stopAlarm = new Intent(this, alarmInterfaceService.class);
            stopAlarm.putExtra("alertNotif", false);
            startService(stopAlarm);

            //Cancel notifs
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(MainActivity.ALERT_NOTIF_ID);

            Toast.makeText(this, "Please grant location permissions to use this service", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("AlertNotif", "connectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        Log.i("alertNotif", "connectionFailed");

        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(alertNotifService.this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Error")
                        .setContentText("Problem pulling alerts.");
        notifBuilder.setAutoCancel(true);
        Intent serviceIntent = new Intent(alertNotifService.this, alertNotifService.class);
        PendingIntent servicePendingIntent = PendingIntent.getService(alertNotifService.this, 0, serviceIntent, 0);
        notifBuilder.setContentIntent(servicePendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(MainActivity.ALERT_NOTIF_ID);
        mNotificationManager.notify(MainActivity.ALERT_NOTIF_ID, notifBuilder.build());
        stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("alertNotif", "New location pulled");
        lastLocation = location;
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

        //Stop location updates and disconnect
        stopLocationUpdates();
        googleApiClient.disconnect();
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
        final RequestBuilder weather = new RequestBuilder();
        final Request request = new Request();
        request.addExcludeBlock(Request.Block.CURRENTLY);
        request.addExcludeBlock(Request.Block.MINUTELY);
        request.addExcludeBlock(Request.Block.HOURLY);
        request.addExcludeBlock(Request.Block.FLAGS);
        request.setLat(String.valueOf(latitude));
        request.setLng(String.valueOf(longitude));
        request.setUnits(Request.Units.US);
        request.setLanguage(Request.Language.ENGLISH);

        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                Log.i("DarkSky API", "Pull request successful");
                //Pull and parse weather alerts
                todaySunrise = weatherResponse.getDaily().getData().get(0).getSunriseTime();
                todaySunset = weatherResponse.getDaily().getData().get(0).getSunsetTime();
                if(weatherResponse.getAlerts() != null){
                    Log.i("alerts", weatherResponse.getAlerts().size() + "");
                    for(int i = 0; i < weatherResponse.getAlerts().size(); i++){
                        Log.i("Alert", weatherResponse.getAlerts().get(i).getTitle());
                        Log.i("Alert", weatherResponse.getAlerts().get(i).getDescription());
                    }

                    //Check to see if these are new alerts
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(alertNotifService.this);
                    for(int i = 0; i < weatherResponse.getAlerts().size(); i++){
                        if(!prefs.contains(weatherResponse.getAlerts().get(i).getUri() + ".Alert")){
                            //This is a new alert, add it to the list of new alerts
                            newAlerts.add(weatherResponse.getAlerts().get(i));
                            Log.i("New alert", weatherResponse.getAlerts().get(i).getUri());
                        }
                        else{
                            Log.i("Old alert", "ignoring");
                        }
                    }

                    SharedPreferences.Editor editor = prefs.edit();
                    //Persist the alerts list as to not notify the user of the same alerts
                    for(int i = 0; i < newAlerts.size(); i++){
                        String alertURI = newAlerts.get(i).getUri();
                        Long alertIssueTime = newAlerts.get(i).getTime() * 1000;//Store UNIX in millis
                        editor.putLong(alertURI + ".Alert", alertIssueTime);
                    }

                    //Check to see if it has been 48 hours (just to be safe), and if we can remove any persisted alert from sharedPrefs
                    Map<String,?> sharedPrefsKeys = prefs.getAll();
                    for(String key : sharedPrefsKeys.keySet()){
                        Log.i("Printing key", key);
                        if(key.contains(".Alert")){
                            //Get the issuance time of the alert in UNIX format
                            Log.i("Key", "Contains .Alert");
                            Long value = (Long) sharedPrefsKeys.get(key);
                            //If 48 hours has passed since the issuance of the alert
                            if(System.currentTimeMillis() >= value + 172800000){
                                //Remove the alert
                                Log.i("Removing alert", key);
                                editor.remove(key);
                            }
                        }
                    }
                    editor.commit();
                }
                else{
                    Log.i("alerts", "null");
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(alertNotifService.this);
                    Map<String,?> sharedPrefsKeys = prefs.getAll();
                    for(String key : sharedPrefsKeys.keySet()){
                        Log.i("Printing key", key);
                        if(key.contains(".Alert")){
                            Log.i("Key", "Contains .Alert");
                        }
                    }
                }

                //Create/update notification if there is new data to notify the user about
                if(newAlerts.size() > 0){
                    Log.i("alertService", "New alerts available, notifying");
                    if(createNewNotif){
                        createNewNotification();
                    }
                    else{
                        createNotification();
                    }
                }
                else{
                    Log.i("alertService", "No new alerts");
                }

                //Kill the connection
                if(googleApiClient.isConnected()){
                    googleApiClient.disconnect();
                }
                clearData();
                stopSelf();

                //You know its working when this happens
                createTestNotif();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("DarkSky API", "Error while calling: " + retrofitError.getUrl());
                Log.i("DarkSky API", retrofitError.getMessage());
                //Kill the connection
                if(googleApiClient.isConnected()){
                    googleApiClient.disconnect();
                }
                NotificationCompat.Builder notifBuilder =
                        new NotificationCompat.Builder(alertNotifService.this)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("Error")
                                .setContentText("Problem pulling alerts.");
                notifBuilder.setAutoCancel(true);
                Intent serviceIntent = new Intent(alertNotifService.this, alertNotifService.class);
                PendingIntent servicePendingIntent = PendingIntent.getService(alertNotifService.this, 0, serviceIntent, 0);
                notifBuilder.setContentIntent(servicePendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(MainActivity.ALERT_NOTIF_ID);
                mNotificationManager.notify(MainActivity.ALERT_NOTIF_ID, notifBuilder.build());

                clearData();
                stopSelf();
            }
        });
    }

    //Notifications
    /**
     * Creates alert notification for Lollipop through Marshmallow.
     */
    private void createNotification(){
        //Determine which color to use for large icon background
        int setID = determineLayoutColor(todaySunrise, todaySunset);
        int color = -1;

        switch (setID){
            case 0:
                color = getResources().getColor(R.color.colorOrange);
                break;
            case 1:
                color = getResources().getColor(R.color.colorBlueLight);
                break;
            case 2:
                color = getResources().getColor(R.color.colorYellow);
                break;
            case 3:
                color = getResources().getColor(R.color.colorPurple);
                break;
        }

        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Weather alerts")
                        .setContentText("New weather statement for your area")
//                        .setLargeIcon(icon)
                        .setColor(color);
        Intent activityIntent = new Intent(this, IntroActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
        notifBuilder.setContentIntent(pendingIntent);
        notifBuilder.setAutoCancel(true);
        notifBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MainActivity.ALERT_NOTIF_ID, notifBuilder.build());
    }

    /**
     * Creates alert notification for Nougat.
     */
    private void createNewNotification(){
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Weather alerts")
                        .setContentText("New weather statement for your area");
        Intent activityIntent = new Intent(this, IntroActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
        notifBuilder.setContentIntent(pendingIntent);
        notifBuilder.setAutoCancel(true);
        notifBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MainActivity.ALERT_NOTIF_ID, notifBuilder.build());
    }

    /**
     * Creates test notification every time the alert service is updated.
     */
    private void createTestNotif(){
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_info_white)
                        .setContentTitle("Update")
                        .setContentText("Updated alert notification");
        notifBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notifBuilder.build());
    }

    //Time
    /**
     * Determines layout color based on current time, ID used to determine fog icon.
     * @return
     */
    private int determineLayoutColor(String sunriseTime, String sunsetTime){
        //TODO - HANDLE 24 HOUR TIME!
        //Compare currentTime UNIX timestamp to sunriseTime and sunsetTime UNIX timestamp
        String currentTime = getCurrentTime();
        Long currentTimeLong = Long.valueOf(currentTime);
        Long sunriseTimeLong = Long.valueOf(sunriseTime);
        Long sunsetTimeLong = Long.valueOf(sunsetTime);
        int setID = 4;
        Log.i("currentTime", currentTime);
        Log.i("sunriseTime", sunriseTime);
        Log.i("sunsetTime", sunsetTime);

        //If it is sunrise or sunset time
        if(currentTimeLong.equals(sunriseTimeLong)){
            setID = 0;
        }
        else if(currentTimeLong.equals(sunsetTimeLong)){
            setID = 2;
        }
        //If it is within 30 mins of sunrise or sunset time
        else if(currentTimeLong > (sunriseTimeLong - 1800) && currentTimeLong < (sunriseTimeLong + 1800)){
            setID = 0;
        }
        else if(currentTimeLong > (sunsetTimeLong - 1800) && currentTimeLong < (sunsetTimeLong + 1800)){
            setID = 2;
        }
        //If it is day time
        else if(currentTimeLong > (sunriseTimeLong + 1800) && currentTimeLong < (sunsetTimeLong - 1800)){
            setID = 1;
        }
        //If it is night time
        else if(currentTimeLong > (sunsetTimeLong + 1800)){
            setID = 3;
        }
        //If it is early morning time
        else{
            setID = 3;
        }
        return setID;
    }

    /**
     * Gets current UNIX timestamp.
     * @return
     */
    @NonNull
    private String getCurrentTime(){
        return String.valueOf(System.currentTimeMillis() /1000);
    }

    /**
     * Clears data stored in variables after notif update/creation.
     */
    private void clearData(){
        lastLocation = null;
//        todaySummary = null;
        latitude = 0.0;
        longitude = 0.0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(googleApiClient != null){
            if(googleApiClient.isConnected()){
                googleApiClient.disconnect();
            }
        }
    }
}