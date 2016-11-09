package dean.weather;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by DeanF on 11/8/2016.
 */

public class FetchAddressIntentService extends IntentService {
    //Define a geocoder object to fetch the address
    Geocoder geocoder;
    //Result receiver
    protected ResultReceiver receiver;

    //Constructor
    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        geocoder = new Geocoder(this, Locale.getDefault());
        String errorMsg = null;

        //Get the location
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        List<Address> addresses = null;
        receiver = new ResultReceiver(new android.os.Handler());

        try {
            //Get single address
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch (IOException ioException)
        {
            // Catch network or other I/O problems.
            errorMsg = "Service unavailable.";
            Log.e(TAG, errorMsg, ioException);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            // Catch invalid latitude or longitude values.
            errorMsg = "Invalid arguments.";
            Log.e(TAG, errorMsg + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        //Handle cases where no address was found
        if (addresses == null || addresses.size()  == 0)
        {
            if (errorMsg.isEmpty()) {
                errorMsg = "No addresses found.";
                Log.e(TAG, errorMsg);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMsg);
        } else
        {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "Address found.");
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }

    /**
     * Delivers result of Intent Service to calling class.
     * @param resultCode
     * @param message
     */
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        receiver.send(resultCode, bundle);
    }
}