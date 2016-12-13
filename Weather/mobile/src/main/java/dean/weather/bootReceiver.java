package dean.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by DeanF on 12/11/2016.
 */

public class bootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.i("bootReceiver", "Starting service");
            //Start the notification service
            Intent notificationIntent = new Intent(context, notificationService.class);
            context.startService(notificationIntent);
        }
    }
}
