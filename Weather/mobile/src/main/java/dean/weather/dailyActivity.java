package dean.weather;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by DeanF on 10/13/2016.
 */

public class DailyActivity extends AppCompatActivity {

    //View data for top layout
    private Integer passedDayInt;
    private String passedDay;
    private String pulledDescription;
    private String pulledLocation;
    private Integer pulledHi;
    private Integer pulledLo;
    private String pulledIcon;
    private String pulledCondition;

    //View data for bottom layout
    private String pulledWind;
    private Integer pulledPrecip;
    private Integer currentPrecip;
    private Integer currentHumidity;
    private Integer currentDewpoint;
    private Integer currentPressure;
    private String currentVisibilty;
    private Integer currentCloudCover;
    private String sunriseTime;
    private String sunsetTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        //Pull this day's data
        //Get the selected day
        passedDay = getIntent().getExtras().getString("day");
        Log.i("selectedDay", passedDay);
        switch (passedDay){
            case "Mon":
                passedDay = "Monday";
                break;
            case "Tue":
                passedDay = "Tuesday";
                break;
            case "Wed":
                passedDay = "Wednesday";
                break;
            case "Thu":
                passedDay = "Thursday";
                break;
            case "Fri":
                passedDay = "Friday";
                break;
            case "Sat":
                passedDay = "Saturday";
                break;
            case "Sun":
                passedDay = "Sunday";
                break;
        }

        //Get the day's position in data sets
        passedDayInt = getIntent().getExtras().getInt("dayInt");
        Log.i("selectedDayPosition", passedDayInt.toString());

        //Get the day's description
        pulledDescription = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getSummary();
        Log.i("dayDesc", pulledDescription);

        //Get the day's location
        pulledLocation = MainActivity.currentLocation;

        //Get the day's Hi
        Double pulledHiDouble = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getTemperatureMax();
        pulledHi = pulledHiDouble.intValue();
        Log.i("dayHi", pulledHi.toString());

        //Get the day's Lo
        Double pulledLoDouble = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getTemperatureMin();
        pulledLo = pulledLoDouble.intValue();
        Log.i("dayLo", pulledLo.toString());

        //Get the day's icon
        pulledIcon = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getIcon();
        Log.i("dayIcon", pulledIcon);

        //Get the day's condition
        switch (pulledIcon){
            case "clear-day":
                pulledCondition = "Clear";
                break;
            case "clear-night":
                pulledCondition = "Clear";
                break;
            case "rain":
                pulledCondition = "Rain";
                break;
            case "snow":
                pulledCondition = "Snow";
                break;
            case "sleet":
                pulledCondition = "Sleet";
                break;
            case "wind":
                pulledCondition = "Windy";
                break;
            case "fog":
                pulledCondition = "Foggy";
                break;
            case "cloudy":
                pulledCondition = "Cloudy";
                break;
            case "partly-cloudy-day":
                pulledCondition = "Partly Cloudy";
                break;
            case "partly-cloudy-night":
                pulledCondition = "Partly Cloudy";
                break;
            default:
                pulledCondition = "Clear";
                Log.i("CurrentConditions", "Unsupported condition.");
                break;
        }
        Log.i("dayCond", pulledCondition);

        //Get the day's wind
        

        //Set toolbar
        Toolbar dailyToolbar = (Toolbar) findViewById(R.id.dailyToolbar);
        setSupportActionBar(dailyToolbar);

        //Customize the app bar
        assert dailyToolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(passedDay);
        dailyToolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlue));
        //Enable up functions
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set color of system bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBlue));
    }

    //Action bar events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //Up button
            case android.R.id.home:
                finish();
                return true;
            //User action not recognized
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
