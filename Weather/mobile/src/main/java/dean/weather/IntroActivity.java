package dean.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

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

/**
 * Created by DeanF on 10/21/2016.
 */

public class IntroActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient;
    static int PERMISSIONS_REQUEST;
    Button btnNeedAccess;
    Button btnEnableServices;
    Button btnRetryConnection;
    Button btnRetryAirplane;

    @Override
    protected void onStart() {
        super.onStart();
        //Decide on which activity to launch
        Log.i("IntroActivity", "Instantiated");
        decideActivity();
    }

    /**
     * Chooses which activity to launch, depending on permissions and first launch.
     */
    private void decideActivity(){
        PERMISSIONS_REQUEST = 42;
        //Make sure we can access the user's location
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        //We have permissions
        if(locationPermission == PackageManager.PERMISSION_GRANTED){
            //Make sure location services are enabled
            checkLocationSettings();
        }
        else {
            //If we don't have permission, it may be first start
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String firstStart = sharedPref.getString(getResources().getString(R.string.first_launch_key), "0");
            if(firstStart.equals("0")){
                //It is first start, and we need to onboard the user, so we launch the onboarding activity
                Intent onboardingIntent = new Intent(this, OnboardingActivity.class);
                startActivity(onboardingIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Log.i("Intent", "Onboarding");
            }
            else{
                //It isn't first start, but we need to request permission for location
                Log.i("Intent", "Permissions");
                //Show permissions needed activity
                setContentView(R.layout.need_permission_activity);
                //Setup references and customizations for layout
                Window window = this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(R.color.colorBlueDark));
                btnNeedAccess = (Button) findViewById(R.id.btnNeedAccess);
                btnNeedAccess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Request location permission
                        ActivityCompat.requestPermissions(IntroActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET}, PERMISSIONS_REQUEST);
                    }
                });

                //Change the preferences to reflect both services being disabled
                SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = mySPrefs.edit();
                editor.putBoolean(getString(R.string.ongoing_notif_key), false);
                editor.putBoolean(getString(R.string.alert_notif_key), false);
                editor.apply();

                //End the ongoing alarm
                Intent stopOngoing = new Intent(this, alarmInterfaceService.class);
                stopOngoing.putExtra("repeatNotif", false);
                startService(stopOngoing);

                //End the alerts alarm
                Intent stopAlerts = new Intent(this, alarmInterfaceService.class);
                stopAlerts.putExtra("alertNotif", false);
                startService(stopAlerts);
            }
        }
    }

    //Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If the user didn't cancel the dialog
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("Permissions", "Permission granted");
                //Rerun checks
                decideActivity();
            }
        }
    }

    //Check for location settings

    /**
     * Checks for location settings, and asks user to change them if not satisfied
     */
    private void checkLocationSettings(){
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.i("GoogleAPIClient", "Creating new instance");
        }

        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }

        //Connect to the Google API
        googleApiClient.connect();
    }

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Check location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());

        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //Settings satisfied, launch main
                        Log.i("IntroActivity", "Launching main");
                        Intent startMain = new Intent(IntroActivity.this, MainActivity.class);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(startMain);
                        overridePendingTransition(0, 0);

                        if(googleApiClient.isConnected()){
                            googleApiClient.disconnect();
                        }
                        finish();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            //Show a layout asking them to enable location services
                            setContentView(R.layout.location_settings_change_activity);
                            //Setup references and customizations for layout
                            Window window = IntroActivity.this.getWindow();
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(IntroActivity.this.getResources().getColor(R.color.colorBlueDark));
                            btnEnableServices = (Button) findViewById(R.id.btnEnableLocation);
                            btnEnableServices.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Attempt to change location settings
                                    try {
                                        status.startResolutionForResult(IntroActivity.this, 90);
                                    } catch (IntentSender.SendIntentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        //Change the preferences to reflect both services being disabled
                        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = mySPrefs.edit();
                        editor.putBoolean(getString(R.string.ongoing_notif_key), false);
                        editor.putBoolean(getString(R.string.alert_notif_key), false);
                        editor.apply();

                        //End the ongoing alarm
                        Intent stopOngoing = new Intent(IntroActivity.this, alarmInterfaceService.class);
                        stopOngoing.putExtra("repeatNotif", false);
                        startService(stopOngoing);

                        //End the alerts alarm
                        Intent stopAlerts = new Intent(IntroActivity.this, alarmInterfaceService.class);
                        stopAlerts.putExtra("alertNotif", false);
                        startService(stopAlerts);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Check for airplane mode
                        if(Settings.System.getInt(IntroActivity.this.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0){
                            Log.i("IntroActivity", "Airplane mode enabled");
                            //Airplane mode is on, show layout, and disable services
                            setContentView(R.layout.airplane_mode_activity);
                            //Setup references and customizations for layout
                            Window window1 = IntroActivity.this.getWindow();
                            window1.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window1.setStatusBarColor(IntroActivity.this.getResources().getColor(R.color.colorBlueDark));
                            btnRetryAirplane = (Button) findViewById(R.id.btnRetryAirplane);
                            btnRetryAirplane.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Retry
                                    Log.i("IntroActivity", "Airplane retry clicked");
                                    decideActivity();
                                }
                            });
                        }
                        else{
                            //Show a layout asking them to enable location services in settings
                            Log.i("IntroActivity", "Resolution not available");
                            setContentView(R.layout.location_settings_change_activity_nobtn);
                            Window window1 = IntroActivity.this.getWindow();
                            window1.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window1.setStatusBarColor(IntroActivity.this.getResources().getColor(R.color.colorBlueDark));

                            if(googleApiClient.isConnected()){
                                googleApiClient.disconnect();
                            }

                            //Change the preferences to reflect both services being disabled
                            SharedPreferences mySPrefs1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor1 = mySPrefs1.edit();
                            editor1.putBoolean(getString(R.string.ongoing_notif_key), false);
                            editor1.putBoolean(getString(R.string.alert_notif_key), false);
                            editor1.apply();

                            //End the ongoing alarm
                            Intent stopOngoing1 = new Intent(IntroActivity.this, alarmInterfaceService.class);
                            stopOngoing1.putExtra("repeatNotif", false);
                            startService(stopOngoing1);

                            //End the alerts alarm
                            Intent stopAlerts1 = new Intent(IntroActivity.this, alarmInterfaceService.class);
                            stopAlerts1.putExtra("alertNotif", false);
                            startService(stopAlerts1);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("IntroActivity", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("IntroActivity", "Connection failed");
        //Show a layout asking the user to make sure there is a connection
        setContentView(R.layout.no_connection_activity);
        //Setup references and customizations for layout
        Window window = IntroActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(IntroActivity.this.getResources().getColor(R.color.colorBlueDark));
        btnRetryConnection = (Button) findViewById(R.id.btnRetryConnection);
        btnRetryConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retry the connection
                decideActivity();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Location settings change request returned
        if(requestCode == 90){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made, rerun logic
                    Log.i("IntroActivity", "Location settings changed");
                    decideActivity();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change location settings, but chose not to
                    Log.i("IntroActivity", "Location settings not changed");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(googleApiClient.isConnected())
            googleApiClient.disconnect();
    }
}