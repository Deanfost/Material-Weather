package dean.weather;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by DeanF on 11/11/2016.
 */

public class ChangeLocationSettingsFragment extends Fragment {
    Button btnEnableLocationSettings;
    final int REQUEST_CHANGE_SETTINGS = 15;
    static Initializer sInitializer;
    LinearLayout changeLocationSettingsFragLayout;

    interface Initializer{
        void beginNormalOperations1();
    }

    //Obtain reference to calling activity
    public static void setInitializer(ChangeLocationSettingsFragment.Initializer initializer){
        sInitializer = initializer;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_location_settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Setup references
        changeLocationSettingsFragLayout = (LinearLayout) getView().findViewById(R.id.change_location_settings_fragment_layout);
        btnEnableLocationSettings = (Button) getView().findViewById(R.id.btnEnableLocationSettings);
        btnEnableLocationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Prompt the user to enable their location settings
                //Create location request
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(createLocationRequest());

                //Create location settings request to make sure the request is permitted
                PendingResult<LocationSettingsResult> locationSettingsResultPendingResult = LocationServices.SettingsApi.checkLocationSettings(MainActivity.googleApiClient, builder.build());

                //Check location settings
                locationSettingsResultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                        final Status locationStatus = locationSettingsResult.getStatus();
//                        final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                        switch (locationStatus.getStatusCode()){
                            case LocationSettingsStatusCodes.SUCCESS:
                                //Settings are fine, how did we get here?
                                Log.i("ChangeLocSettingsFrag", "Settings are fine");
                                break;

                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied, but this can be fixed
                                // by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    locationStatus.startResolutionForResult(getActivity(), REQUEST_CHANGE_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                //Location settings aren't satisfied, but there is no way to fix them. Do not show dialog.
                                Snackbar snackbar = Snackbar.make(changeLocationSettingsFragLayout, "Please enable location services in settings.", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                break;
                        }
                    }
                });
            }
        });
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
        //Check the result of the location settings change request
        if(requestCode == REQUEST_CHANGE_SETTINGS){
            switch (requestCode) {
                case REQUEST_CHANGE_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made, so move to loadingFragment and begin normal operations
                            try{
                                sInitializer.beginNormalOperations1();
                            }
                            catch (SecurityException e){
                                Log.e("LocationPermission", "Permission denied");
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            //Do nothing
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }
}
