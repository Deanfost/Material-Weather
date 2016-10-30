package dean.weather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by DeanF on 10/21/2016.
 */

public class introActivity extends AppCompatActivity {
    static int LOCATION_PERMISSIONS_REQUEST;
    Button btnNeedAccess;

    @Override
    protected void onStart() {
        super.onStart();
        //Decide on which activity to launch
        decideActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //Handle request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If the user didn't cancel the dialog
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //If the user allowed access, then launch the main activity
                Intent startMain = new Intent(this, MainActivity.class);
                startActivity(startMain);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
    }

    private void decideActivity(){
        LOCATION_PERMISSIONS_REQUEST = 42;
        //Make sure we can access the user's location
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        //Launch main if we do
        if(locationPermission == PackageManager.PERMISSION_GRANTED){
            Intent startMain = new Intent(this, MainActivity.class);
            startActivity(startMain);
            overridePendingTransition(0, 0);
        }
        else{
            //If we don't have permission, it may be first start
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String firstStart = sharedPref.getString(getResources().getString(R.string.first_launch_key), "0");
            if(firstStart.equals("0")){
                //It is first start, and we need to onboard the user, so we launch the onboarding activity
                Intent onboardingIntent = new Intent(this, onboardingActivity.class);
                startActivity(onboardingIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Log.i("Intent", "Onboarding");

            }
            else{
                //It isn't first start, but we need to request permission for location
                Log.i("Intent", "Permissions");
                setContentView(R.layout.need_permission_activity);
                //Setup references and customizations for layout
                Window window = this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(R.color.colorBlue));
                btnNeedAccess = (Button) findViewById(R.id.btnNeedAccess);
                btnNeedAccess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Request location permission
                        ActivityCompat.requestPermissions(introActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);
                    }
                });
            }
        }
    }
}
