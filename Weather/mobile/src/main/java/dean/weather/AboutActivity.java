package dean.weather;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.FirebaseApp;

import java.util.List;

/**
 * Created by Dean Foster on 2/7/2017.
 */

public class AboutActivity extends AppCompatActivity {
    LinearLayout parentLayout;
    LinearLayout upperLayout;
    RelativeLayout rateLayout;
    RelativeLayout licenseLayout;
    RelativeLayout srcLayout;
    RelativeLayout donateLayout;
    ImageView rateIcon;
    ImageView licenseIcon;
    ImageView srcIcon;
//    ImageView donateIcon;
    int setID = MainActivity.setID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        if(savedInstanceState != null){
            //Restore the setID from bundle
            setID = savedInstanceState.getInt("setID");
        }

        setContentView(R.layout.activity_about);

        Toolbar aboutToolbar = (Toolbar) findViewById(R.id.aboutToolbar);
        setSupportActionBar(aboutToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About");

        //References
        parentLayout = (LinearLayout) findViewById(R.id.aboutParentLayout);
        upperLayout = (LinearLayout) findViewById(R.id.aboutUpperLayout);
        rateLayout = (RelativeLayout) findViewById(R.id.aboutRateLayout);
        licenseLayout = (RelativeLayout) findViewById(R.id.aboutAttrLayout);
        srcLayout = (RelativeLayout) findViewById(R.id.aboutSrcLayout);
//        donateLayout = (RelativeLayout) findViewById(R.id.aboutDonateLayout);
        rateIcon = (ImageView) findViewById(R.id.aboutRateIcon);
        licenseIcon = (ImageView) findViewById(R.id.aboutAttrIcon);
        srcIcon = (ImageView) findViewById(R.id.aboutSrcIcon);
//        donateIcon = (ImageView) findViewById(R.id.aboutDonateIcon);

        //Set the color of the layout
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorBlueDark)));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBlueDark)));
        window.setStatusBarColor(getResources().getColor(R.color.colorBlueDark));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean(getString(R.string.theme_change_key), false)){
            Log.i("aboutActivity", "Theme change enabled");

            switch (setID){
                case 0:
                    this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorYellowDark)));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorYellowDark)));
                    upperLayout.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                    rateIcon.setColorFilter(getResources().getColor(R.color.colorYellow));
                    licenseIcon.setColorFilter(getResources().getColor(R.color.colorYellow));
                    srcIcon.setColorFilter(getResources().getColor(R.color.colorYellow));
//                    donateIcon.setColorFilter(getResources().getColor(R.color.colorYellow));
                    window.setStatusBarColor(getResources().getColor(R.color.colorYellowDark));
                    break;
                case 1:
                    this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorBlueDark)));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBlueDark)));
                    upperLayout.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                    rateIcon.setColorFilter(getResources().getColor(R.color.colorBlue));
                    licenseIcon.setColorFilter(getResources().getColor(R.color.colorBlue));
                    srcIcon.setColorFilter(getResources().getColor(R.color.colorBlue));
//                    donateIcon.setColorFilter(getResources().getColor(R.color.colorBlue));
                    window.setStatusBarColor(getResources().getColor(R.color.colorBlueDark));
                    break;
                case 2:
                    this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorOrangeDark)));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOrangeDark)));
                    upperLayout.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                    rateIcon.setColorFilter(getResources().getColor(R.color.colorOrange));
                    licenseIcon.setColorFilter(getResources().getColor(R.color.colorOrange));
                    srcIcon.setColorFilter(getResources().getColor(R.color.colorOrange));
//                    donateIcon.setColorFilter(getResources().getColor(R.color.colorOrange));
                    window.setStatusBarColor(getResources().getColor(R.color.colorOrangeDark));
                    break;
                case 3:
                    this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorPurpleDark)));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPurpleDark)));
                    upperLayout.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                    rateIcon.setColorFilter(getResources().getColor(R.color.colorPurple));
                    licenseIcon.setColorFilter(getResources().getColor(R.color.colorPurple));
                    srcIcon.setColorFilter(getResources().getColor(R.color.colorPurple));
//                    donateIcon.setColorFilter(getResources().getColor(R.color.colorPurple));
                    window.setStatusBarColor(getResources().getColor(R.color.colorPurpleDark));
                    break;
            }
        }
        else{
            Log.i("aboutActivity", "Theme change disabled");
        }
    }

    @Override
    protected void onResume() {
        //Click listeners
        rateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("aboutActivity", "rate clicked");
                boolean marketFound = false;
                String appId = getPackageName();
                Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId));
                final List<ResolveInfo> otherApps = getPackageManager().queryIntentActivities(rateIntent, 0);
                for (ResolveInfo otherApp: otherApps) {
                    //Find the actual google play app
                    if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {
                        ActivityInfo otherAppActivity = otherApp.activityInfo;
                        ComponentName componentName = new ComponentName(otherAppActivity.applicationInfo.packageName, otherAppActivity.name);
                        rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        rateIntent.setComponent(componentName);
                        startActivity(rateIntent);
                        marketFound = true;
                        break;
                    }
                }

                //If there is no google play app, open the browser
                if(!marketFound){
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+appId));
                    startActivity(webIntent);
                }
            }
        });

        licenseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("aboutActivity", "attr clicked");
                Intent licensesIntent = new Intent(AboutActivity.this, AttributionActivity.class);
                startActivity(licensesIntent);
            }
        });

        srcLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("aboutActivity", "src clicked");
                //Move to darksky.net
                String url = "https://darksky.net/poweredby/";
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(url));
                startActivity(webIntent);
            }
        });

//        donateLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("aboutActivity", "donate clicked");
//                Snackbar snackbar = Snackbar
//                        .make(parentLayout, "Coming soon.", Snackbar.LENGTH_SHORT);
//                snackbar.show();
//            }
//        });
        super.onResume();
    }

    @Override
    protected void onPause() {
        rateLayout.setOnClickListener(null);
        licenseLayout.setOnClickListener(null);
        srcLayout.setOnClickListener(null);
//        donateLayout.setOnClickListener(null);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("About Activity", "Finishing");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("onSaveInstanceState", "Called - About");
        outState.putInt("setID", setID);
        super.onSaveInstanceState(outState);
    }
}
