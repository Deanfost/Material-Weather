package dean.weather;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.firebase.FirebaseApp;

/**
 * Created by Dean on 12/23/2016.
 */

public class settingsActivity extends PreferenceActivity{
    //In app billing
    IInAppBillingService mService;

    //Preferences
    SwitchPreference ongoingNotif;
    SwitchPreference alertNotif;
    Preference tutorialPref;
    Preference feedbackPref;
    Preference supportPref;
    boolean performChecksReturn;
    int setID = MainActivity.setID;

    //Lifecycle and preference listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            //Destroy - restoring instance
            finish();
        }
        else {
            //Default implementation
            addPreferencesFromResource(R.xml.preferences);

            //Preferences
            ongoingNotif = (SwitchPreference) findPreference(getString(R.string.ongoing_notif_key));
            alertNotif = (SwitchPreference) findPreference(getString(R.string.alert_notif_key));
            tutorialPref = findPreference(getResources().getString(R.string.support_tutorial_key));
            feedbackPref = findPreference(getResources().getString(R.string.support_feedback_key));
            supportPref = findPreference(getResources().getString(R.string.support_donate_key));

            //Color accents
                switch(setID){
                    case 0:
                        setTheme(R.style.SettingsThemeYellow);
                        break;
                    case 1:
                        setTheme(R.style.SettingsThemeBlue);
                        break;
                    case 2:
                        setTheme(R.style.SettingsThemeOrange);
                        break;
                    case 3:
                        setTheme(R.style.SettingsThemePurple);
                        break;
                }

            //Customize the window
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorWhiteDark));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Ongoing notification pref
        ongoingNotif.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.i("ongoingNotif", "Pref changed");
                Boolean ongoingNotifValue = (Boolean) newValue;
                Log.i("ongoingNotif", ongoingNotifValue.toString());
                //Start the notif service
                if(ongoingNotifValue){
                    //Check if we are able to use current location
                    if(performChecks()){
                        //Start it, everything is looking good
                        Log.i("ongoingNotifPref", "Looks good, starting service");
                        Intent serviceIntent = new Intent(settingsActivity.this, alarmInterfaceService.class);
                        serviceIntent.putExtra("repeatNotif", true);
                        startService(serviceIntent);
                        return true;
                    }
                    else{
                        //Don't save the value, conditions not met
                        return false;
                    }
                }
                else{
                    //Tell the alarmservice to kill the alarm if both services have been disabled
                    Log.i("ongoingNotifPref", "stoppingService");
                    Intent stopService = new Intent(settingsActivity.this, alarmInterfaceService.class);
                    stopService.putExtra("repeatNotif", false);
                    startService(stopService);

                    //Cancel the notification
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
                    notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ERROR_ID);
                    return true;
                }
            }
        });

        //Alert notification pref
        alertNotif.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.i("alert pref", "Changed");
                if((Boolean) newValue){
                    //Check to see if we can do this
                    if(performChecks()){
                        //Start the alert service
                        Log.i("alert pref", "Starting service");
                        Intent alarmService = new Intent(settingsActivity.this, alarmInterfaceService.class);
                        alarmService.putExtra("repeatNotif", true);
                        startService(alarmService);
                        return true;
                    }
                    else{
                        //Leave the pref off, conditions not met
                        Log.i("performChecks", "Returned false");
                        return false;
                    }
                }
                else{
                    //Tell the alarmservice to kill the alarm if both services have been disabled
                    Intent stopService = new Intent(settingsActivity.this, alarmInterfaceService.class);
                    stopService.putExtra("repeatNotif", false);
                    startService(stopService);

                    //Kill the notification
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(MainActivity.ALERT_NOTIF_ID);
                    return true;
                }
            }
        });
        tutorialPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.i("Settings", "Tutorial pref clicked");
                Intent tutorialIntent = new Intent(settingsActivity.this, tutorialActivity.class);
                startActivity(tutorialIntent);
                return false;
            }
        });

        feedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.i("Settings", "Feedback pref clicked");
                //Launch a chooser for an email app to send feedback to me
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("text/email");
                email.putExtra(Intent.EXTRA_EMAIL, new String[] { "Deanfoster45@gmail.com" });
                email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                //Resolve the intent, and launch a chooser
                PackageManager pm = settingsActivity.this.getPackageManager();
                if(email.resolveActivity(pm) != null){
                    startActivity(Intent.createChooser(email, "Email developer"));
                }
                else{
                    Log.i("settingsActivity", "No application can handle email intent");
                    Toast.makeText(settingsActivity.this, "Please install an email application", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        supportPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                return true;
            }
        });

        ServiceConnection mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Un-register click listeners
        ongoingNotif.setOnPreferenceClickListener(null);
        alertNotif.setOnPreferenceChangeListener(null);
        tutorialPref.setOnPreferenceClickListener(null);
        feedbackPref.setOnPreferenceClickListener(null);
        supportPref.setOnPreferenceClickListener(null);
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
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Settings Activity", "Finishing");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("onSaveInstanceState", "Called - Settings");
        outState.putBoolean("restoring", true);
        super.onSaveInstanceState(outState);
    }
}