package dean.weather;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.FirebaseApp;

/**
 * Created by Dean Foster on 3/2/2017.
 */

public class ChangeLocationSettingsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient;
    LinearLayout parent;
    Button btnEnable;
    static Context passedContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.location_settings_change_activity);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.i("GoogleAPIClient", "Creating new instance");
        }

        parent = (LinearLayout) findViewById(R.id.settingsChangeActivityParent);
        btnEnable = (Button) findViewById(R.id.btnEnableLocation);

        //Customize window
        Window window = ChangeLocationSettingsActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ChangeLocationSettingsActivity.this.getResources().getColor(R.color.colorBlueDark));

        //Button click listener
        btnEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ChangeSettingsAct", "btn clicked");
                googleApiClient.connect();
            }
        });

        //Change the preferences to reflect both services being disabled
        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.putBoolean(getString(R.string.ongoing_notif_key), false);
        editor.putBoolean(getString(R.string.alert_notif_key), false);
        editor.apply();

        //End the ongoing alarm
        Intent stopOngoing = new Intent(ChangeLocationSettingsActivity.this, alarmInterfaceService.class);
        stopOngoing.putExtra("repeatNotif", false);
        startService(stopOngoing);

        //Cancel notifs
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
        notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ERROR_ID);
        notificationManager.cancel(MainActivity.ALERT_NOTIF_ID);

        //End the alerts alarm
        Intent stopAlerts = new Intent(ChangeLocationSettingsActivity.this, alarmInterfaceService.class);
        stopAlerts.putExtra("alertNotif", false);
        startService(stopAlerts);
    }

    /**
     * Collects context from Main.
     * @param context
     */
    public static void passContext(Context context){
        passedContext = context;
    }

    @Override
    public void onBackPressed() {
        //Override onBackPressed
    }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("changeSettings", "onActivityResult - called");
        //Check the result of the location settings changeTheme request
        if(requestCode == 15){
            switch (requestCode) {
                case 15:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made, so move to main
                            try{
                                if(googleApiClient.isConnected())
                                {
                                    googleApiClient.disconnect();
                                }
                                finish();
                                MainActivity mainActivity = (MainActivity) passedContext;
                                mainActivity.refreshNoLoad();
                            }
                            catch (SecurityException e){
                                Log.e("LocationPermission", "Permission denied");
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to changeTheme settings, but chose not to
                            //Do nothing
                            if(googleApiClient.isConnected())
                                googleApiClient.disconnect();
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Create location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());

        //Create location settings request to make sure the request is permitted
        PendingResult<LocationSettingsResult> locationSettingsResultPendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        locationSettingsResultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status locationStatus = locationSettingsResult.getStatus();
//                        final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                switch (locationStatus.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        //Settings are fine, how did we get here? (Probs airplane mode)
                        Log.i("ChangeLocSettingsFrag", "Settings are fine");
                        //Move to main
                        finish();
                        MainActivity mainActivity = (MainActivity) passedContext;
                        mainActivity.refreshNoLoad();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            Log.i("changeSettings", "Change required");
                            locationStatus.startResolutionForResult(ChangeLocationSettingsActivity.this, 15);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Location settings aren't satisfied, but there is no way to fix them.
                        Log.i("changeSettings", "Change unavailable");
                        Snackbar snackbar = Snackbar.make(parent, "Please enable location services in settings.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("ChangeLocSettingsAct", "Connection failed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(googleApiClient != null){
            if(googleApiClient.isConnected()){
                googleApiClient.disconnect();
            }
        }
    }
}
