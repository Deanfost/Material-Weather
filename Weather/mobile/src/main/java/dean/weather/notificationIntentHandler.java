package dean.weather;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by DeanF on 12/14/2016.
 */

public class notificationIntentHandler extends IntentService{

    public notificationIntentHandler() {
        super(null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("NotifIntentHndlr", "Started");
//        //When instantiated, clear the back stack if there is one, and then start a new instance of introActivity
//        Intent mainIntent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("Restart", true);
//        startActivity(mainIntent);
//        stopSelf();
        Intent restartIntent = this.getPackageManager()
                .getLaunchIntentForPackage(this.getPackageName() );
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, restartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1, pendingIntent);
        System.exit(2);
    }
}
