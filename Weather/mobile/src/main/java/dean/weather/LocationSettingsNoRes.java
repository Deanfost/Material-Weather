package dean.weather;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Dean Foster on 3/2/2017.
 */

public class LocationSettingsNoRes extends AppCompatActivity {
    static Context passedContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_settings_change_activity_nobtn);

        //Customize window
        Window window = LocationSettingsNoRes.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(LocationSettingsNoRes.this.getResources().getColor(R.color.colorBlueDark));
    }

    @Override
    public void onBackPressed() {
        //Override on back pressed
    }
}