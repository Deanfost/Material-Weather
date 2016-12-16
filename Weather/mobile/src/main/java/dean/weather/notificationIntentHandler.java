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
        //When instantiated, clear the back stack if there is one, and then start a new instance of introActivity
        Intent mainIntent = new Intent(this, IntroActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(mainIntent);
        stopSelf();
    }
}
