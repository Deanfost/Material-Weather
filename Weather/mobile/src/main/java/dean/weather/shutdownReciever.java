package dean.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dean Foster on 1/15/2017.
 */

public class shutdownReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.i("shutdownReceiver", "Broadcast received");
//        //On shutdown, add one full interval to the last saved summary alarm time, to determine if the alarm is overdue on startup
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = prefs.edit();
//        Long prevFireTime = prefs.getLong("key_summary_alarm", 0);
//        boolean summaryEnabled = prefs.getBoolean("key_notif_summary", false);
//        //If the alarm has been set before
//        if(summaryEnabled){
//            if(prevFireTime != 0){
//                Date date = new Date(prevFireTime);
//                DateFormat formatter = new SimpleDateFormat("HH:mm");
//                String dateFormatted = formatter.format(date);
//                Integer hh = Integer.valueOf(dateFormatted.substring(0, 2));
//                Integer mm = Integer.valueOf(dateFormatted.substring(3));
//                Log.i("prev hh", hh.toString());
//                Log.i("prev mm", mm.toString());
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(prevFireTime);
//                calendar.set(Calendar.HOUR_OF_DAY, hh);
//                calendar.set(Calendar.MINUTE, mm);
//                //Add one full interval
//                calendar.add(Calendar.MINUTE, 5);
//                editor.putLong("key_summary_alarm", calendar.getTimeInMillis());
//                editor.apply();
//            }
//        }
    }
}