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

public class IntroActivity extends AppCompatActivity {
    static int PERMISSIONS_REQUEST;
    Button btnNeedAccess;

    @Override
    protected void onStart() {
        super.onStart();
        //Decide on which activity to launch
        Log.i("IntroActivity", "Instantiated");
        decideActivity();
    }

    //Handle request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If the user didn't cancel the dialog
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("Permissions", "Permission granted");
                //Launch main
                Intent startMain = new Intent(this, MainActivity.class);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(startMain);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }
    }

    /**
     * Chooses which activity to launch, depending on permissions and first launch.
     */
    private void decideActivity(){
        PERMISSIONS_REQUEST = 42;
        //Make sure we can access the user's location
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        //Launch main if we do
        if(locationPermission == PackageManager.PERMISSION_GRANTED && locationPermission == PackageManager.PERMISSION_GRANTED){
            Log.i("IntroActivity", "Launching main");
            Intent startMain = new Intent(this, MainActivity.class);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(startMain);
            overridePendingTransition(0, 0);
            finish();
        }
        else{
            //If we don't have permission, it may be first start
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String firstStart = sharedPref.getString(getResources().getString(R.string.first_launch_key), "0");
            if(firstStart.equals("0")){
                //It is first start, and we need to onboard the user, so we launch the onboarding activity
                Intent onboardingIntent = new Intent(this, OnboardingActivity.class);
                startActivity(onboardingIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Log.i("Intent", "Onboarding");
                finish();
            }
            else{
                //It isn't first start, but we need to request permission for location
                Log.i("Intent", "Permissions");
                //Launch permissions needed activity
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
                        ActivityCompat.requestPermissions(IntroActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET}, PERMISSIONS_REQUEST);
                    }
                });
            }
        }
    }
}
