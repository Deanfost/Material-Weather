package dean.weather;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Integer precipHumidity;
    private Integer pulledDewpoint;
    private Integer pulledPressure;
    private String pulledVisibility;
    private Integer pulledCloudCover;
    private String daySunriseTime;
    private String daySunsetTime;

    //Views
    Toolbar dailyToolbar;
    LinearLayout wrapperLayout;
    RelativeLayout topLayout;
    TextView currentLocation;
    TextView currentDate;
    ImageView currentConditionsIcon;
    TextView currentConditions;
    TextView todaysHiLo;
    TextView currentWind;
    TextView currentPrecip;
    TextView currentHumidity;
    TextView currentDewpoint;
    TextView currentPressure;
    TextView currentVisibility;
    TextView currentCloudCover;
    TextView currentWindValue;
    TextView currentPrecipValue;
    TextView currentHumidityValue;
    TextView currentDewPointValue;
    TextView currentPressureValue;
    TextView currentVisibilityValue;
    TextView currentCloudCoverValue;
    ImageView sunriseIcon;
    ImageView sunsetIcon;
    TextView sunriseTime;
    TextView sunsetTime;

    Typeface robotoLight;

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

        //Parse current wind speed and bearing
        String currentWindSpeed = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getWindSpeed();
        Double currentWindSpeedDouble = Double.valueOf(currentWindSpeed);
        int currentWindSpeedInt = currentWindSpeedDouble.intValue();
        String currentWindBearing = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getWindBearing();
        int currentWindBearingValue = Integer.valueOf(currentWindBearing);
        Log.i("dayWindSpeed", MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getWindSpeed());
        Log.i("dayWindBearing", MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getWindBearing());
        Log.i("dayWindBearingValue", String.valueOf(currentWindBearingValue));
        //TODO - BE SURE TO CHECK FOR THE UNITS!
        if(currentWindBearingValue >= 0 && currentWindBearingValue < 45){
            pulledWind = "↓" + currentWindSpeedInt + "MPH";
        }
        else if(currentWindBearingValue >= 45 && currentWindBearingValue < 90){
            pulledWind = "↙" + currentWindSpeedInt + "MPH";
        }
        else if(currentWindBearingValue >= 90 && currentWindBearingValue < 135){
            pulledWind = "←" + currentWindSpeedInt + "MPH";
        }
        else if(currentWindBearingValue >= 135 && currentWindBearingValue < 180){
            pulledWind = "↖" + currentWindSpeedInt + "MPH";
        }
        else if(currentWindBearingValue >= 180 && currentWindBearingValue < 225){
            pulledWind = "↑" + currentWindSpeedInt + "MPH";
        }
        else if(currentWindBearingValue >= 225 && currentWindBearingValue < 270){
            pulledWind = "↗" + currentWindSpeedInt + "MPH";
        }
        else if(currentWindBearingValue >= 270 && currentWindBearingValue < 315){
            pulledWind = "→" + currentWindSpeedInt + "MPH";
        }
        else if(currentWindBearingValue >= 315 && currentWindBearingValue < 360){
            pulledWind = "↘" + currentWindSpeedInt + "MPH";
        }

        //Get day's Precip
        String currentPrecipProb = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getPrecipProbability();
        Log.i("dayPrecipString", currentPrecipProb);
        Double currentPrecipDouble = Double.valueOf(currentPrecipProb) * 100;
        pulledPrecip = currentPrecipDouble.intValue();

        //Get day's Humidity
        String currentHumidityString = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getHumidity();
        Log.i("dayHumidStr", currentHumidityString);
        Double currentHumidityDouble = Double.valueOf(currentHumidityString) * 100;
        precipHumidity = currentHumidityDouble.intValue();

        //Get day's Point
        String currentDewPointString = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getDewPoint();
        Log.i("dayDPointStr", currentDewPointString);
        Double currentDewPointDouble = Double.valueOf(currentDewPointString);
        pulledDewpoint = currentDewPointDouble.intValue();

        //Get day's Pressure
        String currentPressureString = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getPressure();
        Log.i("dayPresString", currentPressureString);
        Double currentPressureDouble = Double.valueOf(currentPressureString);
        Double currentPressureDoubleConverted = currentPressureDouble * 0.0295301;//Convert Millibars to inHg
        pulledPressure = currentPressureDoubleConverted.intValue();

        //Get day's Visibility
        String currentVisibilityString = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getVisibility();
        Log.i("dayVisString", currentVisibilityString);
        Double currentVisibilityDouble = Double.valueOf(currentVisibilityString);
        Integer currentVisibiltiyInt = Double.valueOf(currentVisibilityDouble).intValue();
        //If it is above 1, parse to just an integer
        if(currentVisibilityDouble > 1){
            pulledVisibility = String.valueOf(currentVisibiltiyInt);
        }
        //Else, pass something like 0.45
        else{
            pulledVisibility = currentVisibilityDouble.toString();
        }

        //Get day's cover
        String currentCloudCoverString = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getCloudClover();
        Log.i("dayCloudCover", currentCloudCoverString);
        Double currentCloudCoverDouble = Double.valueOf(currentCloudCoverString) * 100;
        pulledCloudCover = currentCloudCoverDouble.intValue();

        //Get day's time
        String sunriseTimeString = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getSunriseTime();//UNIX timestamp
        Log.i("dayTimeUNIX", sunriseTimeString);
        Long sunriseTimeInMili = Long.valueOf(sunriseTimeString) * 1000;
        Date sunriseDateObject = new Date(sunriseTimeInMili);
        //TODO - CHECK FOR TIME SETTINGS!
        SimpleDateFormat sunriseDateFormat = new SimpleDateFormat("h:mm aa");
        daySunriseTime = sunriseDateFormat.format(sunriseDateObject.getTime());
        Log.i("daySunriseTime", daySunriseTime);

        //Get day's time
        String sunsetTimeString = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getSunsetTime();//UNIX timestamp
        Log.i("sunsetTimeUNIX", sunsetTimeString);
        Long sunsetTimeInMili = Long.valueOf(sunsetTimeString) * 1000;
        Date sunsetDateObject = new Date(sunsetTimeInMili);
        //TODO - CHECK FOR TIME SETTINGS!
        SimpleDateFormat sunsetDateFormat = new SimpleDateFormat("h:mm aa");
        daySunsetTime = sunsetDateFormat.format(sunsetDateObject.getTime());
        Log.i("daySunsetTime", daySunsetTime);

        //References
        //Set toolbar
        dailyToolbar = (Toolbar) findViewById(R.id.dailyToolbar);
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
