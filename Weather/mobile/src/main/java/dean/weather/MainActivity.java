package dean.weather;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.FirebaseApp;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.AlertsBlock;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@Keep
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, NoConnectionFragment.connectionRefresher,
        LocationUnavailableFragment.dataFetcher, ProviderInstaller.ProviderInstallListener{

    //Layout
    Toolbar toolbar;
    LinearLayout mainActivityLayout;

    //Location
    final int REQUEST_CHANGE_SETTINGS = 15;

    //Address receiver
    protected Location lastLocation;//Location to pass to the address method

    //Google APIs
    public static GoogleApiClient googleApiClient;
    public double latitude;
    public double longitude;

    //WeatherResponse
    public static WeatherResponse pulledWeatherResponse;

    //Hourly
    public List<Integer> pulledComparisonHours = new ArrayList<>();//Used for hourly comparison in hourly adapter
    public List<String> pulledHours = new ArrayList<>();
    public List<String> pulledIcons = new ArrayList<>();
    public List<Integer> pulledTemps = new ArrayList<>();
    public List<Integer> pulledWinds = new ArrayList<>();

    //Daily
    private List<String> pulledDays = new ArrayList<>();
    private List<String> pulledDailyCond = new ArrayList<>();
    private List<Integer> pulledHIs = new ArrayList<>();
    private List<Integer> pulledLOs = new ArrayList<>();
    private List<Integer> pulledPrecips = new ArrayList<>();

    //Current
    public static String currentLocation = "---";
    private String currentDay = "---";
    private String currentDate = "---";
    private String currentIcon = "---";
    private int currentTemp = -1;
    private String currentConditions = "---";
    private String todaysHI = "---";
    private String todaysLO = "---";
    private String todaysHILO = "---";
    private String todaysMinutely = "---";
    private String currentWind = "---";
    private int currentPrecip = -1;
    private int currentHumidity = -1;
    private int currentDewpoint = -1;
    private int currentPressure = -1;
    private String currentVisibilty = "---";
    private int currentCloudCover;
    private String sunriseTime = "---";
    private String sunsetTime = "---";
    private String updateTime = "---";
    public static int setID = -1;
    private int alertsCount = 0;

    //Alerts
    private ArrayList<AlertsBlock> newAlerts = new ArrayList<>();
    Snackbar alertSnackbar;

    //Units
    int units;
    int hourFormat;

    //Theme
    boolean changeTheme;

    //Notification
    public static final int FOLLOW_NOTIF_ID = 23;
    public static final int ALERT_NOTIF_ID = 32;
    public static final int FOLLOW_NOTIF_ERROR_ID = 33;

    //App bar buttons
    public static boolean enableRefresh = true;
    public static boolean enableAlerts = true;

    //Activity state
    public static boolean isRunning = true;

    //Fragments - if these are true, defer the transactions to onResumeFragments()
    private boolean mainPending = false;
    private boolean loadingPending = false;
    private boolean noConnectionPending = false;
    private boolean locationUnavPending = false;

    //Security updates
    private static final int ERROR_DIALOG_REQUEST_CODE = 1;
    private boolean mRetryProviderInstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate", "started");
        //Firebase
        FirebaseApp.initializeApp(this);

        //Set content view
        setContentView(R.layout.activity_main);

        //Create an instance of GoogleAPIClient
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.i("GoogleAPIClient", "Creating new instance");
        }

        //Customize toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Customize the app bar
        assert toolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBlue)));

        //Reference to mainLayout
        mainActivityLayout = (LinearLayout) findViewById(R.id.mainActivityLayout);

        //Initialize loading fragment at start
        loadingFragmentTransaction();

        //Set default layout color(blue)
        setID = 1;
        setMainLayoutColor(1);

