package dean.weather;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

/**
 * Created by DeanF on 12/11/2016.
 */

public class notificationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    //Location settings change
    private final int REQUEST_CHANGE_SETTINGS = 15;

    //Address receiver
    protected Location lastLocation;//Location to pass to the address method

    //Google APIs
    private GoogleApiClient googleApiClient;
    private double latitude;
    private double longitude;

    //WeatherResponse
    private WeatherResponse pulledWeatherResponse;



    public notificationService() {
        super(null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("notifService", "Started");
        //Setup GoogleAPIClient
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();

        //TODO - CHECK TO SEE IF THE USER HAS A DEFAULT LOCATION SET/IF THE USER WANTS A LOCATION REPORT

    }

    /**
     * Creates notification with current weather data.
     */
    private void createNotification(){
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_collapsed);
        notificationView.setImageViewResource(R.id.notifCondIcon, R.drawable.ic_cloudy_white);
        notificationView.setTextViewText(R.id.notifCondition, "44\u00B0 - Cloudy");
        notificationView.setTextViewText(R.id.notifLocation, "Boston");
        notificationView.setTextViewText(R.id.notifBody, "Hi - 40° Lo - 22°");
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContent(notificationView)
                        .setSmallIcon(R.drawable.ic_cloudy_white);
        //Intent to go to main activity
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.putExtra("NotificationMsg", "Clicked");//Let onResume know to refresh data
        //Create backstack for intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //Add activity to the top of stack
        stackBuilder.addNextIntent(mainIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        notificationManager.notify(MainActivity.NOTIF_ID, notificationBuilder.build());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("NotifService", "GoogleAPIClient connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
