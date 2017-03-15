package dean.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.FirebaseApp;

/**
 * Created by DeanF on 12/11/2016.
 */

public class bootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseApp.initializeApp(context);

        Log.i("BootReceiver", "broadcastReceived");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //If either the repeat notif, summary notif, or alert notif is enabled
        if(prefs.getBoolean("key_notif_follow", false) || prefs.getBoolean("key_notif_alert", false)) {
            //Tell the AlarmInterfaceService class to start the notifService, and to setup an alarm
            //Put the appropriate extras
            //Repeating notif
            if(prefs.getBoolean("key_notif_follow", false) || prefs.getBoolean("key_notif_alert", false)){
                Intent interfaceIntent = new Intent(context, alarmInterfaceService.class);
                interfaceIntent.putExtra("repeatNotif", true);
                Log.i("BootReceiver", "repeatNotif enabled");
                context.startService(interfaceIntent);
            }

            //Alerts notif
//            if(prefs.getBoolean("key_notif_alert", false)){
//                //Restart the alerts pull
//                Intent interfaceIntent = new Intent(context, alarmInterfaceService.class);
//                interfaceIntent.putExtra("alertNotif", true);
//                Log.i("BootReceiver", "alertNotif enabled");
//                context.startService(interfaceIntent);
//            }
        }
        else{
            Log.i("BootReceiver", "All services disabled");
        }
    }
}