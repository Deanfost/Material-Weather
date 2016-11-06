package dean.weather;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DeanF on 11/6/2016.
 */

public class mainFragment extends Fragment{

    //Hourly
    public static List<Integer> passedHours;
    public static List<Integer> passedTemps;
    public static List<String> passedConditions;
    public static List<Integer> passedWind;

    //Daily
    private static List<String> passedDays;
    private static List<String> passedDailyCond;
    private static List<Integer> passedHIs;
    private static List<Integer> passedLOs;
    private static List<Integer> passedPrecip;

    //Setup recyclerViews
    private RecyclerView hourlyRecyclerView;
    private RecyclerView.Adapter hourlyRecyclerAdapter;
    private RecyclerView.LayoutManager hourlyLayoutManager;

    private RecyclerView dailyRecyclerView;
    private RecyclerView.Adapter dailyRecyclerAdapter;
    private RecyclerView.LayoutManager dailyLayoutManager;

    private static String passedTempValue;
    private static String passedHILOValue;
    private static String passedWindValue;
    private static int passedHumidityValue;
    private static int passedDewpointValue;
    private static String passedPressureValue;
    private static String passedVisibilityValue;
    private static int passedCloudCoverValue;
    private static int passedSunriseTimeValue;
    private static int passedSunsetTimeValue;
    private static int passedUpdateTimeValue;

    Typeface robotoLight;
    LinearLayout topLayout;
    ImageView currentConditionsIcon;
    TextView currentTemp;
    TextView currentConditions;
    TextView todaysHiLo;
    TextView currentWind;
    TextView currentHumidity;
    TextView currentDewpoint;
    TextView currentPressure;
    TextView currentVisibility;
    TextView currentCloudCover;
    TextView currentWindValue;
    TextView currentHumidityValue;
    TextView currentDewPointValue;
    TextView currentPressureValue;
    TextView currentVisibilityValue;
    TextView currentCloudCoverValue;
    TextView sunriseTime;
    TextView sunsetTime;
    TextView updateTime;

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
        topLayout = (LinearLayout) getView().findViewById(R.id.topContentLayout);
        currentTemp = (TextView) getView().findViewById(R.id.currentTemp);
        currentConditions = (TextView) getView().findViewById(R.id.currentConditions);
        todaysHiLo = (TextView) getView().findViewById(R.id.todaysHiLo);
        currentWind = (TextView) getView().findViewById(R.id.currentDetailsWindLabel);
        currentHumidity = (TextView) getView().findViewById(R.id.currentDetailsHumidityLabel);
        currentDewpoint = (TextView) getView().findViewById(R.id.currentDetailsDewpointLabel);
        currentPressure = (TextView) getView().findViewById(R.id.currentDetailsPressureLabel);
        currentVisibility = (TextView) getView().findViewById(R.id.currentDetailsVisibilityLabel);
        currentCloudCover = (TextView) getView().findViewById(R.id.currentDetailsCloudCoverLabel);
        currentWindValue = (TextView) getView().findViewById(R.id.currentDetailsWindValue);
        currentHumidityValue = (TextView) getView().findViewById(R.id.currentDetailsHumidityValue);
        currentDewPointValue = (TextView) getView().findViewById(R.id.currentDetailsDewPointValue);
        currentPressureValue = (TextView) getView().findViewById(R.id.currentDetailsPressureValue);
        currentVisibilityValue = (TextView) getView().findViewById(R.id.currentDetailsVisibilityValue);
        currentCloudCoverValue = (TextView) getView().findViewById(R.id.currentDetailsCloudCoverValue);
        sunriseTime = (TextView) getView().findViewById(R.id.sunriseTime);
        sunsetTime = (TextView) getView().findViewById(R.id.sunsetTime);
        updateTime = (TextView) getView().findViewById(R.id.updateTime);

        //Typeface
        currentTemp.setTypeface(robotoLight);
        currentConditions.setTypeface(robotoLight);
        todaysHiLo.setTypeface(robotoLight);
        currentWind.setTypeface(robotoLight);
        currentHumidity.setTypeface(robotoLight);
        currentDewpoint.setTypeface(robotoLight);
        currentPressure.setTypeface(robotoLight);
        currentVisibility.setTypeface(robotoLight);
        currentCloudCover.setTypeface(robotoLight);
        currentWindValue.setTypeface(robotoLight);
        currentHumidityValue.setTypeface(robotoLight);
        currentDewPointValue.setTypeface(robotoLight);
        currentPressureValue.setTypeface(robotoLight);
        currentVisibilityValue.setTypeface(robotoLight);
        currentCloudCoverValue.setTypeface(robotoLight);
        sunriseTime.setTypeface(robotoLight);
        sunsetTime.setTypeface(robotoLight);
        updateTime.setTypeface(robotoLight);

        //Setup example hourly data sets
        passedHours = new ArrayList<>();
        passedTemps = new ArrayList<>();
        passedConditions = new ArrayList<>();
        passedWind = new ArrayList<>();
        //passedHours
        int hour = 1;
        for (int i = 0; i < 12; i++) {
            passedHours.add(hour);
            hour++;
        }
        //passedTemps
        int temp = 65;
        for (int i = 0; i < 12; i++) {
            passedTemps.add(temp);
            temp += 2;
        }
        //passedConditions
        for (int i = 0; i < 12; i++) {
            passedConditions.add("Overcast");
        }
        //passedWind
        int wind = 4;
        for (int i = 0; i < 12; i++) {
            passedWind.add(wind);
            wind += 3;
        }

