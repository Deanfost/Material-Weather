package dean.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        //If either the repeat notif, summary notif, or alert notif is enabled
        if(prefs.getBoolean("key_notif_follow", false) || prefs.getBoolean("key_notif_summary", false) || prefs.getBoolean("key_notif_alert", false)) {
            //Tell the alarmInterface class to start the notifService, and to setup an alarm
            Log.i("bootReceiver", "Starting alarmInterface");
            Intent interfaceIntent = new Intent(context, alarmInterface.class);
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
