package dean.weather;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;

/**
 * Created by Dean on 12/23/2016.
 */

public class settingsActivity extends PreferenceActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient;
    SharedPreferences prefs;
    boolean permissionGranted;
    boolean servicesEnabled;
    Preference followNotif;
    boolean performChecksReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Setup GoogleAPIClient to check is location services are enabled
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(settingsActivity.this);
        followNotif = findPreference(getString(R.string.follow_notif_key));

    }

    //Configurations
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        // If the user has clicked on a preference screen, set up the screen
        if (preference instanceof PreferenceScreen) {
            setUpNestedScreen((PreferenceScreen) preference);
        }

        return false;
    }

    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();

        Toolbar bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        bar.setTitle(preferenceScreen.getTitle());

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //GoogleAPI
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Locations and callbacks
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made, so allow the service to be activated
                            Log.i("onActivityResult", "settingsEnabled");
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to, display snackbar asking the user to enable location services
                            Snackbar snackbar = Snackbar
                                    .make(getListView(), "Welcome to AndroidHive", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            Log.i("onActivityResult", "settingsDisabled");
                            break;
                        default:
                            break;
                    }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 22: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    Log.i("permissionResult", "granted");
                }
                else {
                    //Permission denied
                    Snackbar snackbar = Snackbar
                            .make(getListView(), "Grant location permissions to access this feature", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.i("permissionResult", "withheld");
                }
                return;
            }
        }
    }

    /**
     * Checks to see if both location permission and services are enabled.
     * @return performChecksReturn
     */
    private boolean performChecks(){
        //Check to see if location permissions and services are enabled
        int locationPermissionCheck = ContextCompat.checkSelfPermission(settingsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(locationPermissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.i("permissionsCheck", "Granted");
            //Check to see if location services are enabled
            if(isLocationServicesEnabled()){
                //Permissions granted, and services are on, start the service
                performChecksReturn = true;
            }
            else{
                //show a snackbar asking the user to enable location services
                Snackbar snackbar = Snackbar.make(getListView(), "Please enable location services to access this feature", Snackbar.LENGTH_SHORT);
                snackbar.show();
                performChecksReturn = false;
            }
        }
        //Permission has not been granted
        else{
            //Display a snackbar allowing the user to enable the permission
            Snackbar snackbar = Snackbar
                    .make(getListView(), "Please allow this app to access your location to use this feature", Snackbar.LENGTH_LONG)
                    .setAction("Allow", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Display a dialogue
                            ActivityCompat.requestPermissions(settingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 22);
                            performChecksReturn = false;
                        }
                    });
            snackbar.show();
        }
        Log.i("checksReturn", performChecksReturn + "");
        return performChecksReturn;
    }

    /**
     * Checks to see if location services are enabled.
     * @return
     */
    private boolean isLocationServicesEnabled(){
        int locationMode;
        try {
            locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        //True if location services are on and not set to device only
        Log.i("locServices", (locationMode != Settings.Secure.LOCATION_MODE_OFF && locationMode != Settings.Secure.LOCATION_MODE_SENSORS_ONLY) + "" );
        return locationMode != Settings.Secure.LOCATION_MODE_OFF && locationMode != Settings.Secure.LOCATION_MODE_SENSORS_ONLY;
    }

    //Lifecycle and click listeners
    @Override
    protected void onStart() {
        super.onStart();
        //Connect to googleAPIClient
//        if(!googleApiClient.isConnected()){
//            googleApiClient.connect();
//        }
        //Register preference listeners

        //Follow notification pref
        followNotif.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            boolean saveFollowNotifValue;
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.i("followNotif", "Pref clicked");

                Boolean followNotifValue = (Boolean) newValue;
                Log.i("followNotif", followNotifValue.toString());
                //Start the notif service
                if(followNotifValue){
                    //Check if we are able to
                    if(performChecks()){
                        //Start it, everything is looking good
                        Log.i("followNotifPref", "Looks good, starting service");
                        Intent serviceIntent = new Intent(settingsActivity.this, alarmInterface.class);
                        serviceIntent.putExtra("repeatNotif", true);
                        startService(serviceIntent);
                        saveFollowNotifValue = true;
                    }
                    else{
                        //Don't save the value, conditions not met
                        saveFollowNotifValue = false;
                    }
                }
                else{
                    //Stop the notif service
                    Log.i("followNotifPref", "stoppingService");
                    Intent stopService = new Intent(settingsActivity.this, alarmInterface.class);
                    stopService.putExtra("repeatNotif", false);
                    startService(stopService);

                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
                    saveFollowNotifValue = true;
                }
                //Persist the new value?
                Log.i("saveFollowNotif", saveFollowNotifValue + "");
                return saveFollowNotifValue;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Un-register click listeners
        followNotif.setOnPreferenceClickListener(null);

        //Disconnect from GoogleAPI
//        if(googleApiClient.isConnected()){
//            googleApiClient.disconnect();
//        }
    }

}