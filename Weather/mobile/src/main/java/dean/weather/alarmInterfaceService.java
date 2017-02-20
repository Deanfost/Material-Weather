package dean.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

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
//        ongoingServiceIntent.setAction("dean.weather.alarmRepeating");
        PendingIntent ongoingAlarmIntent = PendingIntent.getService(this, 0, ongoingServiceIntent, 0);
        //Alert notif
        Intent alertNotifIntent = new Intent(this, alertNotifService.class);
//        alertNotifIntent.setAction("dean.weather.alarmAlerts");
        PendingIntent alertAlarmIntent = PendingIntent.getService(this, 0, alertNotifIntent, 0);

        if(intent != null){
            if(intent.getExtras() != null){
                //Ongoing notif
                if(intent.getExtras().containsKey("repeatNotif")){
                    //Start the repeating notification
                    if(intent.getExtras().getBoolean("repeatNotif")){
                        Log.i("AlarmInterfaceService", "Starting repeatNotif - from settings");

                        //Setup an alarm to fire in 30 mins
                        alarmManager.cancel(ongoingAlarmIntent);
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1800000, AlarmManager.INTERVAL_HALF_HOUR, ongoingAlarmIntent);

                        //Start the first pull
                        Intent firstPull = new Intent(this, ongoingNotifService.class);
                        firstPull.putExtra("pull", true);
                        this.startService(firstPull);
                    }
                    //Cancel the repeating notification
                    else{
                        Log.i("AlarmInterfaceService", "Cancelling repeatNotif");
                        //Cancel the alarm
                        alarmManager.cancel(ongoingAlarmIntent);
                        //Kill the notification service
                        Intent notSticky = new Intent(this, ongoingNotifService.class);
                        notSticky.putExtra("notSticky", false);//Sends an intent to the service to return START_NOT_STICKY, which will allow it to die
                        startService(notSticky);
                    }
                }

                //Summary notif
//                if(intent.getExtras().containsKey("summaryNotif")){
//                    //Start the summary notification
//                    if(intent.getExtras().getBoolean("summaryNotif")){
//                        Log.i("AlarmInterfaceService", "Setting summaryNotif alarm");
//                        //Setup an alarm to fire at the set time to start the service
//                        Long alarmTime = intent.getExtras().getLong("alarmTime");
//                        Date date = new Date(alarmTime);
//                        DateFormat formatter = new SimpleDateFormat("HH:mm");
//                        String dateFormatted = formatter.format(date);
//                        Integer hh = Integer.valueOf(dateFormatted.substring(0, 2));
//                        Integer mm = Integer.valueOf(dateFormatted.substring(3));
//                        Log.i("hh", hh.toString());
//                        Log.i("mm", mm.toString());
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis(System.currentTimeMillis());
//                        calendar.set(Calendar.HOUR_OF_DAY, hh);
//                        calendar.set(Calendar.MINUTE, mm);
//                        //If the calendar time is greater than the current time
//                        if((calendar.getTimeInMillis() > System.currentTimeMillis())){
//                            //Start the alarm
//                            alarmManager.cancel(alertAlarmIntent);
//                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 300000, alertAlarmIntent);
//                            //Persist the alarm in sharedPreferences to be started again if the system were to restart
//                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//                            SharedPreferences.Editor editor = sharedPref.edit();
//                            editor.putLong(getString(R.string.summary_alarm_key), calendar.getTimeInMillis());
//                            editor.apply();
//                            stopSelf();
//                        }
//                        else{
//                            //Start the alarm
//                            Log.i("alarmIntService", "Time is in the past");
//                            calendar.add(Calendar.MINUTE, 5);
//                            alarmManager.cancel(alertAlarmIntent);
//                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 300000, alertAlarmIntent);
//                            //Persist the alarm in sharedPreferences to be started again if the system were to restart
//                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//                            SharedPreferences.Editor editor = sharedPref.edit();
//                            editor.putLong(getString(R.string.summary_alarm_key), calendar.getTimeInMillis());
//                            editor.apply();
//                            stopSelf();
//                        }
//                    }
//                    //Stop the summary notification
//                    else{
//                        Log.i("AlarmInterfaceService", "Cancelling summaryNotif");
//                        //Cancel the alarm
//                        alarmManager.cancel(alertAlarmIntent);
//                        //Remove the alarm from the key-value pair
//                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putLong(getString(R.string.summary_alarm_key), 0);
//                        editor.apply();
//                        stopSelf();
//                    }
//                }

                //Alert notif
                if(intent.getExtras().containsKey("alertNotif")){
                    //Start the alert notification
                    if(intent.getExtras().getBoolean("alertNotif")){
                        Log.i("AlarmInterfaceService", "Starting alertNotif");
                        //Setup an alarm to pull in an hour for alerts
                        alarmManager.cancel(alertAlarmIntent);
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR, alertAlarmIntent);

                        //Start the first pull
                        Intent firstPull = new Intent(this, alertNotifService.class);
                        this.startService(firstPull);
                    }
                    else{
                        //Stop the alert notification
                        Log.i("AlarmInterfaceService", "Stopping alertNotif");
                        alarmManager.cancel(alertAlarmIntent);
                    }
                }

            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}