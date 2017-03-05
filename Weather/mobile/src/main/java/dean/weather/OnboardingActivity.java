package dean.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

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
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

/**
 * Created by DeanF on 10/29/2016.
 */

public class OnboardingActivity extends IntroActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static int PERMISSIONS_REQUEST = 42;
    private Fragment onBoardingFragPermissions;
    private Class onBoardingFragPermissionsClass;
    private Fragment onBoardingFragSettings;
    private Class onBoardingFragSettingsClass;
    public static Activity currentActivity;
    GoogleApiClient googleApiClient;
    boolean defaultFlow;
    Status locationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        currentActivity = this;

        //TODO - ADD LOGIC TO TEST FOR LOCATION SETTINGS
        //Connect to GoogleApiClient
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Determine if location settings are enabled, build the flow
        Log.i("Onboarding activity", "Checking location settings");
        googleApiClient.connect();

        //Add initial slide
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(R.color.colorBlue)
                .background(R.color.colorBlueLight)
                .fragment(R.layout.onboarding_frag_one)
                .build());
    }

    //Handle request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If the user didn't cancel the dialog
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                if(defaultFlow){

                    //Only save progress at this point
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.first_launch_key), "1");
                    editor.apply();

                    //Set new nav policy
                    setNavigationPolicy(new NavigationPolicy() {
                        @Override
                        public boolean canGoForward(int i) {
                            return true;
                        }

                        @Override
                        public boolean canGoBackward(int i) {
                            return false;
                        }
                    });
                }
                else{
                    //Set new nav policy
                    setNavigationPolicy(new NavigationPolicy() {
                        @Override
                        public boolean canGoForward(int i) {
                            switch (i){
                                case 6:
                                    return false;
                                case 7:
                                    return true;
                            }
                            return true;
                        }

                        @Override
                        public boolean canGoBackward(int i) {
                            return false;
                        }
                    });
                }
                nextSlide();
            }
        }
    }

    public static void requestPermissions(){
        ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET}, PERMISSIONS_REQUEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(googleApiClient != null){
            if(googleApiClient.isConnected()){
                googleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

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
                locationStatus = locationSettingsResult.getStatus();

                switch (locationStatus.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        //Location settings are on, initialize default flow
                        defaultFlow = true;
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //Location settings are not turned on, initialize different flow
                        defaultFlow = false;
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Continue with default flow
                        defaultFlow = true;
                        break;
                }
                buildFlow();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Onboarding activity", "GoogleApiClient failed");
        defaultFlow = true;
        buildFlow();
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

    public void startSettingsResolution(){
        try {
            locationStatus.startResolutionForResult(OnboardingActivity.this, 15);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check the result of the location settings change request
        if(requestCode == 15){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made, continue with flow, save progress in shared prefs
                    try{
                        if(!defaultFlow){
                            //Save progress
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.first_launch_key), "1");
                            editor.apply();
                        }

                        //Continue with flow
                        setNavigationPolicy(new NavigationPolicy() {
                            @Override
                            public boolean canGoForward(int i) {
                                return true;
                            }

                            @Override
                            public boolean canGoBackward(int i) {
                                return false;
                            }
                        });
                        nextSlide();

                    }
                    catch (SecurityException e){
                        Log.e("LocationPermission", "Permission denied");
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change location settings, but chose not to, do nothing
                    Log.i("Onboarding activity", "Change location settings request cancelled");
                    break;
                default:
                    break;
            }
        }
    }

    private void buildFlow(){
        //Initialize conditional fragments
        if(defaultFlow){
            try {
                onBoardingFragPermissionsClass = OnboardingPermissionFrag.class;
                onBoardingFragPermissions = (Fragment) onBoardingFragPermissionsClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                onBoardingFragPermissionsClass = OnboardingPermissionFrag.class;
                onBoardingFragPermissions = (Fragment) onBoardingFragPermissionsClass.newInstance();

                onBoardingFragSettingsClass = OnboardingSettingsFrag.class;
                onBoardingFragSettings = (Fragment) onBoardingFragSettingsClass.newInstance();

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //Setup the Onboarding flow
        if(defaultFlow){
            //Create slide 1
//            addSlide(new FragmentSlide.Builder()
//                    .backgroundDark(R.color.colorBlue)
//                    .background(R.color.colorBlueLight)
//                    .fragment(R.layout.onboarding_frag_one)
//                    .build());

            //Create slide 2
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(R.layout.onboarding_frag_two)
                    .build());

            //Create slide 3
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(R.layout.onboarding_frag_three)
                    .build());

            //Create slide 4
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(R.layout.onboarding_frag_four)
                    .build());

            //Create slide 5
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(R.layout.onboarding_frag_five)
                    .build());

            //Create slide 6
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(onBoardingFragPermissions)
                    .build());

            //Create slide 7
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(R.layout.onboarding_frag_done)
                    .build());

            setSkipEnabled(false);

            setNavigationPolicy(new NavigationPolicy() {
                @Override
                public boolean canGoForward(int position) {
                    switch(position){
                        case 0:
                            return true;
                        case 1:
                            return true;
                        case 2:
                            return true;
                        case 3:
                            return true;
                        case 4:
                            return true;
                        case 5:
                            return false;
                        case 6:
                            return true;
                    }
                    return true;
                }

                @Override
                public boolean canGoBackward(int position) {
                    switch (position){
                        case 0:
                            return false;
                        case 1:
                            return true;
                        case 2:
                            return true;
                        case 3:
                            return true;
                        case 4:
                            return true;
                        case 5:
                            return true;
                        case 6:
                            return true;
                    }
                    return false;
                }
            });
        }
        else{
            //Create slide 1
//            addSlide(new FragmentSlide.Builder()
//                    .backgroundDark(R.color.colorBlue)
//                    .background(R.color.colorBlueLight)
//                    .fragment(R.layout.onboarding_frag_one)
//                    .build());

            //Create slide 2
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(R.layout.onboarding_frag_two)
                    .build());

            //Create slide 3
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(R.layout.onboarding_frag_three)
                    .build());

            //Create slide 4
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(R.layout.onboarding_frag_four)
                    .build());

            //Create slide 5
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(R.layout.onboarding_frag_five)
                    .build());

            //Create slide 6
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(onBoardingFragPermissions)
                    .build());

            //Create slide 7
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(onBoardingFragSettings)
                    .build());

            //Create slide 8
            addSlide(new FragmentSlide.Builder()
                    .backgroundDark(R.color.colorBlue)
                    .background(R.color.colorBlueLight)
                    .fragment(R.layout.onboarding_frag_done)
                    .build());

            setSkipEnabled(false);

            setNavigationPolicy(new NavigationPolicy() {
                @Override
                public boolean canGoForward(int position) {
                    switch(position){
                        case 0:
                            return true;
                        case 1:
                            return true;
                        case 2:
                            return true;
                        case 3:
                            return true;
                        case 4:
                            return true;
                        case 5:
                            return false;
                        case 6:
                            return false;
                        case 7:
                            return true;
                    }
                    return true;
                }

                @Override
                public boolean canGoBackward(int position) {
                    switch (position){
                        case 0:
                            return false;
                        case 1:
                            return true;
                        case 2:
                            return true;
                        case 3:
                            return true;
                        case 4:
                            return true;
                        case 5:
                            return true;
                        case 6:
                            return true;
                        case 7:
                            return true;
                    }
                    return false;
                }
            });
        }
    }
}
