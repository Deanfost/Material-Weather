package dean.weather;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

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
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PermissionsFragment.Initializer, NoConnectionFragment.connectionRefresher {

    Toolbar toolbar;
    LinearLayout mainActivityLayout;

    //Location settings change
    final int REQUEST_CHANGE_SETTINGS = 15;

    //Google APIs
    GoogleApiClient googleApiClient;
    public String latitude;
    public String longitude;

    //Hourly
    public List<Integer> pulledHours;
    public List<Integer> pulledTemps;
    public List<String> pulledConditions;
    public List<Integer> pulledWind;

    //Daily
    private List<String> pulledDays;
    private List<String> pulledDailyCond;
    private List<Integer> pulledHIs;
    private List<Integer> pulledLOs;
    private List<Integer> pulledPrecip;

    //Current
    private int currentTemp;
    private String currentConditions;
    private String todaysHI;
    private String todaysLO;
    private String todaysHILO;//Concatenate the two variables above to this format - HI/LO
    private String currentWind;
    private int currentHumidity;
    private int currentDewpoint;
    private int currentPressure;//Be sure to add units on the end when updating views!
    private int currentVisibilty;
    private int currentCloudCover;
    private int sunriseTime;
    private int sunsetTime;
    private String updateTime;

    public static int setID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an instance of GoogleAPIClient
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Give fragment interfaces reference to mainActivity
        PermissionsFragment.setInitializer(this);
        NoConnectionFragment.setConnectionRefresher(this);

        //Connect to the Google API
        googleApiClient.connect();

        //Set content view
        setContentView(R.layout.activity_main);

        //Customize toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Customize the app bar
        assert toolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        //Reference to mainLayout
        mainActivityLayout = (LinearLayout) findViewById(R.id.mainActivityLayout);

        //Initialize loading fragment at start
        loadingFragmentTransaction();

        //Get the time of day and determine which setID to use
        //TODO - Finish determineLayoutColor
        setID = 1;
        setMainLayoutColor(setID);
    }

    //Action bar events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Settings
            case R.id.action_settings:
                //TODO - GO TO SETTINGS ACTIVITY
                return true;
            //Refresh data
            case R.id.action_refresh:
                //TODO - REFRESH DATA
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

        //Customize menu options
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
        //Get current location
        requestLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Show fragment to show the user that the connection failed
        noConnectionFragmentTransaction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check the result of the location settings change request
        if(requestCode == REQUEST_CHANGE_SETTINGS){
            switch (requestCode) {
                case REQUEST_CHANGE_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made, so request location
                            try{
                                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                if(lastLocation != null){
                                    latitude = String.valueOf(lastLocation.getLatitude());
                                    longitude = String.valueOf(lastLocation.getLongitude());
                                    Log.i("Latitude", latitude);
                                    Log.i("Longitude", longitude);
                                    Snackbar snackbar = Snackbar.make(mainActivityLayout, latitude + ", " + longitude, Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            }
                            catch (SecurityException e){
                                Log.e("LocationPermission", "Permission denied");
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            loadingFragmentTransaction();
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }

    //Location updates
    /**
     * Creates location request.
     */
    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(300000);//5 Minutes
        locationRequest.setFastestInterval(60000);//One minute
        //City block accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    /**
     * Checks for location permissions, location settings, and pulls location from Google Location API.
     */
    private void requestLocation(){
        int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(locationPermissionCheck == PackageManager.PERMISSION_GRANTED){
            //Create location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(createLocationRequest());

            //Create location settings request to make sure the request is permitted
            PendingResult<LocationSettingsResult> locationSettingsResultPendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

            //Check location settings
            locationSettingsResultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status locationStatus = locationSettingsResult.getStatus();
                    final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                    switch (locationStatus.getStatusCode()){
                        case LocationSettingsStatusCodes.SUCCESS:
                            //All location requirements are satisfied, request location
                            try{
                                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                if(lastLocation != null){
                                    //Get latitude and longitude for DarkSky API
                                    latitude = String.valueOf(lastLocation.getLatitude());
                                    longitude = String.valueOf(lastLocation.getLongitude());
                                    Log.i("Latitude", latitude);
                                    Log.i("Longitude", longitude);
                                    Snackbar snackbar = Snackbar.make(mainActivityLayout, latitude + ", " + longitude, Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    mainFragmentTransaction();//this is only for testing! remove soon
                                    //Call API
//                                    pullForecast();
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
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                locationStatus.startResolutionForResult(MainActivity.this, REQUEST_CHANGE_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Location settings aren't satisfied, but there is no way to fix them. Do not show dialog.
                            break;
                    }
                }
            });
        }
        else{
            //Tell the user to enable location services
            //TODO - SHOW A FRAGMENT TELLING THE USER TO ENABLE LOCATIONS AND IMPLEMENT LOGIC
            loadingFragmentTransaction();
        }
    }

    //Layout customizations and updates
    /**
     * Gets the time of day, and determines which color set(colorPurple/colorPurpleDark) should be used.
     * @return colorSet
     */
    private int determineLayoutColor() {
        int setID = 0;
        Calendar c = Calendar.getInstance();
//        int hour =
        //TODO - FIND OUT WHEN THE SUNRISE/SUNSET IS AND IF TIME IS WITHIN 30 MINS OF IT, SET COLOR TO YELLOW

        return setID;
    }

    /**
     * Customizes main layout colors.
     * @param colorSet
     */
    private void setMainLayoutColor(int colorSet){
        //Setup resources to change
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
                toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorYellowDark));

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
                toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlueDark));

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
                toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorOrangeDark));

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
                toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorPurpleDark));

                window = null;
                break;
        }
    }

    //Dark Sky API
    /**
     * Initializes API Wrapper Lib, and forms pull request to receive weather data.
     */
    private void pullForecast(){
        //Get the Dark Sky Wrapper API ready
        ForecastApi.create("331ebe65d3032e48b3c603c113435992");

        //Form a pull request
        RequestBuilder weather = new RequestBuilder();
        Request request = new Request();
        request.setLat(latitude);
        request.setLng(longitude);
        request.setUnits(Request.Units.US);
        request.setLanguage(Request.Language.ENGLISH);

        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                Log.i("DarkSky API", "Pull request successful");
                //TODO - Parse JSON response

                //TODO - Convert units

                //TODO - Populate data sets

                //TODO - Update views
                mainFragmentTransaction();//Create method in mainFragment to update views

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("DarkSky API", "Error while calling: " + retrofitError.getUrl());
                noConnectionFragmentTransaction();
            }
        });
    }

    //Lifecycle events
    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    //Fragments
    /**
     * Create new mainFragment transaction.
     */
    private void mainFragmentTransaction(){
        FragmentManager mainFragmentManager = getFragmentManager();
        FragmentTransaction mainFragmentTransaction = mainFragmentManager.beginTransaction();
        MainFragment MainFragment = new MainFragment();
        mainFragmentTransaction.add(R.id.mainContentView, MainFragment);
        mainFragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        mainFragmentTransaction.commit();
    }
    /**
     * Create new loadingFragment transaction.
     */
    private void loadingFragmentTransaction(){
        FragmentManager mainFragmentManager = getFragmentManager();
        FragmentTransaction mainFragmentTransaction = mainFragmentManager.beginTransaction();
        LoadingFragment LoadingFragment = new LoadingFragment();
        mainFragmentTransaction.add(R.id.mainContentView, LoadingFragment);
        mainFragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        mainFragmentTransaction.commit();
    }

    /**
     * Create new noConnectionFragment transaction.
     */
    private void noConnectionFragmentTransaction(){
        FragmentManager mainFragmentManager = getFragmentManager();
        FragmentTransaction mainFragmentTransaction = mainFragmentManager.beginTransaction();
        NoConnectionFragment noConnectionFragment = new NoConnectionFragment();
        mainFragmentTransaction.add(R.id.mainContentView, noConnectionFragment);
        mainFragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        mainFragmentTransaction.commit();
    }

    /**
     * Conducts loadingFragment transaction, and begins pulling location and data(called from PermissionsFragment).
     */
    @Override
    public void beginNormalOperations() {
        loadingFragmentTransaction();
        requestLocation();
    }

    /**
     * Conducts loadingFragment transaction, and begins pulling location and data(called from NoConnectionFragment).
     */
    @Override
    public void retryConnection() {
        loadingFragmentTransaction();
        requestLocation();
    }
}