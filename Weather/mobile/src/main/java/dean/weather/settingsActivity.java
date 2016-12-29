package dean.weather;

import android.Manifest;
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
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

/**
 * Created by Dean on 12/23/2016.
 */

public class settingsActivity extends PreferenceActivity {
    SharedPreferences prefs;
    Preference followNotif;
    boolean performChecksReturn;

    //Lifecycle and click listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(settingsActivity.this);
        followNotif = findPreference(getString(R.string.follow_notif_key));

    }

    @Override
    protected void onStart() {
        super.onStart();
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
    }

    //Checks and callbacks
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
                //Show a toast asking the user to enable location services
                Toast.makeText(this, "Please enable location services to access this feature", Toast.LENGTH_SHORT).show();
                performChecksReturn = false;
            }
        }
        //Permission has not been granted
        else{
            //Request permission access
            ActivityCompat.requestPermissions(settingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 22);
            performChecksReturn = false;
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

    /**
     * Receives permission request result callback.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 22: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    Log.i("permissionResult", "granted");
                }
                else {
                    //Permission denied, do nothing
                    Log.i("permissionResult", "withheld");
                }
                return;
            }
        }
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
}