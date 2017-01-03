package dean.weather;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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

        //Create test notif
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_finish)
                        .setContentTitle("Summary notif")
                        .setContentText("Get notified bitch");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(435, mBuilder.build());
        stopSelf();
    }
}