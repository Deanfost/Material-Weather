package dean.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by DeanF on 10/19/2016.
 */

public class decisionActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Decide whether or not to launch intro activity or go straight to main
        //Get key-value pair that determines if it is first launch or not
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //If it is 0, then this is first launch - open intro activity. If it is 1, then it is not, and it will launch the main activity.
        int firstLaunch = sharedPreferences.getInt(getString(R.string.first_launch_key), 0);
        Log.i("firstLaunch", String.valueOf(firstLaunch));
        if(firstLaunch == 0) {
            //Launch intro activity and clear the stack
            Intent introAct = new Intent(getApplicationContext(), introActivity.class);
            introAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(introAct);
            overridePendingTransition(0, 0);
        }
        else{
            //Launch main activity and clear the stack
            Intent mainAct = new Intent(getApplicationContext(), MainActivity.class);
            mainAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainAct);
            overridePendingTransition(0, 0);
        }
    }
}
