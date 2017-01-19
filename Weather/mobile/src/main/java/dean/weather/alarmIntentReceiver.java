package dean.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Dean Foster on 1/18/2017.
 */

public class alarmIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().contains("dean.weather.alarmRepeating")){
            Log.i("alarmReceiver", "Repeating intent received");

        }
        if(intent.getAction().contains("dean.weather.alarmAlerts")){
            Log.i("alarmReceiver", "Alert intent received");

        }
    }
}
