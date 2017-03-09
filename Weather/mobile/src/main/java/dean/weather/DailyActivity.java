package dean.weather;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.google.firebase.FirebaseApp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DeanF on 10/13/2016.
 */

public class DailyActivity extends AppCompatActivity {
    //View data for top layout
    private Integer passedDayInt = -1;
    private String passedDay = "---";
    private String passedDate = "---";
    private String pulledDescription = "---";
    private String pulledLocation = "---";
    private Integer pulledHi = -1;
    private Integer pulledLo = -1;
    private String pulledIcon = "---";
    private String pulledCondition = "---";

    //View data for bottom layout
    private String pulledWind = "---";
    private Integer pulledPrecip = -1;
    private Integer pulledHumidity = -1;
    private Integer pulledDewpoint = -1;
    private Integer pulledPressure = -1;
    private Integer pulledCloudCover = -1;
    private String daySunriseTime = "---";
    private String daySunsetTime = "---";

    //Views
    Toolbar dailyToolbar;
    LinearLayout wrapperLayout;
    RelativeLayout topLayout;
    TextView viewLocation;
    TextView viewDay;
    TextView viewDescription;
    ImageView viewConditionsIcon;
    TextView viewConditions;
    TextView viewHiLo;
    TextView viewWind;
    TextView viewPrecip;
    TextView viewHumidity;
    TextView viewDewpoint;
    TextView viewPressure;
    TextView viewCloudCover;
    TextView viewWindValue;
    TextView viewPrecipValue;
    TextView viewHumidityValue;
    TextView viewDewPointValue;
    TextView viewPressureValue;
    TextView viewCloudCoverValue;
    ImageView viewSunriseIcon;
    ImageView viewSunsetIcon;
    TextView viewSunriseTime;
    TextView viewSunsetTime;
    TextView viewPoweredLabel;

    Typeface robotoLight;

    //Units
    int units;
    int hourFormat;

    //Color themes
    int setID = MainActivity.setID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            //Restoring instance implementation
            setContentView(R.layout.activity_daily);

            //Pull this day's data from saveInstanceState
            //Get the theme
            setID = savedInstanceState.getInt("setID");
            //Get the selected day
            passedDay = savedInstanceState.getString("passedDay");
            passedDate = savedInstanceState.getString("passedDate");
            Log.i("selectedDay", passedDay);
            //Extend the passed day
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
            passedDayInt = savedInstanceState.getInt("passedDayInt");
            Log.i("selectedDayPosition", passedDayInt.toString());

            //Get the day's description
            pulledDescription = savedInstanceState.getString("pulledDescription");
            Log.i("dayDesc", pulledDescription);

            //Get the day's location
            pulledLocation = savedInstanceState.getString("pulledLocation");
            Log.i("dayLocation", pulledLocation);

            //Get the day's Hi
            pulledHi = savedInstanceState.getInt("pulledHi");
            Log.i("dayHi", pulledHi.toString());

            //Get the day's Lo
            pulledLo = savedInstanceState.getInt("pulledLo");
            Log.i("dayLo", pulledLo.toString());

            //Get the day's icon
            pulledIcon = savedInstanceState.getString("pulledIcon");
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

            //Get the day's wind speed and bearing
            pulledWind = savedInstanceState.getString("pulledWind");

            //Get day's Precip
            pulledPrecip = savedInstanceState.getInt("pulledPrecip");
            Log.i("dayPrecip", pulledPrecip.toString());

            //Get day's Humidity
            pulledHumidity = savedInstanceState.getInt("pulledHumidity");
            Log.i("dayHumidity", pulledHumidity.toString());

            //Get day's Dew Point
            pulledDewpoint = savedInstanceState.getInt("pulledDewpoint");
            Log.i("dayDewPoint", pulledDewpoint.toString());

            //Get day's Pressure
            pulledPressure = savedInstanceState.getInt("pulledPressure");
            Log.i("dayPressure", pulledPressure.toString());