//      //Connect to the Google API
//      googleApiClient.connect();

        //Determine if the security provider is up to date
        ProviderInstaller.installIfNeededAsync(this, this);
    }

    //Security provider callbacks
    @Override
    public void onProviderInstalled() {
        Log.i("Main", "Provider is up to date");
        googleApiClient.connect();
    }

    @Override
    public void onProviderInstallFailed(int i, Intent intent) {
        if (GooglePlayServicesUtil.isUserRecoverableError(i)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            GooglePlayServicesUtil.showErrorDialogFragment(i, this, ERROR_DIALOG_REQUEST_CODE, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // The user chose not to take the recovery action
                            onProviderInstallerNotAvailable();
                        }
                    });
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();
        }
    }

    private void onProviderInstallerNotAvailable(){
        Log.i("Main", "Unable to update the security provider");
        googleApiClient.connect();
    }

    //Action bar events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Settings
            case R.id.action_settings:
                    //Open the settings activity
                    Intent settingsIntent = new Intent(this, settingsActivity.class);
                    startActivity(settingsIntent);
                return true;
            //Refresh data
            case R.id.action_refresh:
                if(enableRefresh){
                    refresh();
                }
                else{
                    Log.i("Refresh", "Disabled");
                }
                return true;
            case R.id.action_alerts:
                if(enableAlerts){
                    Log.i("Alerts", "Enabled");
                    //Dismiss the alert snackbar
                    if(alertSnackbar != null){
                        alertSnackbar.dismiss();
                    }

                    //Move to the alerts activity
                    Intent alertsIntent = new Intent(this, AlertsActivity.class);
                    alertsIntent.putExtra("setID", setID);
                    alertsIntent.putExtra("conditionsIcon", currentIcon);
                    startActivity(alertsIntent);
                }
                else{
                    Log.i("Alerts", "Disabled");
                }
                return true;
            //User action not recognized
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_items, menu);

        //Customize black menu option icons to be white
        Drawable icSettings = menu.findItem(R.id.action_settings).getIcon();
        icSettings = DrawableCompat.wrap(icSettings);
        DrawableCompat.setTint(icSettings, getResources().getColor(R.color.colorWhite));

        Drawable icRefresh = menu.findItem(R.id.action_refresh).getIcon();
        icRefresh = DrawableCompat.wrap(icRefresh);
        DrawableCompat.setTint(icRefresh, getResources().getColor(R.color.colorWhite));

        return true;
    }

    //Google API Client events
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Enable theme changes
        if(preferences.getBoolean(getString(R.string.theme_change_key), true)){
            changeTheme = true;
            Log.i("changeTheme", "True");
        }
        else{
            changeTheme = false;
            Log.i("changeTheme", "False");
            setID = 1;
        }

        //Get current location
        Log.i("GoogleAPI", "Connected");
        clearDataSets();
        requestLocationAndData();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("GoogleAPI", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Show fragment to show the user that the connection failed
        Log.i("GoogleAPI", "Failed");
        noConnectionFragmentTransaction();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Main activity", "New location pulled");
        lastLocation = location;
        //Get latitude and longitude for DarkSky API
        latitude = lastLocation.getLatitude();
        longitude = lastLocation.getLongitude();
//        Log.i("Latitude", String.valueOf(latitude));
//        Log.i("Longitude", String.valueOf(longitude));
        //Determine if a geocoder is available
        if(!Geocoder.isPresent()){
            Log.i("Geocoder", "Unavailable");
        }
        //Parse and format data not related to weather
        retrieveAndFormatNonWeatherData();

        //Stop location updates
        stopLocationUpdates();

        //Call DarkSkyAPI
        pullForecast();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check the result of the location settings change request
        if(requestCode == REQUEST_CHANGE_SETTINGS){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made, so request location
                    try{
                        requestLocationAndData();
                    }
                    catch (SecurityException e){
                        Log.e("LocationPermission", "Permission denied");
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change location settings, but chose not to, show activity
                    Log.i("Main", "Moving to change location settings activity");
                    googleApiClient.disconnect();
                    Intent changeSettings = new Intent(MainActivity.this, ChangeLocationSettingsActivity.class);
                    startActivity(changeSettings);
                    ChangeLocationSettingsActivity.passContext(MainActivity.this);
                    break;
                default:
                    break;
            }
        }
        //Check the result of the security update
        else if(requestCode == ERROR_DIALOG_REQUEST_CODE){
            mRetryProviderInstall = true;
        }
    }

    //Location
    /**
     * Creates location request.
     */
    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);//2 seconds (request for an immediate location update)
        locationRequest.setFastestInterval(1000);//1 second
        //City block accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    /**
     * Uses geocoder object to retrieve addresses and localities from latitude and longitude.
     */
    private void getAddresses(){
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
        if(serviceAvailable){
            if (addressList.size() > 0) {
                if(addressList.get(0).getLocality()!= null){
                    currentLocation = addressList.get(0).getLocality();//Assign locality if available
//                    Log.i("getLocality", addressList.get(0).getLocality());

                    //Save the last location for future reference by the ongoing notification
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.last_location_key), currentLocation);
                    editor.apply();
                }
                else if(addressList.get(0).getSubAdminArea() != null){
                    currentLocation = addressList.get(0).getSubAdminArea();//Assign the county if there is no locality
//                    Log.i("getSubAdminArea", addressList.get(0).getSubAdminArea());

                    //Save the last location for future reference by the ongoing notification
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.last_location_key), currentLocation);
                    editor.apply();
                }
                else{
                    currentLocation = "---";
                }
            }
            else{
                Log.i("getLocality", "No localities found.");
                currentLocation = "---";
            }
        }
        else{
            Log.i("Geocoder", "Service unavailable.");
            currentLocation = "---";
        }
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

    /**
     * Checks for location permissions, location settings, and pulls location from Google Location API.
     */
    private void requestLocationAndData(){
        //Make sure the Google API client is connected
        if(googleApiClient.isConnected()) {
            Log.i("RequestData", "called");
            int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if(locationPermissionCheck == PackageManager.PERMISSION_GRANTED){
                Log.i("Permission", "Location access granted");
                //Create location settings request
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(createLocationRequest());

                //Create location request to make sure the request is permitted
                PendingResult<LocationSettingsResult> locationSettingsResultPendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

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
                                        //Get latitude and longitude for DarkSky API
                                        latitude = lastLocation.getLatitude();
                                        longitude = lastLocation.getLongitude();
//                                        Log.i("Latitude", String.valueOf(latitude));
//                                        Log.i("Longitude", String.valueOf(longitude));
                                        //Determine if a geocoder is available
                                        if(!Geocoder.isPresent()){
                                            Log.i("Geocoder", "Unavailable");
                                        }
                                        //Get and parse data for mainFragment
                                        //Parse and format data not related to weather
                                        retrieveAndFormatNonWeatherData();

                                        //Call DarkSkyAPI
                                        pullForecast();
                                    }
                                    else{
                                        //Pull a location update
                                        Log.i("mainActivity", "Last location unavailable");
                                        if(googleApiClient.isConnected()){
                                            startLocationUpdates();

                                            //Setup a timeout
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //Stop the location updates after 20 seconds, show the user the location unavailable fragment
                                                    if(googleApiClient.isConnected()){
                                                        stopLocationUpdates();
                                                        googleApiClient.disconnect();
                                                    }
                                                    //Only show this fragment if the updates didn't work
                                                    if(lastLocation == null)
                                                        locationUnavailableFragmentTransaction();
                                                }
                                            }, 20000);
                                        }
                                    }
                                }
                                catch (SecurityException e){
                                    Log.e("LocationPermission", "Permission denied");
                                }
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied, but this can be fixed
                                // by showing the user a dialog.
                                try {
                                    locationStatus.startResolutionForResult(MainActivity.this, REQUEST_CHANGE_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                //Check for airplane mode
                                if(Settings.System.getInt(MainActivity.this.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0){
                                    //Airplane mode is on, show activity
                                    Log.i("Main", "Moving to airplane mode activity");
                                    googleApiClient.disconnect();
                                    Intent airplaneMode = new Intent(MainActivity.this, AirplaneModeActivity.class);
                                    startActivity(airplaneMode);
                                    AirplaneModeActivity.passContext(MainActivity.this);
                                }
                                else{
                                    //Airplane mode is off, show activity
                                    Log.i("Main", "Settings change unavailable");
                                    googleApiClient.disconnect();
                                    Intent locationSettingsUnav = new Intent(MainActivity.this, LocationSettingsNoRes.class);
                                    startActivity(locationSettingsUnav);
                                }
                                break;
                        }
                    }
                });
            }
            else{
                //Tell the user to grant location permissions
                Log.i("Main", "Moving to permissions activity");
                googleApiClient.disconnect();
                Intent permissionsActivity = new Intent(MainActivity.this, PermissionsActivity.class);
                startActivity(permissionsActivity);
                PermissionsActivity.passContext(MainActivity.this);
            }
        }
        //If there is no connection to the Google API client
        else{
            Log.i("requestLocAndData", "No connection to Google API client");
            noConnectionFragmentTransaction();
        }
    }

    //Layout customizations and updates

    /**
     * Determines layout color based on current time.
     * @return
     */
    private int determineLayoutColor(String sunriseTime, String sunsetTime){
        //TODO - HANDLE 24 HOUR TIME!
        //Compare currentTime UNIX timestamp to sunriseTime and sunsetTime UNIX timestamp
        String currentTime = getCurrentTime();
        Long currentTimeLong = Long.valueOf(currentTime);
        Long sunriseTimeLong = Long.valueOf(sunriseTime);
        Long sunsetTimeLong = Long.valueOf(sunsetTime);
        int innerSetID;
        Log.i("currentTime", currentTime);
        Log.i("sunriseTime", sunriseTime);
        Log.i("sunsetTime", sunsetTime);

        //If it is sunrise or sunset time
        if(currentTimeLong.equals(sunriseTimeLong)){
            innerSetID = 0;
        }
        else if(currentTimeLong.equals(sunsetTimeLong)){
            innerSetID = 2;
        }
        //If it is within 30 mins of sunrise or sunset time
        else if(currentTimeLong > (sunriseTimeLong - 1800) && currentTimeLong < (sunriseTimeLong + 1800)){
            innerSetID = 0;
        }
        else if(currentTimeLong > (sunsetTimeLong - 1800) && currentTimeLong < (sunsetTimeLong + 1800)){
            innerSetID = 2;
        }
        //If it is day time
        else if(currentTimeLong > (sunriseTimeLong + 1800) && currentTimeLong < (sunsetTimeLong - 1800)){
            innerSetID = 1;
        }
        //If it is night time
        else if(currentTimeLong > (sunsetTimeLong + 1800)){
            innerSetID = 3;
        }
        //If it is early morning time
        else{
            innerSetID = 3;
        }
        return innerSetID;
    }

    /**
     * Customizes main layout colors.
     * @param colorSet
     */
    private void setMainLayoutColor(int colorSet){
        //Setup resources to changeTheme
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        switch (colorSet){
            //Sunrise
            case 0:
                //Set color of header in task tray
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorYellowDark)));
                icon = null;

                //Set color of system bar
                window.setStatusBarColor(this.getResources().getColor(R.color.colorYellowDark));

                //Customize app bar
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOrangeDark)));

                window = null;
                break;

            //Daytime
            case 1:
                //Set color of header in task tray
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorBlueDark)));
                icon = null;

                //Set color of system bar
                window.setStatusBarColor(this.getResources().getColor(R.color.colorBlueDark));

                //Customize app bar
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBlueDark)));

                window = null;
                break;
            //Sunset
            case 2:
                //Set color of header in task tray
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorOrangeDark)));
                icon = null;

                //Set color of system bar
                window.setStatusBarColor(this.getResources().getColor(R.color.colorOrangeDark));

                //Customize app bar
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOrangeDark)));

                window = null;
                break;
            //Nighttime
            case 3:
                //Set color of header in task tray
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorPurpleDark)));
                icon = null;

                //Set color of system bar
                window.setStatusBarColor(this.getResources().getColor(R.color.colorPurpleDark));

                //Customize app bar
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPurpleDark)));

                window = null;
                break;
            default:
                Log.i("SetLayoutColor", "defaultCondition");
        }
    }

    //Dark Sky API
    /**
     * Initializes API Wrapper Lib, forms pull request for weather data, and parses and formats it, and passes it to MainFragment.
     */
    private void pullForecast(){
        Log.i("forecastRequest", "pullingForecast");
        //Get the Dark Sky Wrapper API ready
        ForecastApi.create(getResources().getString(R.string.api_key));

        //Determine which units to use
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Form a pull request
        final RequestBuilder weather = new RequestBuilder();
        final Request request = new Request();
        request.setLat(String.valueOf(latitude));
        request.setLng(String.valueOf(longitude));
        request.setLanguage(Request.Language.ENGLISH);
        if(preferences.getString(getString(R.string.units_list_key), "0").equals("0")){
            //Use English units
            request.setUnits(Request.Units.US);
            units = 0;
        }
        else{
            //Use metric units
            request.setUnits(Request.Units.UK);
            units = 1;
        }

        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                Log.i("DarkSky API", "Pull request successful");
                //Setup a static reference to the response for other activities
                pulledWeatherResponse = weatherResponse;

                //Parse and format response
                //Parse currentTemp
                Double tempDouble = weatherResponse.getCurrently().getTemperature();
                if(tempDouble != null){
                    currentTemp = tempDouble.intValue();
                    Log.i("currentTemp", currentTemp + "");
                }
                else{
                    currentTemp = -1;
                    Log.i("currentTemp", "---");
                }

                //Set condition icon and condition statement
                if(weatherResponse.getCurrently().getIcon() != null)
                    currentIcon = weatherResponse.getCurrently().getIcon();
                else
                    currentIcon = "---";

                Log.i("currentIcon", currentIcon);
                switch (currentIcon){
                    case "clear-day":
                        currentConditions = "Clear";
                        break;
                    case "clear-night":
                        currentConditions = "Clear";
                        break;
                    case "rain":
                        currentConditions = "Rain";
                        break;
                    case "snow":
                        currentConditions = "Snow";
                        break;
                    case "sleet":
                        currentConditions = "Sleet";
                        break;
                    case "wind":
                        currentConditions = "Windy";
                        break;
                    case "fog":
                        currentConditions = "Foggy";
                        break;
                    case "cloudy":
                        currentConditions = "Cloudy";
                        break;
                    case "partly-cloudy-day":
                        currentConditions = "Partly Cloudy";
                        break;
                    case "partly-cloudy-night":
                        currentConditions = "Partly Cloudy";
                        break;
                    default:
                        currentConditions = "---";
                        Log.i("CurrentConditions", "Unsupported condition.");
                        break;
                }

                //Parse HI/LO
                Double HiDouble;
                Double LoDouble;
                HiDouble = weatherResponse.getDaily().getData().get(0).getTemperatureMax();
                LoDouble = weatherResponse.getDaily().getData().get(0).getTemperatureMin();
                if(HiDouble != null){
                    todaysHI = String.valueOf(HiDouble.intValue());
                }
                else{
                    todaysHI = "---";
                }

                if(LoDouble != null){
                    todaysLO = String.valueOf(LoDouble.intValue());
                }
                else{
                    todaysLO = "---";
                }

                Log.i("HI", todaysHI);
                Log.i("LO", todaysLO);
                todaysHILO = todaysHI + "\u00B0" + "/" + todaysLO + "\u00B0";//76°/42° format

                //Parse summary forecast
                if(weatherResponse.getMinutely() != null){
                    todaysMinutely = weatherResponse.getMinutely().getSummary();
                    Log.i("todayMinutely", todaysMinutely);
                }
                else{
                    Log.i("todayMinutely", "Null");
                    todaysMinutely = "---";
                }

                //Parse current wind speed and bearing
                String currentWindSpeed = weatherResponse.getCurrently().getWindSpeed();
                if(currentWindSpeed != null){
                    Double currentWindSpeedDouble = Double.valueOf(currentWindSpeed);
                    //Convert MPH to KPH if in metric
                    if(units == 1){
                        currentWindSpeedDouble = currentWindSpeedDouble * 1.6093440;
                    }
                    int currentWindSpeedInt = currentWindSpeedDouble.intValue();
                    String currentWindBearing = weatherResponse.getCurrently().getWindBearing();
                    int currentWindBearingValue = Integer.valueOf(currentWindBearing);
                    Log.i("WindSpeed", weatherResponse.getCurrently().getWindSpeed());
                    Log.i("WindBearing", weatherResponse.getCurrently().getWindBearing());
                    Log.i("WindBearingValue", String.valueOf(currentWindBearingValue));
                    //TODO - BE SURE TO CHECK FOR THE UNITS!
                    //English units
                    if(units == 0) {
                        if (currentWindBearingValue >= 0 && currentWindBearingValue < 45) {
                            currentWind = "↓" + currentWindSpeedInt + "MPH";
                        } else if (currentWindBearingValue >= 45 && currentWindBearingValue < 90) {
                            currentWind = "↙" + currentWindSpeedInt + "MPH";
                        } else if (currentWindBearingValue >= 90 && currentWindBearingValue < 135) {
                            currentWind = "←" + currentWindSpeedInt + "MPH";
                        } else if (currentWindBearingValue >= 135 && currentWindBearingValue < 180) {
                            currentWind = "↖" + currentWindSpeedInt + "MPH";
                        } else if (currentWindBearingValue >= 180 && currentWindBearingValue < 225) {
                            currentWind = "↑" + currentWindSpeedInt + "MPH";
                        } else if (currentWindBearingValue >= 225 && currentWindBearingValue < 270) {
                            currentWind = "↗" + currentWindSpeedInt + "MPH";
                        } else if (currentWindBearingValue >= 270 && currentWindBearingValue < 315) {
                            currentWind = "→" + currentWindSpeedInt + "MPH";
                        } else if (currentWindBearingValue >= 315 && currentWindBearingValue < 360) {
                            currentWind = "↘" + currentWindSpeedInt + "MPH";
                        }
                    }
                    //Metric units
                    else{
                        if (currentWindBearingValue >= 0 && currentWindBearingValue < 45) {
                            currentWind = "↓" + currentWindSpeedInt + "KPH";
                        } else if (currentWindBearingValue >= 45 && currentWindBearingValue < 90) {
                            currentWind = "↙" + currentWindSpeedInt + "KPH";
                        } else if (currentWindBearingValue >= 90 && currentWindBearingValue < 135) {
                            currentWind = "←" + currentWindSpeedInt + "KPH";
                        } else if (currentWindBearingValue >= 135 && currentWindBearingValue < 180) {
                            currentWind = "↖" + currentWindSpeedInt + "KPH";
                        } else if (currentWindBearingValue >= 180 && currentWindBearingValue < 225) {
                            currentWind = "↑" + currentWindSpeedInt + "KPH";
                        } else if (currentWindBearingValue >= 225 && currentWindBearingValue < 270) {
                            currentWind = "↗" + currentWindSpeedInt + "KPH";
                        } else if (currentWindBearingValue >= 270 && currentWindBearingValue < 315) {
                            currentWind = "→" + currentWindSpeedInt + "KPH";
                        } else if (currentWindBearingValue >= 315 && currentWindBearingValue < 360) {
                            currentWind = "↘" + currentWindSpeedInt + "KPH";
                        }
                    }
                }
                else{
                    currentWind = "---";
                }

                //Parse Precip
                String currentPrecipProb = weatherResponse.getCurrently().getPrecipProbability();
                if(currentPrecipProb != null){
                    Log.i("currentPrecipString", currentPrecipProb);
                    Double currentPrecipDouble = Double.valueOf(currentPrecipProb) * 100;
                    currentPrecip = currentPrecipDouble.intValue();
                }
                else{
                    Log.i("currentPrecipString", "---");
                    currentPrecip = -1;
                }

                //Parse Humidity
                String currentHumidityString = weatherResponse.getCurrently().getHumidity();
                if(currentHumidityString != null){
                    Log.i("currentHumidStr", currentHumidityString);
                    Double currentHumidityDouble = Double.valueOf(currentHumidityString) * 100;
                    currentHumidity = currentHumidityDouble.intValue();
                }
                else{
                    Log.i("currentHumidStr", "---");
                    currentHumidity = -1;
                }

                //Parse Dew Point
                String currentDewPointString = weatherResponse.getCurrently().getDewPoint();
                if(currentDewPointString != null){
                    Log.i("currentDPointStr", currentDewPointString);
                    Double currentDewPointDouble = Double.valueOf(currentDewPointString);
                    currentDewpoint = currentDewPointDouble.intValue();
                }
                else{
                    Log.i("currentDPointString", "---");
                    currentDewpoint = -1;
                }

                //Parse Pressure
                String currentPressureString = weatherResponse.getCurrently().getPressure();
                if(currentPressureString != null){
                    Log.i("currentPresString", currentPressureString);
                    Double currentPressureDouble = Double.valueOf(currentPressureString);
                    Double currentPressureDoubleConverted;
                    if(units == 0){
                        //English
                        currentPressureDoubleConverted = currentPressureDouble * 0.0295301;//Convert Millibars to inHg
                    }
                    else{
                        //Metric
                        currentPressureDoubleConverted = currentPressureDouble;//Use Millibars
                    }
                    currentPressure = currentPressureDoubleConverted.intValue();
                }
                else{
                    Log.i("currentPressString", "---");
                    currentPressure = -1;
                }

                //Parse Visibility
                String currentVisibilityString = weatherResponse.getCurrently().getVisibility();
                if(currentVisibilityString != null){
                    Log.i("currentVisString", currentVisibilityString);
                    Double currentVisibilityDouble = Double.valueOf(currentVisibilityString);
                    Integer currentVisibiltiyInt = Double.valueOf(currentVisibilityDouble).intValue();
                    //If it is above 1, parse to just an integer
                    if(currentVisibilityDouble > 1){
                        currentVisibilty = String.valueOf(currentVisibiltiyInt);
                    }
                    //Else, pass something like 0.45
                    else{
                        currentVisibilty = currentVisibilityDouble.toString();
                    }
                }
                else{
                    Log.i("currentVisString", "---");
                    currentVisibilty = "---";
                }

                //Parse cloud cover
                String currentCloudCoverString = weatherResponse.getCurrently().getCloudClover();
                if(currentCloudCoverString != null){
                    Log.i("currentCloudCover", currentCloudCoverString);
                    Double currentCloudCoverDouble = Double.valueOf(currentCloudCoverString) * 100;
                    currentCloudCover = currentCloudCoverDouble.intValue();
                }
                else{
                    Log.i("currentCloudCover", "---");
                    currentCloudCover = -1;
                }

                //Sunrise time
                String sunriseTimeString = weatherResponse.getDaily().getData().get(0).getSunriseTime();//UNIX timestamp
                if(sunriseTimeString != null){
                    //Get times in 12 hour format
                    if(hourFormat == 0){
                        //Parse sunrise time
                        Log.i("sunriseTimeUNIX", sunriseTimeString);
                        Long sunriseTimeInMili = Long.valueOf(sunriseTimeString) * 1000;
                        Date sunriseDateObject = new Date(sunriseTimeInMili);
                        SimpleDateFormat sunriseDateFormat = new SimpleDateFormat("h:mm aa");
                        sunriseTime = sunriseDateFormat.format(sunriseDateObject.getTime());
                        Log.i("sunriseTime", sunriseTime);
                    }
                    //Else get times in 24 hour format
                    else{
                        //Parse sunrise time
                        Log.i("sunriseTimeUNIX", sunriseTimeString);
                        Long sunriseTimeInMili = Long.valueOf(sunriseTimeString) * 1000;
                        Date sunriseDateObject = new Date(sunriseTimeInMili);
                        SimpleDateFormat sunriseDateFormat = new SimpleDateFormat("HH:mm");
                        sunriseTime = sunriseDateFormat.format(sunriseDateObject.getTime());
                        Log.i("sunriseTime", sunriseTime);
                    }
                }
                else{
                    Log.i("sunriseTime", "---");
                    sunriseTime = "---";
                }

                //Sunset time
                String sunsetTimeString = weatherResponse.getDaily().getData().get(0).getSunsetTime();//UNIX timestamp
                if(sunsetTimeString != null){
                    //Get the times in 12 hour format
                    if(hourFormat == 0){
                        //Parse sunset time
                        Log.i("sunsetTimeUNIX", sunsetTimeString);
                        Long sunsetTimeInMili = Long.valueOf(sunsetTimeString) * 1000;
                        Date sunsetDateObject = new Date(sunsetTimeInMili);
                        SimpleDateFormat sunsetDateFormat = new SimpleDateFormat("h:mm aa");
                        sunsetTime = sunsetDateFormat.format(sunsetDateObject.getTime());
                        Log.i("sunsetTime", sunsetTime);
                    }
                    //Else get the times in 24 hour format
                    else{
                        //Parse sunset time
                        Log.i("sunsetTimeUNIX", sunsetTimeString);
                        Long sunsetTimeInMili = Long.valueOf(sunsetTimeString) * 1000;
                        Date sunsetDateObject = new Date(sunsetTimeInMili);
                        SimpleDateFormat sunsetDateFormat = new SimpleDateFormat("HH:mm");
                        sunsetTime = sunsetDateFormat.format(sunsetDateObject.getTime());
                        Log.i("sunsetTime", sunsetTime);
                    }
                }
                else{
                    Log.i("sunsetTime", "---");
                    sunsetTime = "---";
                }

                //Get hourly data
                if(hourFormat == 0){
                    //Use 12 hour format
                    parseHourly();
                }
                else{
                    //Use 24 hour format
                    parseHourly24();
                }
                //Parse hours for comparison during hourly fog icon assignment
                parseHourly24ForComparison();

                //Get daily data
                parseDaily();

                //Get weather alerts
                if(weatherResponse.getAlerts() != null){
                    for(int i = 0; i < weatherResponse.getAlerts().size(); i++){
                        Log.i("Weather alerts", weatherResponse.getAlerts().get(i).getTitle());
                        Log.i("Weather alerts", weatherResponse.getAlerts().get(i).getDescription());
                        alertsCount ++;
                    }

                    //Any alerts?
                    if(alertsCount != 0){
                        //Display a snackbar for 6 seconds
                        alertSnackbar = Snackbar
                                .make(mainActivityLayout, "Weather alerts available.", Snackbar.LENGTH_LONG)
                                .setDuration(6000)
                                .setAction("View", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //Move to the alerts activity
                                        Intent alertsIntent = new Intent(MainActivity.this, AlertsActivity.class);
                                        alertsIntent.putExtra("setID", setID);
                                        alertsIntent.putExtra("conditionsIcon", currentIcon);
                                        startActivity(alertsIntent);
                                    }
                                });

                        alertSnackbar.show();

                        //Log the new alerts
                        Log.i("alerts", weatherResponse.getAlerts().size() + "");
                        for(int i = 0; i < weatherResponse.getAlerts().size(); i++){
                            Log.i("Alert", weatherResponse.getAlerts().get(i).getTitle());
                            Log.i("Alert", weatherResponse.getAlerts().get(i).getDescription());
                        }

                        //Update the alert notif alerts base if enabled
                        if(preferences.getBoolean(getString(R.string.alert_notif_key), false)){
                            Log.i("Main activity", "Updating alert notif");
                            //Check to see if these are new alerts
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
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

                            //Persist the new alerts list as to not notify the user of the same alerts
                            for(int i = 0; i < newAlerts.size(); i++){
                                String alertURI = newAlerts.get(i).getUri();
                                Long alertIssueTime = newAlerts.get(i).getTime() * 1000;//Store UNIX in millis
                                editor.putLong(alertURI + ".Alert", alertIssueTime);
                            }

                            //Check to see if it has been 48 hours, if we can remove any persisted alerts from sharedPrefs
                            Map<String,?> sharedPrefsKeys = prefs.getAll();
                            for(String key : sharedPrefsKeys.keySet()){
                                Log.i("Printing key", key);
                                if(key.contains(".Alert")){
                                    //Get the issuance time of the alert in UNIX * 1000
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
                            editor.apply();
                        }
                    }

                    //Make sure to remove the seen key-value pairs for the alert adapter after 48 hours
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    Map<String,?> sharedPrefsKeys = prefs.getAll();
                    for(String key : sharedPrefsKeys.keySet()){
                        if(key.contains(".Seen")){
                            //Get the issuance time of the alert in UNIX * 1000
                            Log.i("Key", "Contains .Seen");
                            Long value = (Long) sharedPrefsKeys.get(key);
                            //If 48 hours has passed since the issuance of the alert
                            if(System.currentTimeMillis() >= value + 172800000){
                                //Remove the alert
                                Log.i("Removing seen pair", key);
                                editor.remove(key);
                            }
                        }
                    }
                    editor.apply();

                }
                //Log stored alerts
                else{
                    Log.i("alerts", "null");
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    Map<String,?> sharedPrefsKeys = prefs.getAll();
                    for(String key : sharedPrefsKeys.keySet()){
                        Log.i("Printing key", key);
                        if(key.contains(".Alert")){
                            Log.i("Key", "Contains .Alert");
                        }
                    }
                }

                //Set main layout color
                if(changeTheme){
                    setID = determineLayoutColor(sunriseTimeString, sunsetTimeString);
                    setMainLayoutColor(setID);
                }

                //Update views
                Log.i("pulledHoursSize", String.valueOf(pulledHours.size()));
                mainFragmentTransaction();
                MainFragment.passRecyclerDataSets(pulledHours, pulledComparisonHours, pulledTemps, pulledIcons, pulledWinds, pulledDays, pulledDailyCond, pulledHIs, pulledLOs, pulledPrecips);
                MainFragment.passViewData(currentLocation, currentDay, currentDate, currentIcon, currentTemp, currentConditions, todaysHILO, todaysMinutely, currentWind, currentPrecip, currentHumidity, currentDewpoint,
                        currentPressure, currentVisibilty, currentCloudCover, sunriseTime, sunsetTime, updateTime);

                //Update ongoing notification if it is enabled
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                if(prefs.getBoolean(getString(R.string.ongoing_notif_key), false)){
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        //Create notification for Lollipop through Marshmallow
                        updateNotification();
                    }
                    else if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                        //Create notification for Nougat and above
                        updateNewNotification();
                    }
                }

                //Clear the alert notification if it is enabled
                if(prefs.getBoolean(getString(R.string.alert_notif_key), false)){
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(MainActivity.ALERT_NOTIF_ID);
                }

                //Terminate Google API Connection
                if(googleApiClient.isConnected()){
                    googleApiClient.disconnect();
                }

                //Setup a countdown of 10 minutes, and when triggered, display a snackbar telling the user that the data is outdated
