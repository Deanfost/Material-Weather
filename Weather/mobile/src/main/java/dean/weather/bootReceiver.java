package dean.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by DeanF on 12/11/2016.
 */

public class bootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.i("bootReceiver", "Starting service");

            //Setup an alarm to schedule forecast pull tasks
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent serviceIntent = new Intent("dean.weather.alarm");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, serviceIntent, 0);

            //TODO - ENABLE/DISABLE THIS IN SETTINGS
            //Setup an alarm to fire immediately, and then every hour after that
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() - 60000,
                    AlarmManager.INTERVAL_HOUR, alarmIntent);
        }
    }
}
