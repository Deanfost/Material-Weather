package dean.weather;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DeanF on 11/6/2016.
 */

public class MainFragment extends Fragment{

    //Hourly
    public static List<String> passedHoursValues;
    public static List<Integer> passedTempsValues;
    public static List<String> passedConditionsValues;
    public static List<Integer> passedWindValues;

    //Daily
    private static List<String> passedDaysValues;
    private static List<String> passedDailyCondValues;
    private static List<Integer> passedHIsValues;
    private static List<Integer> passedLOsValues;
    private static List<Integer> passedPrecipValues;

    //Setup recyclerViews
    private RecyclerView hourlyRecyclerView;
    private RecyclerView.Adapter hourlyRecyclerAdapter;
    private RecyclerView.LayoutManager hourlyLayoutManager;

    private RecyclerView dailyRecyclerView;
    private RecyclerView.Adapter dailyRecyclerAdapter;
    private RecyclerView.LayoutManager dailyLayoutManager;

    private static String passedLocationValue;
    private static String passedDateValue;
    private static String passedIconValue;
    private static int passedTempValue;
    private static String passedConditionValue;
    private static String passedHILOValue;
    private static String passedWindValue;
    private static int passedPrecipValue;
    private static int passedHumidityValue;
    private static int passedDewpointValue;
    private static int passedPressureValue;
    private static String passedVisibilityValue;
    private static int passedCloudCoverValue;
    private static String passedSunriseTimeValue;
    private static String passedSunsetTimeValue;
    private static String passedUpdateTimeValue;

    Typeface robotoLight;
    LinearLayout wrapperLayout;
    RelativeLayout topLayout;
    TextView currentLocation;
    TextView currentDate;
    ImageView currentConditionsIcon;
    TextView currentTemp;
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
//    TextView updateTime;
    TextView poweredLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Setup references
        robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        wrapperLayout = (LinearLayout) getView().findViewById(R.id.lowerFragmentWrapperContainer);
        topLayout = (RelativeLayout) getView().findViewById(R.id.topContentLayout);
        currentLocation = (TextView) getView().findViewById(R.id.currentLocation);
        currentDate = (TextView) getView().findViewById(R.id.currentDate);
        currentConditionsIcon = (ImageView) getView().findViewById(R.id.iconCurrentConditions);
        currentTemp = (TextView) getView().findViewById(R.id.currentTemp);
        currentConditions = (TextView) getView().findViewById(R.id.currentConditions);
        todaysHiLo = (TextView) getView().findViewById(R.id.todaysHiLo);
        currentWind = (TextView) getView().findViewById(R.id.currentDetailsWindLabel);
        currentPrecip = (TextView) getView().findViewById(R.id.currentDetailsPrecipLabel);
        currentHumidity = (TextView) getView().findViewById(R.id.currentDetailsHumidityLabel);
        currentDewpoint = (TextView) getView().findViewById(R.id.currentDetailsDewpointLabel);
        currentPressure = (TextView) getView().findViewById(R.id.currentDetailsPressureLabel);
        currentVisibility = (TextView) getView().findViewById(R.id.currentDetailsVisibilityLabel);
        currentCloudCover = (TextView) getView().findViewById(R.id.currentDetailsCloudCoverLabel);
        currentWindValue = (TextView) getView().findViewById(R.id.currentDetailsWindValue);
        currentPrecipValue = (TextView) getView().findViewById(R.id.currentDetailsPrecipValue);
        currentHumidityValue = (TextView) getView().findViewById(R.id.currentDetailsHumidityValue);
        currentDewPointValue = (TextView) getView().findViewById(R.id.currentDetailsDewPointValue);
        currentPressureValue = (TextView) getView().findViewById(R.id.currentDetailsPressureValue);
        currentVisibilityValue = (TextView) getView().findViewById(R.id.currentDetailsVisibilityValue);
        currentCloudCoverValue = (TextView) getView().findViewById(R.id.currentDetailsCloudCoverValue);
        sunriseIcon = (ImageView) getView().findViewById(R.id.sunriseIcon);
        sunsetIcon = (ImageView) getView().findViewById(R.id.sunsetIcon);
        sunriseTime = (TextView) getView().findViewById(R.id.sunriseTime);
        sunsetTime = (TextView) getView().findViewById(R.id.sunsetTime);
//        updateTime = (TextView) getView().findViewById(R.id.updateTime);
        poweredLabel = (TextView) getView().findViewById(R.id.poweredByForecast);

