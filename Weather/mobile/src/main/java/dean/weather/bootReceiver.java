package dean.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by DeanF on 12/11/2016.
 */

public class bootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("bootReceiver", "broadcastReceived");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getBoolean("key_notif_follow", false)){
            Log.i("bootReceiver", "Starting service");
            //Setup an alarm to schedule forecast pull tasks
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent serviceIntent = new Intent(context, notificationService.class);
            PendingIntent alarmIntent = PendingIntent.getService(context, 0, serviceIntent, 0);

            //Setup an alarm to fire in one hour, and then every hour after that
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR,
                    AlarmManager.INTERVAL_HOUR, alarmIntent);

            //Start the first pull
            context.startService(serviceIntent);
        }
        else{
            Log.i("bootReceiver", "serviceDisabled");
        }
    }
}
