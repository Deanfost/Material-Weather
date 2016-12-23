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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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
 * Created by DeanF on 12/11/2016.
 */

public class notificationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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

    private Integer currentTemp;
    private String currentIcon;
    private String currentCondition;
    private String currentHi;
    private String currentLo;
    private String sunriseTimeString;
    private String sunsetTimeString;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Refresh data when a new intent is received
        Log.i("notifService", "new intent received, updating");
        googleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
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
                    final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

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
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Location settings aren't satisfied, but there is no way to fix them. Do not show dialog.
                            Log.i("notifService", "Location settings not satisfied");
                            if (googleApiClient.isConnected()) {
                                googleApiClient.disconnect();
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
        }
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
        if (!currentAddress.equals("---")) {
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

    @Override
    public void onConnectionSuspended(int i) {
        //TODO - GET RID OF THIS AFTER TESTING
        Log.i("notifService", "API connection suspended");
        createNotification(false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("notifService", "GoogleAPI connection suspended");
        createNotification(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("notifService", "Location changed, updating notification");
        //Reset the connection, and pull data
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        googleApiClient.connect();
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
            notificationView.setTextViewText(R.id.notifCondition, currentTemp.toString() + "° - " + currentCondition);
            //Set location
            if (hasLocation) {
                notificationView.setTextViewText(R.id.notifLocation, currentAddress);
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
            Intent mainIntent = new Intent(this, notificationIntentHandler.class);
            PendingIntent resultPendingIntent = PendingIntent.getService(this, 0, mainIntent, 0);
            notificationBuilder.setContentIntent(resultPendingIntent);
            notificationBuilder.setOngoing(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MainActivity.NOTIF_ID, notificationBuilder.build());
        } else {
            //Create notification asking the user to try again
            NotificationCompat.Builder notifBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Unable to sync weather")
                            .setContentText("Tap to try again.");
            Intent serviceIntent = new Intent(this, notificationService.class);
            PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
            notifBuilder.setContentIntent(servicePendingIntent);
            notifBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(MainActivity.NOTIF_ID, notifBuilder.build());

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
                    iconID = R.drawable.ic_cloudy_white;
                    Log.i("CurrentConditions", "Unsupported condition.");
                    break;
            }
            //Build the notification
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setContentTitle(currentTemp.toString() + "° - " + currentCondition)
                            .setContentTitle(currentHi + "°/" + currentLo + "° · " + currentAddress)
                            .setSmallIcon(iconID);
            //Intent to go to main activity
            Intent mainIntent = new Intent(this, notificationIntentHandler.class);
            PendingIntent resultPendingIntent = PendingIntent.getService(this, 0, mainIntent, 0);
            notificationBuilder.setContentIntent(resultPendingIntent);
            notificationBuilder.setOngoing(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MainActivity.NOTIF_ID, notificationBuilder.build());
        } else {
            //Create notification asking the user to try again
            NotificationCompat.Builder notifBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Unable to sync weather")
                            .setContentText("Tap to try again.");
            Intent serviceIntent = new Intent(this, notificationService.class);
            PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
            notifBuilder.setContentIntent(servicePendingIntent);
            notifBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(MainActivity.NOTIF_ID, notifBuilder.build());
        }

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    //Dark Sky

    /**
     * Initializes API Wrapper Lib, and forms pull request to receive weather data.
     */
    private void pullForecast() {
        Log.i("forecastRequest", "pullingForecast");
        //TODO - CHECK FOR THE UNITS AND STUFF, AS WELL AS WHEN THE PREFERENCES CHANGE
        //Get the Dark Sky Wrapper API ready
        ForecastApi.create("331ebe65d3032e48b3c603c113435992");

        //Setup location change requests
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }

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
                //Pull and parse data
                //Parse currentTemp
                Double tempDouble = weatherResponse.getCurrently().getTemperature();
                currentTemp = tempDouble.intValue();

                //Set condition icon and condition statement
                currentIcon = weatherResponse.getCurrently().getIcon();
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
                        currentCondition = "Clear";
                        Log.i("CurrentConditions", "Unsupported condition.");
                        break;
                }

                //Parse HI/LO
                Double HiDouble;
                Double LoDouble;
                HiDouble = weatherResponse.getDaily().getData().get(0).getTemperatureMax();
                LoDouble = weatherResponse.getDaily().getData().get(0).getTemperatureMin();
                currentHi = String.valueOf(HiDouble.intValue());
                currentLo = String.valueOf(LoDouble.intValue());
                Log.i("HI", currentHi);
                Log.i("LO", currentLo);

                //Parse sunrise and sunset times to determine current layout color
                //Parse sunrise time
                sunriseTimeString = weatherResponse.getDaily().getData().get(0).getSunriseTime();//UNIX timestamp
                Log.i("sunriseTimeUNIX", sunriseTimeString);

                //Parse sunset time
                sunsetTimeString = weatherResponse.getDaily().getData().get(0).getSunsetTime();//UNIX timestamp
                Log.i("sunsetTimeUNIX", sunsetTimeString);

                //Create/update notification
                //Test to ses which one to make
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    //Create notification for Lollipop through Marshmallow
                    createNotification(true);
                }
                else if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    //Create notification for Nougat and above
                    createNewNotification(true);
                }

                //Kill the connection
                if(googleApiClient.isConnected()){
                    googleApiClient.disconnect();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("DarkSky API", "Error while calling: " + retrofitError.getUrl());
                Log.i("DarkSky API", retrofitError.getMessage());
            }
        });
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
}