        //Typeface
        currentTemp.setTypeface(robotoLight);
        currentConditions.setTypeface(robotoLight);
        todaysHiLo.setTypeface(robotoLight);
        currentWind.setTypeface(robotoLight);
        currentPrecip.setTypeface(robotoLight);
        currentHumidity.setTypeface(robotoLight);
        currentDewpoint.setTypeface(robotoLight);
        currentPressure.setTypeface(robotoLight);
        currentVisibility.setTypeface(robotoLight);
        currentCloudCover.setTypeface(robotoLight);
        currentWindValue.setTypeface(robotoLight);
        currentPrecipValue.setTypeface(robotoLight);
        currentHumidityValue.setTypeface(robotoLight);
        currentDewPointValue.setTypeface(robotoLight);
        currentPressureValue.setTypeface(robotoLight);
        currentVisibilityValue.setTypeface(robotoLight);
        currentCloudCoverValue.setTypeface(robotoLight);
        sunriseTime.setTypeface(robotoLight);
        sunsetTime.setTypeface(robotoLight);
//        updateTime.setTypeface(robotoLight);
        poweredLabel.setTypeface(robotoLight);

        //Set color
        setFragmentLayoutColor();

        //Display data
        setViews();
    }

    /**
     * Updates views with data from API.
     */
    private void setViews() {

        //Setup hourlyRecycler view
        hourlyRecyclerView = (RecyclerView) getView().findViewById(R.id.hourlyRecyclerView);
        hourlyRecyclerView.setHasFixedSize(true);

        //Hourly Layout Manager
        hourlyLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        hourlyRecyclerView.setLayoutManager(hourlyLayoutManager);

        //Setup dailyRecycler view
        dailyRecyclerView = (RecyclerView) getView().findViewById(R.id.dailyRecyclerView);
        dailyRecyclerView.setHasFixedSize(true);

        //Daily Linear Layout Manager
        dailyLayoutManager = new LinearLayoutManager(getActivity()) {
            //Disable scrolling
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        dailyRecyclerView.setLayoutManager(dailyLayoutManager);

        //Update views
        currentLocation.setText(passedLocationValue);
        currentDate.setText(passedDateValue);
        //TODO - GET NEW ICONS AND UPDATE
        switch (passedIconValue) {
            case "clear-day":
                currentConditionsIcon.setImageResource(R.drawable.ic_sunny_white);
                break;
            case "clear-night":
                currentConditionsIcon.setImageResource(R.drawable.ic_clear_night_white);
                break;
            case "rain":
                currentConditionsIcon.setImageResource(R.drawable.ic_rain_white);
                break;
            case "snow":
                currentConditionsIcon.setImageResource(R.drawable.ic_snow_white);
                break;
            case "sleet":
                currentConditionsIcon.setImageResource(R.drawable.ic_sleet_white);
                break;
            case "wind":
                currentConditionsIcon.setImageResource(R.drawable.ic_windrose_white);
                break;
            case "fog":
                if (MainActivity.setID != 3) {
                    currentConditionsIcon.setImageResource(R.drawable.ic_foggyday_white);
                } else {
                    currentConditionsIcon.setImageResource(R.drawable.ic_foggynight_white);
                }
                break;
            case "cloudy":
                currentConditionsIcon.setImageResource(R.drawable.ic_cloudy_white);
                break;
            case "partly-cloudy-day":
                currentConditionsIcon.setImageResource(R.drawable.ic_partlycloudy_white);
                break;
            case "partly-cloudy-night":
                currentConditionsIcon.setImageResource(R.drawable.ic_partlycloudynight_white);
                break;
            default:
                currentConditionsIcon.setImageResource(R.drawable.ic_cloudy_white);
                Log.i("CurrentConditions", "Unsupported condition.");
                break;
        }

        currentTemp.setText(passedTempValue + "\u00B0");
        currentConditions.setText(passedConditionValue);
        todaysHiLo.setText(passedHILOValue);
        currentWindValue.setText(passedWindValue);
        currentPrecipValue.setText(String.valueOf(passedPrecipValue) + "%");
        currentHumidityValue.setText(String.valueOf(passedHumidityValue) + "%");
        currentDewPointValue.setText(String.valueOf(passedDewpointValue) + "\u00B0");
        currentPressureValue.setText(String.valueOf(passedPressureValue) + "inHg");
        currentVisibilityValue.setText(String.valueOf(passedVisibilityValue) + "mi");
        currentCloudCoverValue.setText(String.valueOf(passedCloudCoverValue) + "%");
        sunriseTime.setText(passedSunriseTimeValue);
        sunsetTime.setText(passedSunsetTimeValue);
//        updateTime.setText(passedUpdateTimeValue);

        //Setup adapters and load in data for recyclerViews
        //Hourly adapter
        hourlyRecyclerAdapter = new HourlyAdapter(getActivity(), passedHoursValues, passedTempsValues, passedConditionsValues, passedWindValues);
        hourlyRecyclerView.setAdapter(hourlyRecyclerAdapter);

        //Daily adapter
        dailyRecyclerAdapter = new DailyAdapter(getActivity(), passedDaysValues, passedDailyCondValues, passedHIsValues, passedLOsValues, passedPrecipValues);
        dailyRecyclerView.setAdapter(dailyRecyclerAdapter);

        //Set top layout slide in anim
        Animator topSlideIn = AnimatorInflater.loadAnimator(getActivity(), R.animator.slide_in);
        topSlideIn.setTarget(topLayout);
        topSlideIn.start();

        //Set lower layout slide in anim
        Animator slideIn = AnimatorInflater.loadAnimator(getActivity(), R.animator.slide_in_lower);
        slideIn.setTarget(wrapperLayout);
        slideIn.setStartDelay(100);
        slideIn.start();
    }

        /**
         * Saves passed values from mainActivity to fragment lists.
         */

    public static void passRecyclerDataSets(List<String> passedHours, List<Integer> passedTemps, List<String> passedConditions, List<Integer> passedWind,
                                            List<String> passedDays, List<String> passedDailyCond, List<Integer> passedHis, List<Integer> passedLos, List<Integer> passedPrecip) {
        Log.i("passDataSets", "called");

        //Hourly
        passedHoursValues = passedHours;
        passedTempsValues = passedTemps;
        passedConditionsValues = passedConditions;
        passedWindValues = passedWind;
        //Daily
        passedDaysValues = new ArrayList<>(passedDays);
        passedDailyCondValues = new ArrayList<>(passedDailyCond);
        passedHIsValues = new ArrayList<>(passedHis);
        passedLOsValues = new ArrayList<>(passedLos);
        passedPrecipValues = new ArrayList<>(passedPrecip);

    }

    /**
     * Saves passed values from mainActivity to current variables.
     */
    public static void passViewData(String passedLocation, String passedDate, String passedIcon, int passedTemp, String passedCondition, String passedHILO, String passedWind, int passedPrecip, int passedHumidity,
                                    int passedDewpoint, int passedPressure, String passedVisibility, int passedCloudCover, String passedSunriseTime, String passedSunsetTime, String passedUpdateTime) {

        passedLocationValue = passedLocation;
        passedDateValue = passedDate;
        passedIconValue = passedIcon;
        passedTempValue = passedTemp;
        passedConditionValue = passedCondition;
        passedHILOValue = passedHILO;
        passedWindValue = passedWind;
        passedPrecipValue = passedPrecip;
        passedHumidityValue = passedHumidity;
        passedDewpointValue = passedDewpoint;
        passedPressureValue = passedPressure;
        passedVisibilityValue = passedVisibility;
        passedCloudCoverValue = passedCloudCover;
        passedSunriseTimeValue = passedSunriseTime;
        passedSunsetTimeValue = passedSunsetTime;
        passedUpdateTimeValue = passedUpdateTime;
    }

    /**
     * Sets of fragment layout depending on time of day.
     */
    private void setFragmentLayoutColor() {
        switch (MainActivity.setID) {
            //Sunrise
            case 0:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorYellow));

                //Views
                currentWindValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentPrecipValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentHumidityValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentDewPointValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentPressureValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentVisibilityValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentCloudCoverValue.setTextColor(getResources().getColor(R.color.colorYellow));
                sunriseIcon.setColorFilter(getActivity().getResources().getColor(R.color.colorYellow));
                sunsetIcon.setColorFilter(getActivity().getResources().getColor(R.color.colorYellow));
                break;
            //Daytime
            case 1:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorBlue));

                //Views
                currentWindValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentPrecipValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentHumidityValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentDewPointValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentPressureValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentVisibilityValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentCloudCoverValue.setTextColor(getResources().getColor(R.color.colorBlue));
                sunriseIcon.setColorFilter(getActivity().getResources().getColor(R.color.colorBlue));
                sunsetIcon.setColorFilter(getActivity().getResources().getColor(R.color.colorBlue));
                break;
            //Sunset
            case 2:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorOrange));

                //Views
                currentWindValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentPrecipValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentHumidityValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentDewPointValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentPressureValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentVisibilityValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentCloudCoverValue.setTextColor(getResources().getColor(R.color.colorOrange));
                sunriseIcon.setColorFilter(getActivity().getResources().getColor(R.color.colorOrange));
                sunsetIcon.setColorFilter(getActivity().getResources().getColor(R.color.colorOrange));
                break;
            //Nighttime
            case 3:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorPurple));

                //Views
                currentWindValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentPrecipValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentHumidityValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentDewPointValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentPressureValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentVisibilityValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentCloudCoverValue.setTextColor(getResources().getColor(R.color.colorPurple));
                sunriseIcon.setColorFilter(getActivity().getResources().getColor(R.color.colorPurple));
                sunsetIcon.setColorFilter(getActivity().getResources().getColor(R.color.colorPurple));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

