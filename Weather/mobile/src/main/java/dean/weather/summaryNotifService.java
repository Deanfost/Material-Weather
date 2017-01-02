package dean.weather;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Dean on 12/25/2016.
 */

public class summaryNotifService extends IntentService {

    public summaryNotifService() {
        super(null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Creates a summary weather summary notification at a set time everyday
        Log.i("summaryNotifService", "started");


        stopSelf();
    }
}
