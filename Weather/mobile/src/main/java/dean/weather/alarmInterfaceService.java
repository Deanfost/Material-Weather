package dean.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Initialize intents and alarms for later use
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        //Ongoing notif
        Intent ongoingServiceIntent = new Intent(this, ongoingNotifService.class);
        ongoingServiceIntent.putExtra("pull", true);
        PendingIntent ongoingAlarmIntent = PendingIntent.getService(this, 0, ongoingServiceIntent, 0);
        //Summary notif
        Intent summaryNotifIntent = new Intent(this, summaryNotifService.class);
        PendingIntent summaryAlarmIntent = PendingIntent.getService(this, 0, summaryNotifIntent, 0);

        if(intent != null){
            if(intent.getExtras() != null){

                //Ongoing notif
                if(intent.getExtras().containsKey("repeatNotif")){
                    //Start the repeating notification
                    if(intent.getExtras().getBoolean("repeatNotif")){
                        Log.i("alarmInterfaceService", "Starting repeatNotif");

                        //Setup an alarm to fire in one hour, and then every hour after that
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR,
                                AlarmManager.INTERVAL_HOUR, ongoingAlarmIntent);

                        //Start the first pull
                        this.startService(ongoingServiceIntent);
                        stopSelf();
                    }
                    //Cancel the repeating notification
                    else{
                        Log.i("alarmInterfaceService", "Cancelling repeatNotif");
                        //Cancel the alarm
                        alarmManager.cancel(ongoingAlarmIntent);
                        //Kill the notification service
                        Intent notSticky = new Intent(this, ongoingNotifService.class);
                        notSticky.putExtra("notSticky", false);//Sends an intent to the service to return START_NOT_STICKY, which will allow it to die
                        startService(notSticky);
                        stopSelf();
                    }
                }

                //Summary notif
                if(intent.getExtras().containsKey("summaryNotif")){
                    //Start the summary notification
                    if(intent.getExtras().getBoolean("summaryNotif")){
                        Log.i("alarmInterfaceService", "Setting summaryNotif alarm");
                        //Setup an alarm to fire at the set time to start the service
                        Long alarmTime = intent.getExtras().getLong("alarmTime");
                        Date date = new Date(alarmTime);
                        DateFormat formatter = new SimpleDateFormat("HH:mm");
                        String dateFormatted = formatter.format(date);
                        Integer hh = Integer.valueOf(dateFormatted.substring(0, 2));
                        Log.i("hh", hh.toString());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, hh);
                        if(!(alarmTime < System.currentTimeMillis())){
                            alarmManager.cancel(summaryAlarmIntent);
                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTime, AlarmManager.INTERVAL_DAY, summaryAlarmIntent);
                            stopSelf();
                        }
                        else{
                            Log.i("alarmIntService", "Time is in the past");
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                            alarmManager.cancel(summaryAlarmIntent);
                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, , AlarmManager.INTERVAL_DAY, summaryAlarmIntent);
                            stopSelf();
                        }
                    }
                    //Stop the summary notification
                    else{
                        Log.i("alarmInterfaceService", "Cancelling summaryNotif");
                        //Cancel the alarm
                        alarmManager.cancel(summaryAlarmIntent);
                        stopSelf();
                    }
                }

//                if(intent.getExtras().containsKey("alertNotif")){
//                    //Start the alert notification
//                    if(intent.getExtras().getBoolean("alertNotif")){
//                        Log.i("alarmInterfaceService", "Starting alertNotif");
//                    }
//                }
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}