        //Setup example daily datasets
        passedDays = new ArrayList<>();
        passedDailyCond = new ArrayList<>();
        passedHIs = new ArrayList<>();
        passedLOs = new ArrayList<>();
        passedPrecip = new ArrayList<>();

        //Days
        int dateInt = 1;
        for(int i = 0; i < 8; i ++){
            String dates = "Sat";
            passedDays.add(dates);
            dateInt ++;
        }

        //Conditions
        for(int i = 0; i < 8; i++){
            String condition = "clear";
            passedDailyCond.add(condition);
        }

        //HIs
        int HI = 70;
        for(int i = 0; i < 10; i++){
            passedHIs.add(HI);
            HI += 3;
        }

        //LOs
        int LO = 50;
        for(int i = 0; i < 10; i++){
            passedLOs.add(LO);
            LO += 2;
        }

        //Precipitation
        int dailyPrecip = 3;
        for(int i = 0; i < 10; i++){
            passedPrecip.add(dailyPrecip);
            dailyPrecip+= 3;
        }

        //Set color
        setFragmentLayoutColor();

        //Display data
        setViews();
    }

    /**
     * Updates views with data from API.
     */
    private void setViews(){

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
        dailyLayoutManager = new LinearLayoutManager(getActivity());
        dailyRecyclerView.setLayoutManager(dailyLayoutManager);

        //Update views
        //TODO - SET VIEWS TO "--" IN XML TO SIGNIFY NO DATA IF IT IS NOT CHANGED

        //Setup adapters and load in data for recyclerViews
        //Hourly adapter
        hourlyRecyclerAdapter = new hourlyAdapter(getActivity(), passedHours, passedTemps, passedConditions, passedWind);
        hourlyRecyclerView.setAdapter(hourlyRecyclerAdapter);

        //Daily adapter
        dailyRecyclerAdapter = new dailyAdapter(getActivity(), passedDays, passedConditions, passedHIs, passedLOs, passedPrecip);
        dailyRecyclerView.setAdapter(dailyRecyclerAdapter);

    }

    /**
     * Saves passed values from mainActivity to fragment lists.
     */
    public static void passRecyclerDataSets(List<Integer> passedHours, List<Integer> passedTemps, List<String> passedConditions, List<Integer> passedWind,
                                            List<String> passedDays, List<String> passedDailyCond, List<Integer> passedHis, List<Integer> passedLos, List<Integer> passedPrecip){

        mainFragment.passedHours = new ArrayList<>(passedHours);
        mainFragment.passedTemps = new ArrayList<>(passedTemps);
        mainFragment.passedConditions = new ArrayList<>(passedConditions);
        mainFragment.passedWind = new ArrayList<>(passedWind);
        mainFragment.passedDays = new ArrayList<>(passedDays);
        mainFragment.passedDailyCond = new ArrayList<>(passedDailyCond);
        mainFragment.passedHIs = new ArrayList<>(passedHis);
        mainFragment.passedLOs = new ArrayList<>(passedLos);
        mainFragment.passedPrecip = new ArrayList<>(passedPrecip);

    }

    /**
     * Saves passed values from mainActivity to current variables.
     */
    public static void passCurrentDataSets(String passedTemp, String passedHILO, String passedWind, int passedHumidity, int passedDewpoint,
    String passedPressure, String passedVisibility, int passedCloudCover, int passedSunriseTime, int passedSunsetTime, int passedUpdateTime ){

        passedTempValue = passedTemp;
        passedHILOValue = passedHILO;
        passedWindValue = passedWind;
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
     * Sets color of fragment layout depending on time of day.
     */
    private void setFragmentLayoutColor(){
        switch (MainActivity.setID){
            //Sunrise
            case 0:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorYellow));

                //Views
                currentWindValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentHumidityValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentDewPointValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentPressureValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentVisibilityValue.setTextColor(getResources().getColor(R.color.colorYellow));
                currentCloudCoverValue.setTextColor(getResources().getColor(R.color.colorYellow));
                break;
            //Daytime
            case 1:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorBlue));

                //Views
                currentWindValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentHumidityValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentDewPointValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentPressureValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentVisibilityValue.setTextColor(getResources().getColor(R.color.colorBlue));
                currentCloudCoverValue.setTextColor(getResources().getColor(R.color.colorBlue));
                break;
            //Sunset
            case 2:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorOrange));

                //Views
                currentWindValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentHumidityValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentDewPointValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentPressureValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentVisibilityValue.setTextColor(getResources().getColor(R.color.colorOrange));
                currentCloudCoverValue.setTextColor(getResources().getColor(R.color.colorOrange));
                break;
            //Nighttime
            case 3:
                //Top layout
                topLayout.setBackgroundColor(getResources().getColor(R.color.colorPurple));

                //Views
                currentWindValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentHumidityValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentDewPointValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentPressureValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentVisibilityValue.setTextColor(getResources().getColor(R.color.colorPurple));
                currentCloudCoverValue.setTextColor(getResources().getColor(R.color.colorPurple));
                break;
        }
    }
}
