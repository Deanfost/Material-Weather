package dean.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by DeanF on 10/20/2016.
 */

public class introActivity extends AppCompatActivity {
    Button btnLaunchMain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);
        //Show the user how it's done!

        //Setup references
        btnLaunchMain = (Button) findViewById(R.id.launchMain);

        //Let the app know in the future it is no longer first launch
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.first_launch_key), 1);
        editor.commit();

        //Setup click events
        btnLaunchMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch main
                Intent mainAct = new Intent(getApplicationContext(), MainActivity.class);
                mainAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainAct);
            }
        });
    }
}
