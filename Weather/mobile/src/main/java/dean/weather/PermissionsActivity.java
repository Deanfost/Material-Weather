package dean.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

import static dean.weather.IntroActivity.PERMISSIONS_REQUEST;

/**
 * Created by Dean Foster on 3/2/2017.
 */

public class PermissionsActivity extends AppCompatActivity {
    Button btnGrant;
    static Context passedContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.need_permission_activity);

        btnGrant = (Button) findViewById(R.id.btnNeedAccess);

        //Customize window
        Window window = PermissionsActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(PermissionsActivity.this.getResources().getColor(R.color.colorBlueDark));

        //Button click listener
        btnGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start the resolution
                ActivityCompat.requestPermissions(PermissionsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
            }
        });

        //Change the preferences to reflect both services being disabled
        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.putBoolean(getString(R.string.ongoing_notif_key), false);
        editor.putBoolean(getString(R.string.alert_notif_key), false);
        editor.apply();

        //End the ongoing alarm
        Intent stopOngoing = new Intent(this, alarmInterfaceService.class);
        stopOngoing.putExtra("repeatNotif", false);
        startService(stopOngoing);

        //End the alerts alarm
        Intent stopAlerts = new Intent(this, alarmInterfaceService.class);
        stopAlerts.putExtra("alertNotif", false);
        startService(stopAlerts);
    }

    /**
     * Collects context from Main.
     * @param context
     */
    public static void passContext(Context context){
        passedContext = context;
    }

    @Override
    public void onBackPressed() {
        //Override onBackPressed
    }

    //Handle request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //If the user didn't cancel the dialog
        if (grantResults.length > 0) {
            //Location permission and internet permission
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //If the user granted permissions, begin normal operations in mainActivity
                Log.i("PermissionsActivity", "Refreshing main");
                finish();
                MainActivity mainActivity = (MainActivity) passedContext;
                mainActivity.refreshNoLoad();
            }
        }
        else{
            Log.i("PermissionsActivity", "User cancelled dialog");
        }
    }
}
