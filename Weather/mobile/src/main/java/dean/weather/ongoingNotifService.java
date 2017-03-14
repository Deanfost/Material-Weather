package dean.weather;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by DeanF on 12/11/2016.
 */

public class ongoingNotifService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Integer currentTemp = -1;
    private String currentIcon = "---";
    private String currentCondition = "---";
    private String currentHi = "---";
    private String currentLo = "---";
    private String sunriseTimeString = "---";
    private String sunsetTimeString = "---";
    private ArrayList<AlertsBlock> newAlerts = new ArrayList<>();

    //Address receiver
    protected Location lastLocation;//Location to pass to the address method
    private double latitude;
    private double longitude;

    //Location request
    LocationRequest locationRequest;

    //Google APIs
    private GoogleApiClient googleApiClient;
    private String currentAddress;
    private boolean hasLocation;

    //Units
    int units;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        Log.i("notifService", "Started");

        //Determine units
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString(getString(R.string.units_list_key), "0").equals("0")){
            //Set English units
            units = 0;
        }
        else{
            //Set Metric units
            units = 1;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Refresh data when a new intent is received
        if (intent != null) {
            if (intent.getExtras() != null) {
                if (intent.getExtras().getBoolean("pull", false)) {
                    Log.i("notifService", "new intent received, updating");
                    //Setup GoogleAPIClient
                    if (googleApiClient == null) {
                        googleApiClient = new GoogleApiClient.Builder(this)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();
                    }
                    googleApiClient.connect();
                    return START_STICKY;
                }
                //Intent specifies the service to stop
                else if (intent.getExtras().containsKey("notSticky")) {
                    Log.i("notifService", "notStickyReceived");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
                    notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ERROR_ID);
                    notificationManager.cancel(MainActivity.ALERT_NOTIF_ID);
                    stopSelf();
                    return START_NOT_STICKY;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
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
     * Uses geocoder object to retrieve addresses and localities from latitude and longitude.
     */
    private void getAddresses() {
        Boolean serviceAvailable = true;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("IO Exception", "getAdresses");
            serviceAvailable = false;
        }
        if (serviceAvailable) {
            if (addressList.size() > 0) {
                if (addressList.get(0).getLocality() != null) {
                    currentAddress = addressList.get(0).getLocality();//Assign locality if available
                    Log.i("getLocality", addressList.get(0).getLocality());
                } else {
                    currentAddress = addressList.get(0).getSubAdminArea();//Assign the county if there is no locality
                    Log.i("getSubAdminArea", addressList.get(0).getSubAdminArea());
                }
            } else {
                Log.i("getLocality", "No localities found.");
            }
        } else {
            Log.i("Geocoder", "Service unavailable.");
            currentAddress = "---";
        }
        if (currentAddress != null &&!currentAddress.equals("---")) {
            //Store the pulled location for future reference
            SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = mySPrefs.edit();
            editor.putString(getString(R.string.last_location_key), currentAddress);
            editor.apply();
            hasLocation = true;
        } else {
            hasLocation = false;
        }
    }

    /**
     * Starts location updates.
     */
    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }
    }

    /**
     * Stops location updates.
     */
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    //GoogleAPI
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
                                    //Get the current address
                                    getAddresses();

                                    //Pull initial data
                                    pullForecast();
                                } else {
                                    Log.i("notifService", "Unable to gather location");
                                    //Request for a location update, and execute rest of logic when a new location is returned
                                    startLocationUpdates();
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
                            editor.putBoolean(getString(R.string.ongoing_notif_key), false);
                            editor.apply();

                            //End the repeating preference alarm
                            Intent stopAlarm = new Intent(getApplicationContext(), alarmInterfaceService.class);
                            stopAlarm.putExtra("repeatNotif", false);
                            startService(stopAlarm);

                            //Cancel notifs
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
                            notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ERROR_ID);

                            //Notify the user