//                countdown.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Set outdated boolean to true
//                        Log.i("countdown", "returned");
//                        dataOutdated = true;
//                    }
//                }, 600000);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("DarkSky API", "Error while calling: " + retrofitError.getUrl());
                Log.i("DarkSky API", retrofitError.getMessage());
                noConnectionFragmentTransaction();
                if(googleApiClient.isConnected()){
                    googleApiClient.disconnect();
                }
            }
        });
    }

    //Lifecycle events
    @Override
    protected void onStop() {
        if(googleApiClient != null){
            if(googleApiClient.isConnected()){
                googleApiClient.disconnect();
            }
        }
        isRunning = false;
//        if(outdatedSnackbar != null){
//            outdatedSnackbar.dismiss();
//        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume", "Triggered");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Enable 24 hour time
        if(preferences.getBoolean(getString(R.string.hour_format_key), false)){
            //24 hour format
            hourFormat = 1;
        }
        else{
            //12 hour format
            hourFormat = 0;
        }

        //Give fragment interfaces reference to mainActivity
        NoConnectionFragment.setConnectionRefresher(this);
        LocationUnavailableFragment.setDataFetcher(this);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        isRunning = true;
        //Check to see if there are any deferred fragment transactions

        if(loadingPending){
            loadingPending = false;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Commit the fragment after 1 second to preserve animations
                    loadingFragmentTransaction();
                }
            }, 1000);
        }

        if(mainPending){
            mainPending = false;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Commit the fragment after 1 second to preserve animations
                    mainFragmentTransaction();
                }
            }, 1000);
        }

        if(locationUnavPending){
            locationUnavPending = false;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Commit the fragment after 1 second to preserve animations
                    locationUnavailableFragmentTransaction();
                }
            }, 1000);
        }

        if(noConnectionPending){
            noConnectionPending = false;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Commit the fragment after 1 second to preserve animations
                    noConnectionFragmentTransaction();
                }
            }, 1000);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mRetryProviderInstall) {
            // We can now safely retry installation.
            ProviderInstaller.installIfNeededAsync(this, this);
        }
        mRetryProviderInstall = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