            //Get day's cloud cover
            pulledCloudCover = savedInstanceState.getInt("pulledCloudCover");
            Log.i("dayCloudCover", pulledCloudCover.toString());

            //Get sunrise and sunset times
            daySunriseTime = savedInstanceState.getString("daySunriseTime");
            daySunsetTime = savedInstanceState.getString("daySunsetTime");

            //References
            //Set toolbar
            dailyToolbar = (Toolbar) findViewById(R.id.dailyToolbar);
            setSupportActionBar(dailyToolbar);

            topLayout = (RelativeLayout) findViewById(R.id.dayTopContentLayout);

            viewDay = (TextView) findViewById(R.id.dayDate);
            viewLocation = (TextView) findViewById(R.id.dayLocation);
            viewDescription = (TextView) findViewById(R.id.dayDesc);
            viewConditionsIcon = (ImageView) findViewById(R.id.iconDayConditions);
            viewConditions = (TextView) findViewById(R.id.dayCondition);
            viewHiLo = (TextView) findViewById(R.id.dayHiLo);
            viewWind = (TextView) findViewById(R.id.dayDetailsWindLabel);
            viewPrecip = (TextView) findViewById(R.id.dayDetailsPrecipLabel);
            viewHumidity = (TextView) findViewById(R.id.dayDetailsHumidityLabel);
            viewDewpoint = (TextView) findViewById(R.id.dayDetailsDewpointLabel);
            viewPressure = (TextView) findViewById(R.id.dayDetailsPressureLabel);
            viewCloudCover = (TextView) findViewById(R.id.dayDetailsCloudCoverLabel);
            viewWindValue = (TextView) findViewById(R.id.dayDetailsWindValue);
            viewPrecipValue = (TextView) findViewById(R.id.dayDetailsPrecipValue);
            viewHumidityValue = (TextView) findViewById(R.id.dayDetailsHumidityValue);
            viewDewPointValue = (TextView) findViewById(R.id.dayDetailsDewPointValue);
            viewPressureValue = (TextView) findViewById(R.id.dayDetailsPressureValue);
            viewCloudCoverValue = (TextView) findViewById(R.id.dayDetailsCloudCoverValue);
            viewSunriseIcon = (ImageView) findViewById(R.id.daySunriseIcon);
            viewSunsetIcon = (ImageView) findViewById(R.id.daySunsetIcon);
            viewSunriseTime = (TextView) findViewById(R.id.daySunriseTime);
            viewSunsetTime = (TextView) findViewById(R.id.daySunsetTime);
            viewPoweredLabel = (TextView) findViewById(R.id.dayPoweredByForecast);

            robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