//                            Toast.makeText(ongoingNotifService.this, "Please enable location services to use this service", Toast.LENGTH_LONG).show();

                            stopSelf();
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Check for airplane mode
                            if(Settings.System.getInt(ongoingNotifService.this.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0){
                                //Airplane mode is on, do nothing
                                Log.i("ongoingNotifService", "Airplane mode on");
                                if (googleApiClient.isConnected()) {
                                    googleApiClient.disconnect();
                                }
                            }
                            else{
                                //Airplane mode is not on
                                Log.i("notifService", "Location settings not satisfied");
                                if (googleApiClient.isConnected()) {
                                    googleApiClient.disconnect();
                                }
                                //Change the preference to reflect the service being disabled
                                SharedPreferences mySPrefs1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor1 = mySPrefs1.edit();
                                editor1.putBoolean(getString(R.string.ongoing_notif_key), false);
                                editor1.apply();

                                //End the repeating preference alarm
                                Intent stopAlarm1 = new Intent(getApplicationContext(), alarmInterfaceService.class);
                                stopAlarm1.putExtra("repeatNotif", false);
                                startService(stopAlarm1);

                                //Cancel notifs
                                NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager1.cancel(MainActivity.FOLLOW_NOTIF_ID);
                                notificationManager1.cancel(MainActivity.FOLLOW_NOTIF_ERROR_ID);

                                //Notify the user
//                                Toast.makeText(ongoingNotifService.this, "Pelase enable location services to use this service", Toast.LENGTH_LONG).show();

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
            //Change the preferences to reflect the service being disabled
            SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = mySPrefs.edit();
            editor.putBoolean(getString(R.string.ongoing_notif_key), false);
            editor.apply();

            //End the repeating preference alarm
            Intent stopAlarm = new Intent(this, alarmInterfaceService.class);
            stopAlarm.putExtra("repeatNotif", false);
            startService(stopAlarm);

            //Cancel notifs
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
            notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ERROR_ID);

            //Notify the user
            Toast.makeText(this, "Please grant location permission to use this service", Toast.LENGTH_LONG).show();

            stopSelf();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("notifService", "API connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("notifService", "GoogleAPI connection failed");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //Create notification for Lollipop through Marshmallow
            createNotification(false);
        }
        else if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            //Create notification for Nougat and above
            createNewNotification(false);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("ongoingNotif", "New location pulled");
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

        //Stop pulling location updates
        stopLocationUpdates();

        //Get the current address
        getAddresses();

        //Pull initial data
        pullForecast();
    }

    //Notification

    /**
     * Creates notification with current weather data.
     */
    private void createNotification(boolean successful) {
        if (successful) {
            //Create the weather notification
            int iconID;
            RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_older);
            //Set icon
            switch (currentIcon) {
                case "clear-day":
                    notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_sunny_white);
                    iconID = R.drawable.ic_sunny_white;
                    break;
                case "clear-night":
                    notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_clear_night_white);
                    iconID = R.drawable.ic_clear_night_white;
                    break;
                case "rain":
                    notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_rain_white);
                    iconID = R.drawable.ic_rain_white;
                    break;
                case "snow":
                    notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_snow_white);
                    iconID = R.drawable.ic_snow_white;
                    break;
                case "sleet":
                    notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_sleet_white);
                    iconID = R.drawable.ic_sleet_white;
                    break;
                case "wind":
                    notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_windrose_white);
                    iconID = R.drawable.ic_windrose_white;
                    break;
                case "fog":
                    //If it is daytime
                    if (determineLayoutColor(sunriseTimeString, sunsetTimeString) != 3) {
                        notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_foggyday_white);
                        iconID = R.drawable.ic_foggyday_white;
                    } else {
                        notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_foggynight_white);
                        iconID = R.drawable.ic_foggynight_white;
                    }
                    break;
                case "cloudy":
                    notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_cloudy_white);
                    iconID = R.drawable.ic_cloudy_white;
                    break;
                case "partly-cloudy-day":
                    notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_partlycloudy_white);
                    iconID = R.drawable.ic_partlycloudy_white;

                    break;
                case "partly-cloudy-night":
                    notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_partlycloudynight_white);
                    iconID = R.drawable.ic_partlycloudynight_white;
                    break;
                default:
                    notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_cloudy_white);
                    iconID = R.drawable.ic_cloudy_white;
                    Log.i("CurrentConditions", "Unsupported condition.");
                    break;
            }
            //Set temp and condition
            String currentTempString;
            if(currentTemp != -1){
                currentTempString = currentTemp.toString();
            }
            else{
                currentTempString = "---";
            }

            notificationView.setTextViewText(R.id.notifCondition, currentTempString + "° - " + currentCondition);
            //Set location
            if (hasLocation) {
                if(! currentAddress.equals("null")){
                    notificationView.setTextViewText(R.id.notifLocation, currentAddress);
                }
                else{
                    notificationView.setTextViewText(R.id.notifLocation, "---");
                }
            } else {
                SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String savedLocation = mySPrefs.getString(getString(R.string.last_location_key), "---");
                notificationView.setTextViewText(R.id.notifLocation, savedLocation);
            }

            //Set high and low
            notificationView.setTextViewText(R.id.notifBody, "Hi - " + currentHi + "° Lo - " + currentLo + "°");
            //Build the notification
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setContent(notificationView)
                            .setSmallIcon(iconID);
            //Intent to go to main activity
            Intent mainIntent = new Intent(this, IntroActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);
            notificationBuilder.setContentIntent(resultPendingIntent);
            notificationBuilder.setOngoing(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
            notificationManager.notify(MainActivity.FOLLOW_NOTIF_ID, notificationBuilder.build());

            //You know its working when this happens
            createOngoingTestNotif();

        } else {
            //Create notification asking the user to try again
            NotificationCompat.Builder notifBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Unable to sync weather")
                            .setContentText("Tap to try again now.");
            Intent serviceIntent = new Intent(this, ongoingNotifService.class);
            PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
            notifBuilder.setContentIntent(servicePendingIntent);
            notifBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(MainActivity.FOLLOW_NOTIF_ERROR_ID);
            mNotificationManager.notify(MainActivity.FOLLOW_NOTIF_ERROR_ID, notifBuilder.build());

            if (googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
        }
    }

    /**
     * Creates a notification for Android Nougat notification style.
     * @param successful
     */
    private void createNewNotification(boolean successful) {
        if (successful) {
            //Create the weather notification
            int iconID;
            //Set icon
            switch (currentIcon) {
                case "clear-day":
                    iconID = R.drawable.ic_sunny_white;
                    break;
                case "clear-night":
                    iconID = R.drawable.ic_clear_night_white;
                    break;
                case "rain":
                    iconID = R.drawable.ic_rain_white;
                    break;
                case "snow":
                    iconID = R.drawable.ic_snow_white;
                    break;
                case "sleet":
                    iconID = R.drawable.ic_sleet_white;
                    break;
                case "wind":
                    iconID = R.drawable.ic_windrose_white;
                    break;
                case "fog":
                    //If it is daytime
                    if (determineLayoutColor(sunriseTimeString, sunsetTimeString) != 3) {
                        iconID = R.drawable.ic_foggyday_white;
                    } else {
                        iconID = R.drawable.ic_foggynight_white;
                    }
                    break;
                case "cloudy":
                    iconID = R.drawable.ic_cloudy_white;
                    break;
                case "partly-cloudy-day":
                    iconID = R.drawable.ic_partlycloudy_white;
                    break;
                case "partly-cloudy-night":
                    iconID = R.drawable.ic_partlycloudynight_white;
                    break;
                default:
                    iconID = R.drawable.ic_sunny_white;
                    Log.i("CurrentConditions", "Unsupported condition.");
                    break;
            }
            String currentTempString;
            if(currentTemp != -1){
                currentTempString = currentTemp.toString();
            }
            else{
                currentTempString = "---";
            }

            String localCurrentAddress;
            if(hasLocation){
                if(!currentAddress.equals("null")){
                    localCurrentAddress = currentAddress;
                }
                else{
                    localCurrentAddress = "---";
                }
            }
            else{
                localCurrentAddress = "---";
            }

            //Build the notification
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setContentTitle(currentTempString + "° - " + currentCondition)
                            .setContentTitle(currentHi + "°/" + currentLo + "° · " + localCurrentAddress)
                            .setSmallIcon(iconID);
            //Intent to go to main activity
            Intent mainIntent = new Intent(this, IntroActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);
            notificationBuilder.setContentIntent(resultPendingIntent);
            notificationBuilder.setOngoing(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
            notificationManager.notify(MainActivity.FOLLOW_NOTIF_ID, notificationBuilder.build());

        } else {
            //Create notification asking the user to try again
            NotificationCompat.Builder notifBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Unable to sync weather")
                            .setContentText("Tap to try again now.");
            Intent serviceIntent = new Intent(this, ongoingNotifService.class);
            serviceIntent.putExtra("pull", true);
            PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
            notifBuilder.setContentIntent(servicePendingIntent);
            notifBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(MainActivity.FOLLOW_NOTIF_ERROR_ID);
            mNotificationManager.notify(MainActivity.FOLLOW_NOTIF_ERROR_ID, notifBuilder.build());
        }

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Creates alert notification for Lollipop through Marshmallow.
     */
    private void createAlertNotification(){
        //Determine which color to use for large icon background
        int setID = determineLayoutColor(sunriseTimeString, sunsetTimeString);
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
    private void createNewAlertNotification(){
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
     * Creates test notification to go along with actual ongoing notif.
     */
    private void createOngoingTestNotif(){
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_check_color)
                        .setContentTitle("Update")
                        .setContentText("Updated weather notification");
        notifBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
        mNotificationManager.notify(1, notifBuilder.build());
    }

    /**
     * Creates test notification every time the alert service is updated.
     */
    private void createAlertTestNotif(){
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_info_white)
                        .setContentTitle("Update")
                        .setContentText("Updated alert notification");
        notifBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notifBuilder.build());
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
        request.addExcludeBlock(Request.Block.HOURLY);
        request.addExcludeBlock(Request.Block.FLAGS);
        request.addExcludeBlock(Request.Block.MINUTELY);
        request.setLat(String.valueOf(latitude));
        request.setLng(String.valueOf(longitude));
        request.setLanguage(Request.Language.ENGLISH);

        //Units
        if(units == 0){
            request.setUnits(Request.Units.US);
        }
        else{
            request.setUnits(Request.Units.UK);
        }

        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                Log.i("DarkSky API", "Pull request successful");
                //Pull and parse data for ongoing notif if enabled
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ongoingNotifService.this);

                //Parse sunrise and sunset times to determine current layout color
                //Parse sunrise time
                if(weatherResponse.getDaily().getData().get(0).getSunriseTime() != null){//UNIX timestamp
                    sunriseTimeString = weatherResponse.getDaily().getData().get(0).getSunriseTime();
                    Log.i("sunriseTimeUNIX", sunriseTimeString);
                }
                else{
                    sunriseTimeString = "---";
                    Log.i("sunriseTimeUNIX", "---");
                }

                //Parse sunset time
                if(weatherResponse.getDaily().getData().get(0).getSunsetTime() != null){//UNIX timestamp
                    sunsetTimeString = weatherResponse.getDaily().getData().get(0).getSunsetTime();
                    Log.i("sunsetTimeUNIX", sunsetTimeString);
                }
                else{
                    sunsetTimeString = "---";
                    Log.i("sunsetTimeUNIX", "---");
                }

                //Parse ongoing notif data if enabled
                if(preferences.getBoolean(getResources().getString(R.string.ongoing_notif_key), false)){
                    //Parse currentTemp
                    Double tempDouble = weatherResponse.getCurrently().getTemperature();
                    if(tempDouble != null)
                        currentTemp = tempDouble.intValue();
                    else
                        currentTemp = -1;

                    //Set condition icon and condition statement
                    if(weatherResponse.getCurrently().getIcon() != null){
                        currentIcon = weatherResponse.getCurrently().getIcon();
                    }
                    else{
                        currentIcon = "---";
                    }
                    Log.i("currentIcon", currentIcon);
                    switch (currentIcon){
                        case "clear-day":
                            currentCondition = "Clear";
                            break;
                        case "clear-night":
                            currentCondition = "Clear";
                            break;
                        case "rain":
                            currentCondition = "Rain";
                            break;
                        case "snow":
                            currentCondition = "Snow";
                            break;
                        case "sleet":
                            currentCondition = "Sleet";
                            break;
                        case "wind":
                            currentCondition = "Windy";
                            break;
                        case "fog":
                            currentCondition = "Foggy";
                            break;
                        case "cloudy":
                            currentCondition = "Cloudy";
                            break;
                        case "partly-cloudy-day":
                            currentCondition = "Partly Cloudy";
                            break;
                        case "partly-cloudy-night":
                            currentCondition = "Partly Cloudy";
                            break;
                        default:
                            currentCondition = "---";
                            Log.i("CurrentConditions", "Unsupported condition.");
                            break;
                    }

                    //Parse HI/LO
                    Double HiDouble;
                    Double LoDouble;
                    HiDouble = weatherResponse.getDaily().getData().get(0).getTemperatureMax();
                    LoDouble = weatherResponse.getDaily().getData().get(0).getTemperatureMin();
                    if(HiDouble != null){
                        currentHi = String.valueOf(HiDouble.intValue());
                        Log.i("HI", currentHi);
                    }
                    else{
                        currentHi = "---";
                        Log.i("HI", "---");
                    }

                    if(LoDouble != null){
                        currentLo = String.valueOf(LoDouble.intValue());
                        Log.i("LO", currentLo);
                    }
                    else{
                        currentLo = "---";
                        Log.i("LO", "---");
                    }

                    //Create/update notification for ongoing
                    //Test to see which one to make
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        //Create notification for Lollipop through Marshmallow
                        createNotification(true);
                    }
                    else if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                        //Create notification for Nougat and above
                        createNewNotification(true);
                    }
                }

                //Pull and parse data for the alert notif if enabled
                if(preferences.getBoolean(getResources().getString(R.string.alert_notif_key), false)){
                    if(weatherResponse.getAlerts() != null){
                        Log.i("alerts", weatherResponse.getAlerts().size() + "");
                        for(int i = 0; i < weatherResponse.getAlerts().size(); i++){
                            Log.i("Alert", weatherResponse.getAlerts().get(i).getTitle());
                            Log.i("Alert", weatherResponse.getAlerts().get(i).getDescription());
                        }

                        //Check to see if these are new alerts
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ongoingNotifService.this);
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
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ongoingNotifService.this);
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
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            //Create notification for Lollipop through Marshmallow
                            createAlertNotification();
                        }
                        else if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                            //Create notification for Nougat and above
                            createNewAlertNotification();
                        }
                    }
                    else{
                        Log.i("alertService", "No new alerts");
                    }

                    //Its working!
                    createAlertTestNotif();
                }

                //Kill the connection
                if(googleApiClient.isConnected()){
                    googleApiClient.disconnect();
                }
                clearData();
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
                        new NotificationCompat.Builder(ongoingNotifService.this)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("Error")
                                .setContentText("Problem accessing Dark ");
                notifBuilder.setAutoCancel(true);
                Intent serviceIntent = new Intent(ongoingNotifService.this, ongoingNotifService.class);
                PendingIntent servicePendingIntent = PendingIntent.getService(ongoingNotifService.this, 0, serviceIntent, 0);
                notifBuilder.setContentIntent(servicePendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(MainActivity.FOLLOW_NOTIF_ERROR_ID);
                mNotificationManager.notify(MainActivity.FOLLOW_NOTIF_ERROR_ID, notifBuilder.build());
                clearData();
            }
        });
    }

    //Time
    /**
     * Determines layout color based on current time, ID used to determine fog icon.
     * @return
     */
    private int determineLayoutColor(String sunriseTime, String sunsetTime){
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
        latitude = 0;
        longitude = 0;
        locationRequest = null;
        currentTemp = null;
        currentIcon = null;
        currentCondition = null;
        currentHi = null;
        currentLo = null;
        sunriseTimeString = null;
        sunsetTimeString = null;
    }


}