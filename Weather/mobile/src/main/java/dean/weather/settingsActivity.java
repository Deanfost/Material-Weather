package dean.weather;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.FirebaseApp;

/**
 * Created by Dean on 12/23/2016.
 */

public class settingsActivity extends PreferenceActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient;
    SharedPreferences prefs;
    boolean permissionGranted;
    boolean servicesEnabled;
    boolean saveFollowNotifValue;
    Preference followNotif;

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
                            try{
                                Intent startService = new Intent(settingsActivity.this, alarmInterface.class);
                                startService.putExtra(getString(R.string.follow_notif_key), true);
                                startService(startService);
                                saveFollowNotifValue = true;
                            }
                            catch (SecurityException e){
                                Log.e("LocationPermission", "Permission denied");
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to, display snackbar asking the user to enable location services
                            Snackbar snackbar = Snackbar
                                    .make(getListView(), "Welcome to AndroidHive", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            saveFollowNotifValue = false;
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
                    Intent startService = new Intent(this, alarmInterface.class);
                    startService.putExtra(getString(R.string.follow_notif_key), true);
                    startService(startService);
                    saveFollowNotifValue = true;
                }
                else {
                    //Permission denied
                    Snackbar snackbar = Snackbar
                            .make(getListView(), "Grant location permissions to access this feature", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    saveFollowNotifValue = false;
                }
                return;
            }
        }
    }

    //Lifecycle and click listeners
    @Override
    protected void onStart() {
        super.onStart();
        //Connect to googleAPIClient
        if(!googleApiClient.isConnected()){
            googleApiClient.connect();
        }
        //Register preference listeners

        //Follow notification pref
        followNotif.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Log.i("followNotif", "Pref clicked");

                Boolean followNotifValue = prefs.getBoolean(getString(R.string.follow_notif_key), false);
                Log.i("followNotif", followNotifValue.toString());
                if(followNotifValue){
                    //Check to see if location permissions and services are enabled
                    int locationPermissionCheck = ContextCompat.checkSelfPermission(settingsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
                    if(locationPermissionCheck == PackageManager.PERMISSION_GRANTED){
                        Log.i("permissionsCheck", "Granted");
                        //Create location settings request
                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                .addLocationRequest(createLocationRequest());
                        PendingResult<LocationSettingsResult> locationSettingsResultPendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                        locationSettingsResultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                            @Override
                            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                                Log.i("locationSettingsCheck", "callbackReceived");
                                final Status locationStatus = locationSettingsResult.getStatus();
                                switch (locationStatus.getStatusCode()){
                                    case LocationSettingsStatusCodes.SUCCESS:
                                        Log.i("locationServices", "Running");
                                        //Location services are running, launch the service
                                        Intent serviceIntent = new Intent(settingsActivity.this, alarmInterface.class);
                                        serviceIntent.putExtra("repeatNotif", true);
                                        startService(serviceIntent);
                                        saveFollowNotifValue = true;
                                        break;
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        //Ask the user to fix the settings
                                        Log.i("locationServices", "Resolution required");
                                        try {
                                            locationStatus.startResolutionForResult(settingsActivity.this, 1);
                                        } catch (IntentSender.SendIntentException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        //Display a snackbar telling the user to enable location services
                                        Log.i("locationServices", "Change unavailable");
                                        Snackbar snackbar = Snackbar
                                                .make(getListView(), "Enable location services to access this feature", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        saveFollowNotifValue = false;
                                        break;

                                }
                            }
                        });
                    }
                    //Permission has not been granted
                    else{
                        //Display a snackbar allowing the user to enable the permission
                        Snackbar snackbar = Snackbar
                                .make(getListView(), "Location permissions required", Snackbar.LENGTH_LONG)
                                .setAction("Grant", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //Display a dialogue
                                        ActivityCompat.requestPermissions(settingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 22);

                                    }
                                });

                        snackbar.show();
                    }
                }
                else{
                    //Stop the notif service
                    Log.i("followNotifPref", "stoppingService");
                    Intent stopService = new Intent(settingsActivity.this, alarmInterface.class);
                    stopService.putExtra(getString(R.string.follow_notif_key), false);
                    startService(stopService);

                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
                    saveFollowNotifValue = true;
                }
                //Persist the new value?
                if(saveFollowNotifValue){
                    return true;
                }
                else{
                    return false;
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        //Un-register click listeners
        followNotif.setOnPreferenceClickListener(null);

        //Disconnect from GoogleAPI
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

}