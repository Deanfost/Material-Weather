package dean.weather;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.johnhiott.darkskyandroidlib.models.AlertsBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean Foster on 1/19/2017.
 */

public class AlertsActivity extends AppCompatActivity {
    private Integer alertCount = 0;
    private List<AlertsBlock> alertsList;
    private RecyclerView alertsRecycler;
    private RecyclerView.Adapter alertsAdapter;
    private RecyclerView.LayoutManager alertsLayoutManager;
    private Integer setID;
    private String currentIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get the color of the layout
        setID = getIntent().getExtras().getInt("setID");

        //Check to see if there are any alerts
        if(MainActivity.pulledWeatherResponse.getAlerts() != null){
            //Pull the alerts
            alertsList = new ArrayList<>();
            for(int i = 0; i < MainActivity.pulledWeatherResponse.getAlerts().size(); i++){
                alertsList.add(MainActivity.pulledWeatherResponse.getAlerts().get(i));
                Log.i("Alert activity", MainActivity.pulledWeatherResponse.getAlerts().get(i).getTitle());
                Log.i("Alert activity", MainActivity.pulledWeatherResponse.getAlerts().get(i).getDescription());
                alertCount ++;
            }
            Log.i("Alert count", alertCount.toString());

            //There are alerts
            setContentView(R.layout.activity_alerts);

            //Setup app bar
            Toolbar alertsToolbar = (Toolbar) findViewById(R.id.alertsToolbar);
            setSupportActionBar(alertsToolbar);
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Weather Alerts");

            //Set the color of the layout
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            switch (setID){
                case 0:
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorYellow)));
                    window.setStatusBarColor(getResources().getColor(R.color.colorYellow));
                    break;
                case 1:
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBlue)));
                    window.setStatusBarColor(getResources().getColor(R.color.colorBlue));
                    break;
                case 2:
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOrange)));
                    window.setStatusBarColor(getResources().getColor(R.color.colorOrange));
                    break;
                case 3:
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPurple)));
                    window.setStatusBarColor(getResources().getColor(R.color.colorPurple));
                    break;
            }

            //Setup recyclerView
            alertsRecycler = (RecyclerView) findViewById(R.id.alertsRecyclerView);
            alertsRecycler.setHasFixedSize(true);

            //Setup recyclerView adapter
            alertsAdapter = new AlertsAdapter(this, alertsList, setID);
            alertsRecycler.setAdapter(alertsAdapter);

            //Setup recyclerView layout manager
            alertsLayoutManager = new LinearLayoutManager(this);
            alertsRecycler.setLayoutManager(alertsLayoutManager);
        }
        else{
            //There are no alerts
            setContentView(R.layout.activity_no_alerts);

            //Get the icon of the current conditions
            currentIcon = getIntent().getExtras().getString("conditionsIcon");

            //Setup references
            Toolbar noAlertsToolbar = (Toolbar) findViewById(R.id.noAlertsToolbar);
            ImageView noAlertsIcon = (ImageView) findViewById(R.id.noAlertsIcon);

            //Setup app bar
            setSupportActionBar(noAlertsToolbar);
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Weather Alerts");

            //Set the color of the layout
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            switch (setID){
                case 0:
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorYellow)));
                    window.setStatusBarColor(getResources().getColor(R.color.colorYellow));
                    noAlertsIcon.setColorFilter(R.color.colorYellowDark);
                    break;
                case 1:
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBlue)));
                    window.setStatusBarColor(getResources().getColor(R.color.colorBlue));
                    noAlertsIcon.setColorFilter(R.color.colorBlueDark);
                    break;
                case 2:
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOrange)));
                    window.setStatusBarColor(getResources().getColor(R.color.colorOrange));
                    noAlertsIcon.setColorFilter(R.color.colorOrangeDark);
                    break;
                case 3:
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPurple)));
                    window.setStatusBarColor(getResources().getColor(R.color.colorPurple));
                    noAlertsIcon.setColorFilter(R.color.colorPurpleDark);
                    break;
            }

            //Set the icon of the layout to the current weather conditions
            switch (currentIcon) {
                case "clear-day":
                    noAlertsIcon.setImageResource(R.drawable.ic_sunny_white);
                    break;
                case "clear-night":
                    noAlertsIcon.setImageResource(R.drawable.ic_clear_night_white);
                    break;
                case "rain":
                    noAlertsIcon.setImageResource(R.drawable.ic_rain_white);
                    break;
                case "snow":
                    noAlertsIcon.setImageResource(R.drawable.ic_snow_white);
                    break;
                case "sleet":
                    noAlertsIcon.setImageResource(R.drawable.ic_sleet_white);
                    break;
                case "wind":
                    noAlertsIcon.setImageResource(R.drawable.ic_windrose_white);
                    break;
                case "fog":
                    if (MainActivity.setID != 3) {
                        noAlertsIcon.setImageResource(R.drawable.ic_foggyday_white);
                    } else {
                        noAlertsIcon.setImageResource(R.drawable.ic_foggynight_white);
                    }
                    break;
                case "cloudy":
                    noAlertsIcon.setImageResource(R.drawable.ic_cloudy_white);
                    break;
                case "partly-cloudy-day":
                    noAlertsIcon.setImageResource(R.drawable.ic_partlycloudy_white);
                    break;
                case "partly-cloudy-night":
                    noAlertsIcon.setImageResource(R.drawable.ic_partlycloudynight_white);
                    break;
                default:
                    noAlertsIcon.setImageResource(R.drawable.ic_cloudy_white);
                    Log.i("CurrentConditions", "Unsupported condition.");
                    break;
            }
        }
    }
}