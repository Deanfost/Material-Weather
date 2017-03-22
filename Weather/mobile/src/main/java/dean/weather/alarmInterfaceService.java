package dean.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.FirebaseApp;

/**
 * Created by Dean on 12/25/2016.
 */

public class alarmInterfaceService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        if(intent != null){
            if(intent.getExtras() != null){
                //Ongoing notif
                if(intent.getExtras().containsKey("repeatNotif")){
                    //Start the repeating notification
                    if(intent.getExtras().getBoolean("repeatNotif")){
                        Log.i("AlarmInterfaceService", "Starting repeatNotif - from settings");
                        Intent ongoingServiceIntent = new Intent(this, ongoingNotifService.class);
                        ongoingServiceIntent.putExtra("pull", true);
                        PendingIntent ongoingAlarmIntent = PendingIntent.getService(this, 0, ongoingServiceIntent, 0);

                        //Setup an alarm to fire immediately and then every 20 minutes after
                        alarmManager.cancel(ongoingAlarmIntent);
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1200000, 1200000, ongoingAlarmIntent);

                        //Start the first pull
                        Intent firstPull = new Intent(this, ongoingNotifService.class);
                        firstPull.putExtra("pull", true);
                        startService(firstPull);
                    }
                    //Cancel the repeating notification
                    else{
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                        boolean ongoingEnabled = preferences.getBoolean(getResources().getString(R.string.ongoing_notif_key), false);
                        boolean alertEnabled = preferences.getBoolean(getResources().getString(R.string.alert_notif_key), false);
                        //If both notifs have been disabled, cancel the alarm for the background service
                        if(!ongoingEnabled && !alertEnabled){
                            Log.i("AlarmInterfaceService", "Cancelling repeatNotif");
                            Intent ongoingServiceIntent = new Intent(this, ongoingNotifService.class);
                            ongoingServiceIntent.putExtra("pull", true);
                            PendingIntent ongoingAlarmIntent = PendingIntent.getService(this, 0, ongoingServiceIntent, 0);

                            alarmManager.cancel(ongoingAlarmIntent);

//                            //Kill the notification service
//                            Intent notSticky = new Intent(this, ongoingNotifService.class);
//                            notSticky.putExtra("notSticky", false);//Sends an intent to the service to return START_NOT_STICKY, which will allow it to die
//                            startService(notSticky);
                        }
                        else{
                            Log.i("AlarmInterfaceService", "Both services are not disabled");
                        }
                    }
                }

//                //Alert notif
//                if(intent.getExtras().containsKey("alertNotif")){
//                    //Start the alert notification
//                    if(intent.getExtras().getBoolean("alertNotif")){
//                        Intent alertNotifIntent = new Intent(this, alertNotifService.class);
//                        PendingIntent alertAlarmIntent = PendingIntent.getService(this, 0, alertNotifIntent, 0);
//
//                        Log.i("AlarmInterfaceService", "Starting alertNotif");
//                        //Setup an alarm to pull every half hour for alerts
//                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_HOUR, AlarmManager.INTERVAL_HALF_HOUR, alertAlarmIntent);
//
//                        //Start the first pull
//                        Intent firstPull = new Intent(this, alertNotifService.class);
//                        this.startService(firstPull);
//                    }
//                    else{
//                        //Stop the alert notification
//                        Intent alertNotifIntent = new Intent(this, alertNotifService.class);
//                        PendingIntent alertAlarmIntent = PendingIntent.getService(this, 0, alertNotifIntent, 0);
//
//                        Log.i("AlarmInterfaceService", "Stopping alertNotif");
//                        alarmManager.cancel(alertAlarmIntent);
//                    }
//                }
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}