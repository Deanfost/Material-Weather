package dean.weather;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

/**
 * Created by Dean Foster on 1/19/2017.
 */

public class AlertsActivity extends AppCompatActivity {
    private RecyclerView alertsRecycler;
    private RecyclerView.Adapter alertsAdapter;
    private RecyclerView.LayoutManager alertsLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check to see if there are any alerts
        if(MainActivity.pulledWeatherResponse.getAlerts() != null){
            //There are alerts
            setContentView(R.layout.activity_alerts);

            //Setup app bar
            Toolbar alertsToolbar = (Toolbar) findViewById(R.id.alertsToolbar);
            setSupportActionBar(alertsToolbar);
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Weather Alerts");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBlueDark)));

            //Setup recyclerView
            alertsRecycler = (RecyclerView) findViewById(R.id.alertsRecyclerView);
            alertsRecycler.setHasFixedSize(true);

            //Setup recyclerView adapter
            

            //Setup recyclerView layout manager
        }
        else{
            //There are no alerts
            setContentView(R.layout.activity_no_alerts);

        }
    }
}
