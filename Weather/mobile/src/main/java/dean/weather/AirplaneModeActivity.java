package dean.weather;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

/**
 * Created by Dean Foster on 3/2/2017.
 */

public class AirplaneModeActivity extends AppCompatActivity {
    Button btnRetry;
    static Context passedContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.airplane_mode_activity);

        btnRetry = (Button) findViewById(R.id.btnRetryAirplane);

        //Customize window
        Window window = AirplaneModeActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(AirplaneModeActivity.this.getResources().getColor(R.color.colorBlueDark));

        //Button click listener
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("AirplaneModeAct", "btn clicked");
                //Check for airplane mode
                if(Settings.System.getInt(AirplaneModeActivity.this.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0){
                    //Airplane mode on, do nothing
                    Log.i("AirplaneModeAct", "Airplane mode still on");
                }
                else{
                    //Airplane mode off, go back to Main, refresh
                    Log.i("AirplaneModeAct", "Airplane mode off, refreshing");
                    finish();
                    MainActivity mainActivity = (MainActivity) passedContext;
                    mainActivity.refreshNoLoad();
                }
            }
        });
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
}