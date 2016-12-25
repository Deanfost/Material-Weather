package dean.weather;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Dean on 12/25/2016.
 */

public class summaryService extends IntentService {

    public summaryService() {
        super(null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Creates a summary weather summary notification at a certain time everyday
        Log.i("summaryService", "started");
        stopSelf();
    }
}
