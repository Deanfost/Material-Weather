package dean.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by DeanF on 12/11/2016.
 */

public class bootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            //Start the notification service
            Intent notificationIntent = new Intent(context, notificationService.class);
            context.startActivity(notificationIntent);
        }
    }
}
