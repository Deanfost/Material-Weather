package dean.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by DeanF on 12/11/2016.
 */

public class bootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("bootReceiver", "broadcastReceived");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //If either the repeat notif, summary notif, or alert notif is enabled
        if(prefs.getBoolean("key_notif_follow", false) || prefs.getBoolean("key_notif_summary", false) || prefs.getBoolean("key_notif_alert", false)) {
            //Tell the alarmInterfaceService class to start the notifService, and to setup an alarm
            Log.i("bootReceiver", "Starting alarmInterfaceService");
            Intent interfaceIntent = new Intent(context, alarmInterfaceService.class);
            //Put the appropriate extras
            //Repeating notif
            if(prefs.getBoolean("key_notif_follow", false)){
                interfaceIntent.putExtra("repeatNotif", true);
                Log.i("bootReceiver", "repeatNotif enabled");
            }
            //Summary notif
            if(prefs.getBoolean("key_notif_summary", false)){
                interfaceIntent.putExtra("summaryNotif", true);
                Log.i("bootReceiver", "summaryNotif enabled");
                //Look at key-value pairs to see if there is an alarm set, and determine if it was supposed to be fired already
                Long alarmTime = prefs.getLong("key_summary_alarm", 0);
                Log.i("alarmTime", alarmTime.toString());
                if(alarmTime < System.currentTimeMillis() && alarmTime != 0){
                    Log.i("bootReciever", "Alarm overdue");
                    //Launch the service
                    Intent summaryServiceIntent = new Intent(context, summaryNotifService.class);
                    context.startService(summaryServiceIntent);
                    //TODO - RESET ALARM?
                }
                else{
                    Log.i("bootReceiver", "Alarm not overdue");
                }

            }
            //Alert notif
            if(prefs.getBoolean("key_notif_alert", false)){
                interfaceIntent.putExtra("alertNotif", true);
                Log.i("bootReceiver", "alertNotif enabled");
            }
            context.startService(interfaceIntent);
        }
        else{
            Log.i("bootReceiver", "All services disabled");
        }
    }
}