            //Customize the app bar
            assert dailyToolbar != null;
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle(passedDate);
            dailyToolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlue));

            //Enable up functions
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            //Customize view fonts
            viewDescription.setTypeface(robotoLight);
            viewConditions.setTypeface(robotoLight);
            viewHiLo.setTypeface(robotoLight);
            viewWind.setTypeface(robotoLight);
            viewPrecip.setTypeface(robotoLight);
            viewHumidity.setTypeface(robotoLight);
            viewDewpoint.setTypeface(robotoLight);
            viewPressure.setTypeface(robotoLight);
            viewCloudCover.setTypeface(robotoLight);
            viewWindValue.setTypeface(robotoLight);
            viewPrecipValue.setTypeface(robotoLight);
            viewHumidityValue.setTypeface(robotoLight);
            viewDewPointValue.setTypeface(robotoLight);
            viewPressureValue.setTypeface(robotoLight);
            viewCloudCoverValue.setTypeface(robotoLight);
            viewSunriseTime.setTypeface(robotoLight);
            viewSunsetTime.setTypeface(robotoLight);
            viewPoweredLabel.setTypeface(robotoLight);

            //Set layout color
            setWindowColor();
            setLayoutColor();

            //Set view values
            setViews();
        }

        else{
            //Default implementation
            setContentView(R.layout.activity_daily);

            //Determine units
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if(preferences.getString(getString(R.string.units_list_key), "0").equals("0")){
                //Use Imperial units
                units = 0;
            }
            else{
                //Use Metric units
                units = 1;
            }

            //Determine hour format
            if(preferences.getBoolean(getString(R.string.hour_format_key), false)){
                //Use 24 hour format
                hourFormat = 1;
            }
            else{
                //Use 12 hour format
                hourFormat = 0;
            }

            //Pull this day's data
            //Get the selected day
            passedDay = getIntent().getExtras().getString("day");
            passedDate = getIntent().getExtras().getString("selectedDate");
            Log.i("selectedDay", passedDay);
            //Extend the passed day
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
            if(MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getSummary() != null){
                pulledDescription = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getSummary();
                Log.i("dayDesc", pulledDescription);
            }
            else{
                pulledDescription = "---";
                Log.i("dayDesc", "---");
            }

            //Get the day's location
            if(MainActivity.currentLocation != null){
                pulledLocation = MainActivity.currentLocation;
                Log.i("dayLocation", pulledLocation);
            }
            else{
                pulledLocation = "---";
                Log.i("dayLocation", "---");
            }

            //Get the day's Hi
            Double pulledHiDouble = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getTemperatureMax();
            if(pulledHiDouble != null){
                pulledHi = pulledHiDouble.intValue();
                Log.i("dayHi", pulledHi.toString());
            }
            else{
                pulledHi = -1;
                Log.i("dayHi", "---");
            }

            //Get the day's Lo
            Double pulledLoDouble = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getTemperatureMin();
            if(pulledLo != null){
                pulledLo = pulledLoDouble.intValue();
                Log.i("dayLo", pulledLo.toString());
            }
            else{
                pulledLo = -1;
                Log.i("dayLo", "---");
            }

            //Get the day's icon
            if(MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getIcon() != null){
                pulledIcon = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getIcon();
                Log.i("dayIcon", pulledIcon);
            }
            else{
                pulledIcon = "---";
                Log.i("dayIcon", "---");
            }

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

            //Parse current wind speed and bearing
            String dayWindSpeed = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getWindSpeed();
            if(dayWindSpeed != null){
                Double dayWindSpeedDouble = Double.valueOf(dayWindSpeed);
                //Convert MPH to KPH if in metric
                if(units == 1){
                    dayWindSpeedDouble = dayWindSpeedDouble * 1.6093440;
                }
                int dayWindSpeedInt = dayWindSpeedDouble.intValue();
                String dayWindBearing = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getWindBearing();
                int dayWindBearingValue = Integer.valueOf(dayWindBearing);
                Log.i("dayWindSpeed", dayWindSpeed);
                Log.i("dayWindBearing", dayWindBearing);

                if(units == 0){
                    if(dayWindBearingValue >= 0 && dayWindBearingValue < 45){
                        pulledWind = "↓" + dayWindSpeedInt + "MPH";
                    }
                    else if(dayWindBearingValue >= 45 && dayWindBearingValue < 90){
                        pulledWind = "↙" + dayWindSpeedInt + "MPH";
                    }
                    else if(dayWindBearingValue >= 90 && dayWindBearingValue < 135){
                        pulledWind = "←" + dayWindSpeedInt + "MPH";
                    }
                    else if(dayWindBearingValue >= 135 && dayWindBearingValue < 180){
                        pulledWind = "↖" + dayWindSpeedInt + "MPH";
                    }
                    else if(dayWindBearingValue >= 180 && dayWindBearingValue < 225){
                        pulledWind = "↑" + dayWindSpeedInt + "MPH";
                    }
                    else if(dayWindBearingValue >= 225 && dayWindBearingValue < 270){
                        pulledWind = "↗" + dayWindSpeedInt + "MPH";
                    }
                    else if(dayWindBearingValue >= 270 && dayWindBearingValue < 315){
                        pulledWind = "→" + dayWindSpeedInt + "MPH";
                    }
                    else if(dayWindBearingValue >= 315 && dayWindBearingValue < 360){
                        pulledWind = "↘" + dayWindSpeedInt + "MPH";
                    }
                }
                else{
                    if(dayWindBearingValue >= 0 && dayWindBearingValue < 45){
                        pulledWind = "↓" + dayWindSpeedInt + "KPH";
                    }
                    else if(dayWindBearingValue >= 45 && dayWindBearingValue < 90){
                        pulledWind = "↙" + dayWindSpeedInt + "KPH";
                    }
                    else if(dayWindBearingValue >= 90 && dayWindBearingValue < 135){
                        pulledWind = "←" + dayWindSpeedInt + "KPH";
                    }
                    else if(dayWindBearingValue >= 135 && dayWindBearingValue < 180){
                        pulledWind = "↖" + dayWindSpeedInt + "KPH";
                    }
                    else if(dayWindBearingValue >= 180 && dayWindBearingValue < 225){
                        pulledWind = "↑" + dayWindSpeedInt + "KPH";
                    }
                    else if(dayWindBearingValue >= 225 && dayWindBearingValue < 270){
                        pulledWind = "↗" + dayWindSpeedInt + "KPH";
                    }
                    else if(dayWindBearingValue >= 270 && dayWindBearingValue < 315){
                        pulledWind = "→" + dayWindSpeedInt + "KPH";
                    }
                    else if(dayWindBearingValue >= 315 && dayWindBearingValue < 360){
                        pulledWind = "↘" + dayWindSpeedInt + "KPH";
                    }
                }
                Log.i("pulledWind", pulledWind);
            }
            else{
                pulledWind = "---";
                Log.i("pulledWind", "---");
            }

            //Get day's Precip
            String dayPrecipProb = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getPrecipProbability();
            if(dayPrecipProb != null){
                Double dayPrecipProbDouble = Double.valueOf(dayPrecipProb) * 100;
                pulledPrecip = dayPrecipProbDouble.intValue();
                Log.i("dayPrecip", dayPrecipProb);
            }
            else{
                pulledPrecip = -1;
                Log.i("dayPrecip", "---");
            }

            //Get day's Humidity
            String dayHumidity = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getHumidity();
            if(dayHumidity != null){
                Double dayHumidityDouble = Double.valueOf(dayHumidity) * 100;
                pulledHumidity = dayHumidityDouble.intValue();
                Log.i("dayHumidity", dayHumidity);
            }
            else{
                pulledHumidity = -1;
                Log.i("dayHumidity", "---");
            }

            //Get day's Dew Point
            String dayDewPoint = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getDewPoint();
            if(dayDewPoint != null){
                Double dayDewPointDouble = Double.valueOf(dayDewPoint);
                pulledDewpoint = dayDewPointDouble.intValue();
                Log.i("dayDewPoint", dayDewPoint);
            }
            else{
                pulledDewpoint = -1;
                Log.i("dayDewPoint", "---");
            }

            //Get day's Pressure
            String dayPressure = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getPressure();
            if(dayPressure != null){
                Double dayPressureDouble = Double.valueOf(dayPressure);
                Double currentPressureDoubleConverted;
                if(units == 0){
                    currentPressureDoubleConverted = dayPressureDouble * 0.0295301;//Convert Millibars to inHg
                }
                else{
                    currentPressureDoubleConverted = dayPressureDouble;//Use Millibars
                }
                pulledPressure = currentPressureDoubleConverted.intValue();
                Log.i("dayPressure", dayPressure);
            }
            else {
                pulledPressure = -1;
                Log.i("dayPressure", "---");
            }

            //Get day's cloud cover
            String dayCloudCover = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getCloudClover();
            if(dayCloudCover != null){
                Double dayCloudCoverDouble = Double.valueOf(dayCloudCover) * 100;
                pulledCloudCover = dayCloudCoverDouble.intValue();
                Log.i("dayCloudCover", dayCloudCover);
            }
            else{
                pulledCloudCover = -1;
                Log.i("dayCloudCover", "---");
            }

            //Get sunrise time
            String sunriseTimeString = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getSunriseTime();//UNIX timestamp
            if(sunriseTimeString != null){
                if(hourFormat == 0){
                    //12 hour time
                    //Get day's sunrise time
                    Long sunriseTimeInMili = Long.valueOf(sunriseTimeString) * 1000;
                    Date sunriseDateObject = new Date(sunriseTimeInMili);
                    SimpleDateFormat sunriseDateFormat = new SimpleDateFormat("h:mm aa");
                    daySunriseTime = sunriseDateFormat.format(sunriseDateObject.getTime());
                    Log.i("daySunriseTime", daySunriseTime);
                }
                else{
                    //24 hour time
                    //Get day's sunrise time
                    Long sunriseTimeInMili = Long.valueOf(sunriseTimeString) * 1000;
                    Date sunriseDateObject = new Date(sunriseTimeInMili);
                    SimpleDateFormat sunriseDateFormat = new SimpleDateFormat("HH:mm");
                    daySunriseTime = sunriseDateFormat.format(sunriseDateObject.getTime());
                    Log.i("daySunriseTime", daySunriseTime);
                }
            }else{
                daySunriseTime = "---";
                Log.i("daySunriseTime", "---");
            }

            //Sunset time
            String sunsetTimeString = MainActivity.pulledWeatherResponse.getDaily().getData().get(passedDayInt).getSunsetTime();//UNIX timestamp
            if(sunsetTimeString != null){
                if(hourFormat == 0){
                    //12 hour time
                    //Get day's sunset time
                    Long sunsetTimeInMili = Long.valueOf(sunsetTimeString) * 1000;
                    Date sunsetDateObject = new Date(sunsetTimeInMili);
                    SimpleDateFormat sunsetDateFormat = new SimpleDateFormat("h:mm aa");
                    daySunsetTime = sunsetDateFormat.format(sunsetDateObject.getTime());
                    Log.i("daySunsetTime", daySunsetTime);
                }
                else{
                    //24 hour time
                    //Get day's sunset time
                    Long sunsetTimeInMili = Long.valueOf(sunsetTimeString) * 1000;
                    Date sunsetDateObject = new Date(sunsetTimeInMili);
                    SimpleDateFormat sunsetDateFormat = new SimpleDateFormat("HH:mm");
                    daySunsetTime = sunsetDateFormat.format(sunsetDateObject.getTime());
                    Log.i("daySunsetTime", daySunsetTime);
                }
            }
            else{
                daySunriseTime = "---";
                Log.i("daySunsetTime", "---");
            }

            //References
            //Set toolbar
            dailyToolbar = (Toolbar) findViewById(R.id.dailyToolbar);
            setSupportActionBar(dailyToolbar);

            topLayout = (RelativeLayout) findViewById(R.id.dayTopContentLayout);

            viewDay = (TextView) findViewById(R.id.dayDate);
            viewLocation = (TextView) findViewById(R.id.dayLocation);
            viewDescription = (TextView) findViewById(R.id.dayDesc);
            viewConditionsIcon = (ImageView) findViewById(R.id.iconDayConditions);
            viewConditions = (TextView) findViewById(R.id.dayCondition);
            viewHiLo = (TextView) findViewById(R.id.dayHiLo);
            viewWind = (TextView) findViewById(R.id.dayDetailsWindLabel);
            viewPrecip = (TextView) findViewById(R.id.dayDetailsPrecipLabel);
            viewHumidity = (TextView) findViewById(R.id.dayDetailsHumidityLabel);
            viewDewpoint = (TextView) findViewById(R.id.dayDetailsDewpointLabel);
            viewPressure = (TextView) findViewById(R.id.dayDetailsPressureLabel);
            viewCloudCover = (TextView) findViewById(R.id.dayDetailsCloudCoverLabel);
            viewWindValue = (TextView) findViewById(R.id.dayDetailsWindValue);
            viewPrecipValue = (TextView) findViewById(R.id.dayDetailsPrecipValue);
            viewHumidityValue = (TextView) findViewById(R.id.dayDetailsHumidityValue);
            viewDewPointValue = (TextView) findViewById(R.id.dayDetailsDewPointValue);
            viewPressureValue = (TextView) findViewById(R.id.dayDetailsPressureValue);
            viewCloudCoverValue = (TextView) findViewById(R.id.dayDetailsCloudCoverValue);
            viewSunriseIcon = (ImageView) findViewById(R.id.daySunriseIcon);
            viewSunsetIcon = (ImageView) findViewById(R.id.daySunsetIcon);
            viewSunriseTime = (TextView) findViewById(R.id.daySunriseTime);
            viewSunsetTime = (TextView) findViewById(R.id.daySunsetTime);
            viewPoweredLabel = (TextView) findViewById(R.id.dayPoweredByForecast);

            robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

            //Customize the app bar
            assert dailyToolbar != null;
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle(passedDate);
            dailyToolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlue));

            //Enable up functions
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            //Customize view fonts
            viewDescription.setTypeface(robotoLight);
            viewConditions.setTypeface(robotoLight);
            viewHiLo.setTypeface(robotoLight);
            viewWind.setTypeface(robotoLight);
            viewPrecip.setTypeface(robotoLight);
            viewHumidity.setTypeface(robotoLight);
            viewDewpoint.setTypeface(robotoLight);
            viewPressure.setTypeface(robotoLight);
            viewCloudCover.setTypeface(robotoLight);
            viewWindValue.setTypeface(robotoLight);
            viewPrecipValue.setTypeface(robotoLight);
            viewHumidityValue.setTypeface(robotoLight);
            viewDewPointValue.setTypeface(robotoLight);
            viewPressureValue.setTypeface(robotoLight);
            viewCloudCoverValue.setTypeface(robotoLight);
            viewSunriseTime.setTypeface(robotoLight);
            viewSunsetTime.setTypeface(robotoLight);
            viewPoweredLabel.setTypeface(robotoLight);

            //Set layout color
            setWindowColor();
            setLayoutColor();

            //Set view values
            setViews();
        }
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

    /**
     * Customizes window colors.
     */
    private void setWindowColor(){
        //Setup resources to changeTheme
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        switch (setID){
            //Sunrise
            case 0:
                //Set color of header in task tray
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorYellowDark)));
                icon = null;

                //Set color of system bar
                window.setStatusBarColor(this.getResources().getColor(R.color.colorYellowDark));

                //Customize app bar
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOrangeDark)));

                window = null;
                break;

            //Daytime
            case 1:
                //Set color of header in task tray
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorBlueDark)));
                icon = null;

                //Set color of system bar
                window.setStatusBarColor(this.getResources().getColor(R.color.colorBlueDark));

                //Customize app bar
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBlueDark)));

                window = null;
                break;
            //Sunset
            case 2:
                //Set color of header in task tray
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorOrangeDark)));
                icon = null;

                //Set color of system bar
                window.setStatusBarColor(this.getResources().getColor(R.color.colorOrangeDark));

                //Customize app bar
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOrangeDark)));

                window = null;
                break;
            //Nighttime
            case 3:
                //Set color of header in task tray
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorPurpleDark)));
                icon = null;

                //Set color of system bar
                window.setStatusBarColor(this.getResources().getColor(R.color.colorPurpleDark));

                //Customize app bar
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPurpleDark)));

                window = null;
                break;
            default:
                Log.i("SetLayoutColor", "defaultCondition");
        }
    }

    /**
     * Sets of layout depending on time of day.
     */
    private void setLayoutColor() {
        switch (setID) {
            //Sunrise
            case 0:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorYellow));

                //Views
                viewWindValue.setTextColor(getResources().getColor(R.color.colorYellow));
                viewPrecipValue.setTextColor(getResources().getColor(R.color.colorYellow));
                viewHumidityValue.setTextColor(getResources().getColor(R.color.colorYellow));
                viewDewPointValue.setTextColor(getResources().getColor(R.color.colorYellow));
                viewPressureValue.setTextColor(getResources().getColor(R.color.colorYellow));
                viewCloudCoverValue.setTextColor(getResources().getColor(R.color.colorYellow));
                viewSunriseIcon.setColorFilter(getResources().getColor(R.color.colorYellow));
                viewSunsetIcon.setColorFilter(getResources().getColor(R.color.colorYellow));
                break;
            //Daytime
            case 1:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorBlue));

                //Views
                viewWindValue.setTextColor(getResources().getColor(R.color.colorBlue));
                viewPrecipValue.setTextColor(getResources().getColor(R.color.colorBlue));
                viewHumidityValue.setTextColor(getResources().getColor(R.color.colorBlue));
                viewDewPointValue.setTextColor(getResources().getColor(R.color.colorBlue));
                viewPressureValue.setTextColor(getResources().getColor(R.color.colorBlue));
                viewCloudCoverValue.setTextColor(getResources().getColor(R.color.colorBlue));
                viewSunriseIcon.setColorFilter(getResources().getColor(R.color.colorBlue));
                viewSunsetIcon.setColorFilter(getResources().getColor(R.color.colorBlue));
                break;
            //Sunset
            case 2:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorOrange));

                //Views
                viewWindValue.setTextColor(getResources().getColor(R.color.colorOrange));
                viewPrecipValue.setTextColor(getResources().getColor(R.color.colorOrange));
                viewHumidityValue.setTextColor(getResources().getColor(R.color.colorOrange));
                viewDewPointValue.setTextColor(getResources().getColor(R.color.colorOrange));
                viewPressureValue.setTextColor(getResources().getColor(R.color.colorOrange));
                viewCloudCoverValue.setTextColor(getResources().getColor(R.color.colorOrange));
                viewSunriseIcon.setColorFilter(getResources().getColor(R.color.colorOrange));
                viewSunsetIcon.setColorFilter(getResources().getColor(R.color.colorOrange));
                break;
            //Nighttime
            case 3:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorPurple));

                //Views
                viewWindValue.setTextColor(getResources().getColor(R.color.colorPurple));
                viewPrecipValue.setTextColor(getResources().getColor(R.color.colorPurple));
                viewHumidityValue.setTextColor(getResources().getColor(R.color.colorPurple));
                viewDewPointValue.setTextColor(getResources().getColor(R.color.colorPurple));
                viewPressureValue.setTextColor(getResources().getColor(R.color.colorPurple));
                viewCloudCoverValue.setTextColor(getResources().getColor(R.color.colorPurple));
                viewSunriseIcon.setColorFilter(getResources().getColor(R.color.colorPurple));
                viewSunsetIcon.setColorFilter(getResources().getColor(R.color.colorPurple));
                break;
        }
    }

    /**
     * Sets view values.
     */
    private void setViews(){
        viewLocation.setText(pulledLocation);
        viewDay.setText(passedDay);
        switch (pulledIcon) {
            case "clear-day":
                viewConditionsIcon.setImageResource(R.drawable.ic_sunny_white);
                break;
            case "clear-night":
                viewConditionsIcon.setImageResource(R.drawable.ic_sunny_white);
                break;
            case "rain":
                viewConditionsIcon.setImageResource(R.drawable.ic_rain_white);
                break;
            case "snow":
                viewConditionsIcon.setImageResource(R.drawable.ic_snow_white);
                break;
            case "sleet":
                viewConditionsIcon.setImageResource(R.drawable.ic_sleet_white);
                break;
            case "wind":
                viewConditionsIcon.setImageResource(R.drawable.ic_windrose_white);
                break;
            case "fog":
                    viewConditionsIcon.setImageResource(R.drawable.ic_foggyday_white);
                break;
            case "cloudy":
                viewConditionsIcon.setImageResource(R.drawable.ic_cloudy_white);
                break;
            case "partly-cloudy-day":
                viewConditionsIcon.setImageResource(R.drawable.ic_partlycloudy_white);
                break;
            case "partly-cloudy-night":
                viewConditionsIcon.setImageResource(R.drawable.ic_partlycloudy_white);
                break;
            default:
                viewConditionsIcon.setImageResource(R.drawable.ic_cloudy_white);
                Log.i("CurrentConditions", "Unsupported condition.");
                break;
        }

        viewDescription.setText(pulledDescription);
        viewConditions.setText(pulledCondition);
        String pulledHiString;
        String pulledLoString;
        if(pulledHi == -1){
            pulledHiString = "---";
        }
        else{
            pulledHiString = pulledHi.toString();
        }

        if(pulledLo == -1){
            pulledLoString = "---";
        }
        else{
            pulledLoString = pulledLo.toString();
        }
        viewHiLo.setText(pulledHiString + "°/" + pulledLoString + "°");
        viewWindValue.setText(pulledWind);
        if(units == 0){
            if(pulledPrecip == -1){
                viewPrecipValue.setText("---");
            }
            else{
                viewPrecipValue.setText(String.valueOf(pulledPrecip) + "%");
            }

            if(pulledHumidity == -1){
                viewHumidityValue.setText("---");
            }
            else{
                viewHumidityValue.setText(String.valueOf(pulledHumidity) + "%");
            }

            if(pulledDewpoint != -1){
                viewDewPointValue.setText("---");
            }
            else{
                viewDewPointValue.setText(String.valueOf(pulledDewpoint) + "\u00B0");
            }

            if(pulledPressure != -1){
                viewPressureValue.setText("---");
            }
            else{
                viewPressureValue.setText(String.valueOf(pulledPressure) + "inHg");
            }

            if(pulledCloudCover != -1){
                viewCloudCoverValue.setText("---");
            }
            else{
                viewCloudCoverValue.setText(String.valueOf(pulledCloudCover) + "%");
            }
        }
        else{
            if(pulledPrecip == -1){
                viewPrecipValue.setText("---");
            }
            else{
                viewPrecipValue.setText(String.valueOf(pulledPrecip) + "%");
            }

            if(pulledHumidity == -1){
                viewHumidityValue.setText("---");
            }
            else{
                viewHumidityValue.setText(String.valueOf(pulledHumidity) + "%");
            }

            if(pulledDewpoint != -1){
                viewDewPointValue.setText("---");
            }
            else{
                viewDewPointValue.setText(String.valueOf(pulledDewpoint) + "\u00B0");
            }

            if(pulledPressure != -1){
                viewPressureValue.setText("---");
            }
            else{
                viewPressureValue.setText(String.valueOf(pulledPressure) + "mb");
            }

            if(pulledCloudCover != -1){
                viewCloudCoverValue.setText("---");
            }
            else{
                viewCloudCoverValue.setText(String.valueOf(pulledCloudCover) + "%");
            }
        }
        viewSunriseTime.setText(daySunriseTime);
        viewSunsetTime.setText(daySunsetTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Daily Activity", "Finishing");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Color themes
        outState.putInt("setID", setID);
        //Top layout
        outState.putInt("passedDayInt", passedDayInt);
        outState.putString("passedDay", passedDay);
        outState.putString("passedDate", passedDate);
        outState.putString("pulledDescription", pulledDescription);
        outState.putString("pulledLocation", pulledLocation);
        outState.putInt("pulledHi", pulledHi);
        outState.putInt("pulledLo", pulledLo);
        outState.putString("pulledIcon", pulledIcon);
        outState.putString("pulledCondition", pulledCondition);
        //Bottom layout
        outState.putString("pulledWind", pulledWind);
        outState.putInt("pulledPrecip", pulledPrecip);
        outState.putInt("pulledHumidity", pulledHumidity);
        outState.putInt("pulledDewpoint", pulledDewpoint);
        outState.putInt("pulledPressure", pulledPressure);
        outState.putInt("pulledCloudCover", pulledCloudCover);
        outState.putString("daySunriseTime", daySunriseTime);
        outState.putString("daySunsetTime", daySunsetTime);
        //Units
        outState.putInt("units", units);
        outState.putInt("hourFormat", hourFormat);
        super.onSaveInstanceState(outState);
    }
}