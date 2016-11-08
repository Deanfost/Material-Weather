package dean.weather;

import android.app.IntentService;
import android.content.Intent;
import android.location.Geocoder;

import java.util.Locale;

/**
 * Created by DeanF on 11/8/2016.
 */

public class FetchAddressIntentService extends IntentService {

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    }
}