//        if(outdatedSnackbar != null){
//            outdatedSnackbar.dismiss();
//        }
        //Remove references
        NoConnectionFragment.setConnectionRefresher(null);
        LocationUnavailableFragment.setDataFetcher(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Display a snackbar if it has been 10 minutes since the data was updated
//        if(dataOutdated){
//            Log.i("onStart", "Data outdated");
//
//            //Display the snackbar after 1 second
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    outdatedSnackbar = Snackbar
//                            .make(mainActivityLayout, "Forecast is outdated.", Snackbar.LENGTH_LONG)
//                            .setAction("REFRESH", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    refresh();
//                                }
//                            });
//                    outdatedSnackbar.show();
//                }
//            }, 1000);
//        }
//        else
//            Log.i("onStart", "Data up to date");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove countdown callbacks
//        countdown.removeCallbacksAndMessages(null);
        clearDataSets();
        Log.i("MainActivity", "Destroyed");
    }

    //Fragments

    /**
     * Refreshes data.
     */
    public void refresh(){
        clearDataSets();
        loadingFragmentTransaction();
        //Reconnect to Google services, and in onConnected, requestDataAndLocation will be called.
        if(!googleApiClient.isConnected()){
            googleApiClient.connect();
        }

        //Reset countdown logic
//        countdown.removeCallbacksAndMessages(null);
//        dataOutdated = false;
//        if(outdatedSnackbar != null)
//            outdatedSnackbar.dismiss();
//        else
//            Log.i("outdatedSnackbar", "Null");
    }

    /**
     * Refreshes data without transacting loadingFragment.
     */
    public void refreshNoLoad(){
        clearDataSets();
        //Reconnect to Google services, and in onConnected, requestDataAndLocation will be called.
        if(!googleApiClient.isConnected()){
            googleApiClient.connect();
        }

        //Reset countdown logic
//        countdown.removeCallbacksAndMessages(null);
//        dataOutdated = false;
//        if(outdatedSnackbar != null)
//            outdatedSnackbar.dismiss();
//        else
//            Log.i("outdatedSnackbar", "Null");
    }

    /**
     * Creates new mainFragment transaction.
     */
    private void mainFragmentTransaction(){
        enableAlerts = true;
        enableRefresh = true;
        //Make sure it is fine to commit the transaction
        if(isRunning){
            FragmentManager mainFragmentManager = getFragmentManager();
            FragmentTransaction mainFragmentTransaction = mainFragmentManager.beginTransaction();
            MainFragment MainFragment = new MainFragment();
            if(!isFinishing()){
                mainFragmentTransaction.setCustomAnimations(R.animator.slide_in, 0);
                mainFragmentTransaction.replace(R.id.mainContentView, MainFragment);
                mainFragmentTransaction.commit();
            }
            else{
                Log.i("MainActivity", "Finishing");
            }
        }
        else{
            //Defer the transaction to onResumeFragments()
            mainPending = true;
        }
    }

    /**
     * Creates new loadingFragment transaction.
     */
    private void loadingFragmentTransaction(){
        enableRefresh = false;
        enableAlerts = false;
        if(isRunning){
            FragmentManager loadingFragmentManager = getFragmentManager();
            FragmentTransaction loadingFragmentTransaction = loadingFragmentManager.beginTransaction();
            LoadingFragment LoadingFragment = new LoadingFragment();
            if(!isFinishing()){
                loadingFragmentTransaction.replace(R.id.mainContentView, LoadingFragment);
                loadingFragmentTransaction.commit();
            }
            else{
                Log.i("MainActivity", "Finishing");
            }
            setMainLayoutColor(1);
        }
        else{
            Log.i("loadingTransaction", "!isRunning");
            loadingPending = true;
        }
    }

    /**
     * Creates new noConnectionFragment transaction.
     */
    private void noConnectionFragmentTransaction(){
        enableAlerts = false;
        enableRefresh = false;
        if(isRunning){
            FragmentManager noConnectionFragmentManager = getFragmentManager();
            FragmentTransaction noConnectionFragmentTransaction = noConnectionFragmentManager.beginTransaction();
            NoConnectionFragment noConnectionFragment = new NoConnectionFragment();
            if(!isFinishing()){
                noConnectionFragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                noConnectionFragmentTransaction.replace(R.id.mainContentView, noConnectionFragment);
                noConnectionFragmentTransaction.commit();
            }
            else{
                Log.i("MainActivity", "Finishing");
            }
            setMainLayoutColor(1);
        }
        else{
            noConnectionPending = true;
        }

        //Reset setID
        setID = 1;
    }

    /**
     * Creates new locationUnavailableFragment transaction.
     */
    private void locationUnavailableFragmentTransaction(){
        enableAlerts = false;
        enableRefresh = false;
        if(isRunning){
            FragmentManager locationUnavailableFragmentManager = getFragmentManager();
            FragmentTransaction locationUnavailableFragmentTransaction = locationUnavailableFragmentManager.beginTransaction();
            LocationUnavailableFragment locationUnavailableFragment = new LocationUnavailableFragment();
            if(!isFinishing()){
                locationUnavailableFragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                locationUnavailableFragmentTransaction.replace(R.id.mainContentView, locationUnavailableFragment);
                locationUnavailableFragmentTransaction.commit();
            }
            else{
                Log.i("MainActivity", "Finishing");
            }
            setMainLayoutColor(1);
        }
        else{
            locationUnavPending = true;
        }
        //Reset setID
        setID = 1;
    }

    /**
     * Conducts loadingFragment transaction, and begins pulling location and data(called from NoConnectionFragment).
     */
    @Override
    public void retryConnection() {
        refresh();
    }

    /**
     * Conducts loadingFragment transaction, and begins pulling location and data(called from LocationUnavailableFragment).
     */
    @Override
    public void retryDataFetch() {
        refresh();
    }

    //View data parsing and formatting

    /**
     * Parses and formats non-weather related data.
     */
    private void retrieveAndFormatNonWeatherData(){
        getAddresses();
        getDay();
        getDate();
        getUpdateTime();
    }

    /**
     * Gets today's day to pass to mainFragment.
     */
    private void getDay(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String today = simpleDateFormat.format(calendar.getTime());

        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE");

        try {
            calendar1.setTime(simpleDateFormat.parse(today));
            currentDay = dayOfWeekFormat.format(calendar1.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets today's date to pass to mainFragment.
     */
    private void getDate(){
        //Format to "October 1, 2016" format, and set date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy");
        currentDate = format.format(calendar.getTime());
    }

    /**
     * Gets the time of the update call(current time).
     */
    private void getUpdateTime(){
        Date time = new Date();
        if(hourFormat == 0){
            //12 hour time
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm aa");
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("MMMM d");
            updateTime = "Updated - " + timeFormat.format(time.getTime());
        }
        else{
            //24 hour time
            SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
            Calendar calendar = Calendar.getInstance();
            updateTime = "Updated - " + timeFormat.format(time.getTime());
        }
    }

    /**
     * Gets current UNIX timestamp.
     * @return
     */
    private String getCurrentTime(){
        long currentTime = System.currentTimeMillis() /1000;
        String currentTimeString = String.valueOf(currentTime);
        return currentTimeString;
    }

    /**
     * Gets current hour in 24 format.
     */
    private String getCurrentHour(){
        Date time = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("k");
        return timeFormat.format(time.getTime());
    }

    /**
     * Parses hour info for hourly datasets in 24hr format(but it continues above 24 for comparison).
     */
    private void parseHourly24ForComparison() {
        //Load in the next 24 hours
        int hourlySetSize = pulledWeatherResponse.getHourly().getData().size();
        Log.i("hourlySetSize", String.valueOf(hourlySetSize));
        //Gather only the next 24 hours
        if (hourlySetSize >= 24) {
            int iteratedHour = Integer.valueOf(getCurrentHour());
            Log.i("hourlySetSize", "greaterThan24");
            Log.i("iteratedHourStart", String.valueOf(iteratedHour));
            for (int i = 0; i < 24; i++) {
                Log.i("iteratedHourComp", iteratedHour + "");
                //Keep iterating for up to the next 24 hours, with entries that go over 23 being the next day
                    pulledComparisonHours.add(iteratedHour);
                    iteratedHour++;
            }
            Log.i("pulledHoursSize24", String.valueOf(pulledComparisonHours.size()));
        } else {
            //Use the data available
            int iteratedHour = Integer.valueOf(getCurrentHour());
            Log.i("iteratedHourComp", String.valueOf(iteratedHour));
            for (int i = 0; i < pulledWeatherResponse.getHourly().getData().size(); i++) {
                iteratedHour = iteratedHour % 24;
                Log.i("iteratedHourStart", String.valueOf(iteratedHour));
                //Keep iterating for up to the amount of data we have, with entries that go over 23 being the next day
                    pulledComparisonHours.add(iteratedHour);
                    iteratedHour++;
            }
            Log.i("pulledHoursSizeComp", String.valueOf(pulledComparisonHours.size()));
        }
    }

    /**
     * Parses hour info for hourly datasets in 24 hr format.
     */
    private void parseHourly24() {
        //Load in the next 24 hours
        int hourlySetSize = pulledWeatherResponse.getHourly().getData().size();
        Log.i("hourlySetSize", String.valueOf(hourlySetSize));
        //Gather only the next 24 hours
        if (hourlySetSize >= 24) {
            int iteratedHour = Integer.valueOf(getCurrentHour());
            Log.i("hourlySetSize", "greaterThan24");
            Log.i("iteratedHourStart", String.valueOf(iteratedHour));
            for (int i = 0; i < 24; i++) {
                Log.i("iteratedHour", iteratedHour + "");
                //Keep iterating for up to the next 24 hours
                if(iteratedHour == 24){
                    pulledHours.add(0 + "000");
                    iteratedHour = 0;
                }
                else if(iteratedHour < 10){
                    pulledHours.add("0" + iteratedHour + "00");
                }
                else if(iteratedHour >= 10 && iteratedHour < 24){
                    pulledHours.add(iteratedHour + "00");
                }
                iteratedHour++;
            }
            Log.i("pulledHoursSize24", String.valueOf(pulledHours.size()));
        } else {
            //Use the data available
            int iteratedHour = Integer.valueOf(getCurrentHour());
            Log.i("iteratedHour", String.valueOf(iteratedHour));
            for (int i = 0; i < pulledWeatherResponse.getHourly().getData().size(); i++) {
                iteratedHour = iteratedHour % 24;
                Log.i("iteratedHourStart", String.valueOf(iteratedHour));
                //Keep iterating for up to the amount of data we have
                if(iteratedHour == 24){
                    pulledHours.add(0 + "000");
                    iteratedHour = 0;
                }
                else if(iteratedHour < 10){
                    pulledHours.add("0" + iteratedHour + "00");
                }
                else if(iteratedHour >= 10 && iteratedHour < 24){
                    pulledHours.add(iteratedHour + "00");
                }
                iteratedHour++;
            }
            Log.i("pulledHoursSize24", String.valueOf(pulledHours.size()));
        }

        //Get icon for next 24 hours
        for(int i = 0; i < pulledHours.size(); i++){
            pulledIcons.add(pulledWeatherResponse.getHourly().getData().get(i).getIcon());
        }

        //Get temps for the next 24 hours
        for(int i = 0; i < pulledHours.size(); i++){
            Double pulledTempDouble = pulledWeatherResponse.getHourly().getData().get(i).getTemperature();
            pulledTemps.add(pulledTempDouble.intValue());
        }

        //Get winds for the next 24 hours
        for(int i = 0; i < pulledHours.size(); i++){
            String pulledWindString = pulledWeatherResponse.getHourly().getData().get(i).getWindSpeed();
            Double pulledWindDouble = Double.valueOf(pulledWindString);
            //Convert MPH to KPH if in metric
            if(units == 1){
                pulledWindDouble = pulledWindDouble * 1.6093440;
            }
            pulledWinds.add(pulledWindDouble.intValue());
        }
    }

    /**
     * Parses hourly info for hourly datasets in 12hr format.
     */
    private void parseHourly(){
        //Load in the next 24 hours
        int hourlySetSize = pulledWeatherResponse.getHourly().getData().size();
        Log.i("hourlySetSize", String.valueOf(hourlySetSize));
        //Gather only the next 24 hours
        if(hourlySetSize >= 24){
            int iteratedHour = Integer.valueOf(getCurrentHour());
            Log.i("hourlySetSize", "greaterThan24");
            Log.i("iteratedHourStart", String.valueOf(iteratedHour));
            for(int i = 0; i < 24; i++){
                iteratedHour = iteratedHour % 24;
                Log.i("iteratedHour", String.valueOf(iteratedHour));
                if(iteratedHour < 12 && iteratedHour > 0){
                    pulledHours.add(iteratedHour + "AM");
                    iteratedHour ++;
                }
                else if(iteratedHour == 12){
                    pulledHours.add("12PM");
                    iteratedHour++;
                }
                else if(iteratedHour > 12 && iteratedHour < 24){
                    pulledHours.add((iteratedHour % 12) + "PM");
                    iteratedHour++;
                }
                else if(iteratedHour == 0){
                    pulledHours.add("12AM");
                    iteratedHour++;
                }
            }
            Log.i("pulledHoursSize", String.valueOf(pulledHours.size()));
        }
        else{
            //Use the data available
            int iteratedHour = Integer.valueOf(getCurrentHour());
            Log.i("iteratedHour", String.valueOf(iteratedHour));
            for(int i = 0; i < pulledWeatherResponse.getHourly().getData().size(); i++){
                iteratedHour = iteratedHour % 24;
                Log.i("iteratedHourStart", String.valueOf(iteratedHour));
                if(iteratedHour < 12 && iteratedHour > 0){
                    pulledHours.add(iteratedHour + "AM");
                    iteratedHour ++;
                }
                else if(iteratedHour == 12){
                    pulledHours.add("12PM");
                    iteratedHour++;
                }
                else if(iteratedHour > 12 && iteratedHour < 24){
                    pulledHours.add((iteratedHour % 12) + "PM");
                    iteratedHour++;
                }
                else if(iteratedHour == 0){
                    pulledHours.add("12AM");
                    iteratedHour++;
                }
            }
            Log.i("pulledHoursSize", String.valueOf(pulledHours.size()));
        }

        //Get icon for next 24 hours
        for(int i = 0; i < pulledHours.size(); i++){
            pulledIcons.add(pulledWeatherResponse.getHourly().getData().get(i).getIcon());
        }

        //Get temps for the next 24 hours
        for(int i = 0; i < pulledHours.size(); i++){
            Double pulledTempDouble = pulledWeatherResponse.getHourly().getData().get(i).getTemperature();
            pulledTemps.add(pulledTempDouble.intValue());
        }

        //Get winds for the next 24 hours
        for(int i = 0; i < pulledHours.size(); i++){
            String pulledWindString = pulledWeatherResponse.getHourly().getData().get(i).getWindSpeed();
            Double pulledWindDouble = Double.valueOf(pulledWindString);
            //Convert MPH to KPH if in metric
            if(units == 1){
                pulledWindDouble = pulledWindDouble * 1.6093440;
            }
            pulledWinds.add(pulledWindDouble.intValue());
        }
    }

    /**
     * Parses daily info for daily datasets.
     */
    private void parseDaily(){
        //Get next 7 days
        int dailySetSize = pulledWeatherResponse.getDaily().getData().size();
        Log.i("dailySetSize", String.valueOf(dailySetSize));
        //Get current date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String today = simpleDateFormat.format(calendar.getTime());
        Log.i("today", today);

        //Get next 7 days or as many days as the data allows
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE");
        try {
            for(int i = 0; i < dailySetSize - 1; i ++){
                calendar1.setTime(simpleDateFormat.parse(today));
                calendar1.add(Calendar.DAY_OF_WEEK, i);
                pulledDays.add(dayOfWeekFormat.format(calendar1.getTime()));
                Log.i("pulledDay", dayOfWeekFormat.format(calendar1.getTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Get HIs/LOs
        for(int i = 0; i < dailySetSize; i++){
            Double tempMin = pulledWeatherResponse.getDaily().getData().get(i).getTemperatureMin();
            Double tempMax = pulledWeatherResponse.getDaily().getData().get(i).getTemperatureMax();
            pulledLOs.add(tempMin.intValue());
            pulledHIs.add(tempMax.intValue());
            if(tempMin != null){
                Log.i("tempMin", String.valueOf(tempMin.intValue()));
            }
            else{
                Log.i("tempMin", "---");
            }

            if(tempMax != null){
                Log.i("tempMax", String.valueOf(tempMax.intValue()));
            }
            else{
                Log.i("tempMax", "---");
            }
        }

        //Get Precips
        for(int i = 0; i < dailySetSize; i++){
            String pulledPrecipString = pulledWeatherResponse.getDaily().getData().get(i).getPrecipProbability();
            if(pulledPrecipString != null){
                Log.i("pulledPrecips", pulledPrecipString);
            }
            else{
                Log.i("pulledPrecips", "---");
            }

            Double pulledPrecipDouble = Double.valueOf(pulledPrecipString) * 100;
            Integer pulledPrecipInt = pulledPrecipDouble.intValue();
            pulledPrecips.add(pulledPrecipInt);
        }

        //Get Icons
        for(int i = 0; i < dailySetSize; i++){
            String pulledIconString = pulledWeatherResponse.getDaily().getData().get(i).getIcon();
            if(pulledIconString != null){
                Log.i("pulledIconString", pulledIconString);
            }
            else{
                Log.i("pulledIconString", "---");
            }
            pulledDailyCond.add(pulledIconString);
        }
    }

    /**
     * Clears values from datasets and variables.
     */
    public void clearDataSets(){

        pulledHours.clear();
        pulledIcons.clear();
        pulledTemps.clear();
        pulledWinds.clear();

        pulledDays.clear();
        pulledDailyCond.clear();
        pulledHIs.clear();
        pulledLOs.clear();
        pulledPrecips.clear();

        currentDay = null;
        currentDate = null;
        currentTemp = -1;
        currentConditions = null;
        todaysHI = null;
        todaysLO = null;
        todaysHILO = null;
        currentWind = null;
        currentPrecip = -1;
        currentHumidity = -1;
        currentDewpoint = -1;
        currentPressure = -1;
        currentVisibilty = null;
        currentCloudCover = -1;
        sunriseTime = null;
        sunsetTime = null;
        updateTime = null;
    }

    //Activities

    /**
     * Launches dailyActivity with data parameters passed from dailyAdapter.
     */
    public void launchDailyActivity(Intent dailyIntent){
        Log.i("launchDaily", "Launching dailyActivity");
        startActivity(dailyIntent);
    }

    @Override
    public void onBackPressed() {
        //Kill the app process
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //Notifications

    /**
     * Updates ongoing notification if one exists on activity launch.
     */
    private void updateNotification() {
            //Create the weather notification
            int iconID;
            RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.custom_notif);
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
                    if (setID != 3) {
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
            notificationView.setTextViewText(R.id.notifCondition, currentTemp + "" + "° - " + currentConditions);
            //Set location
            if (!currentLocation.equals("---")) {
                notificationView.setTextViewText(R.id.notifLocation, currentLocation);
            } else {
                SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String savedLocation = mySPrefs.getString(getString(R.string.last_location_key), "---");
                notificationView.setTextViewText(R.id.notifLocation, savedLocation);
            }

            //Set high and low
            notificationView.setTextViewText(R.id.notifBody, "Hi - " + todaysHI + "° Lo - " + todaysLO + "°");
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
            notificationManager.notify(MainActivity.FOLLOW_NOTIF_ID, notificationBuilder.build());
        }

    /**
     * Updates ongoing notification if one exists on activity launch.
     */
    private void updateNewNotification() {
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
                if (setID != 3) {
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

        //Set location
        String notifLocation;
        if (!currentLocation.equals("---")) {
            notifLocation = currentLocation;
        } else {
            SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String savedLocation = mySPrefs.getString(getString(R.string.last_location_key), "---");
            notifLocation = savedLocation;
        }

        //Build the notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(currentTemp + "° - " + currentConditions)
                        .setContentText(todaysHI + "°/" + todaysLO + "° · " + notifLocation)
                        .setSmallIcon(iconID);

        //Intent to go to main activity
        Intent mainIntent = new Intent(this, IntroActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);
        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationBuilder.setOngoing(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MainActivity.FOLLOW_NOTIF_ID, notificationBuilder.build());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("onSaveInstanceState", "Called - Main");
        super.onSaveInstanceState(outState);
    }
}