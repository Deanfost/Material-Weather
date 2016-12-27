package dean.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Dean on 12/25/2016.
 */

public class alarmInterface extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Initialize intents for later use
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, notificationService.class);
        serviceIntent.putExtra("pull", true);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, serviceIntent, 0);

        if(intent != null){
            if(intent.getExtras() != null){

                if(intent.getExtras().containsKey("repeatNotif")){
                    //Start the repeating notification
                    if(intent.getExtras().getBoolean("repeatNotif")){
                        Log.i("alarmInterface", "Starting repeatNotif");

                        //Setup an alarm to fire in one hour, and then every hour after that
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR,
                                AlarmManager.INTERVAL_HOUR, alarmIntent);

                        //Start the first pull
                        this.startService(serviceIntent);
                    }
                    //Cancel the repeating notification
                    else{
                        Log.i("alarmInterface", "Cancelling repeatNotif");
                        //Cancel the alarm
                        alarmManager.cancel(alarmIntent);
                        //Kill the notification service
                        this.stopService(new Intent(this, notificationService.class));
                    }
                }

                if(intent.getExtras().containsKey("summaryNotif")){
                    //Start the summary notification
                    if(intent.getExtras().getBoolean("summaryNotif")){
                        Log.i("alarmInterface", "Starting repeatNotif");
                    }
                }

                if(intent.getExtras().containsKey("alertNotif")){
                    //Start the alert notification
                    if(intent.getExtras().getBoolean("alertNotif")){
                        Log.i("alarmInterface", "Starting alertNotif");
                    }
                }
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}