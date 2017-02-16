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

public class NotificationIntentHandler extends IntentService{

    public NotificationIntentHandler() {
        super(null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("NotifIntentHndlr", "Started");
        //When instantiated, clear the back stack if there is one, and then start a new instance of introActivity
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        this.startActivity(mainIntent);
        stopSelf();
    }
}
