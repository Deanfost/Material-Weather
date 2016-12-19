package dean.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by DeanF on 12/18/2016.
 */

public class alarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if(intent.getAction().equals("dean.weather.alarm.receiver")){
//            Log.i("alarmReceiver", "Received");
//            //Launch the notification service
//            Intent notifService = new Intent(context, notificationService.class);
//            context.startService(notifService);
//        }
    }
}
