package dean.weather;

import android.Manifest;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.johnhiott.darkskyandroidlib.ForecastApi;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //View pager
    static final int NUM_TABS = 4;
    pagerAdapter mainPagerAdapter;
    ViewPager mainViewPager;

    //Setup recyclerViews
    private RecyclerView hourlyRecyclerView;
    private RecyclerView.Adapter hourlyRecyclerAdapter;
    private RecyclerView.LayoutManager hourlyLayoutManager;

    private RecyclerView dailyRecyclerView;
    private RecyclerView.Adapter dailyRecyclerAdapter;
    private RecyclerView.LayoutManager dailyLayoutManager;

    TabLayout mainTabLayout;
    ImageView backgroundImage;
    AppBarLayout appbarLayout;
    LinearLayout topLayout;
    ImageView currentConditionsIcon;
    Typeface robotoLight;
    TextView currentTemp;
    TextView currentConditions;
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

    //Hourly
    public List<Integer> pulledHours;
    public List<Integer> pulledTemps;
    public List<String> pulledConditions;
    public List<Integer> pulledWind;

    //Daily
    private List<String> pulledDays;
    private List<String> pulleddailyCond;
    private List<Integer> pulledHIs;
    private List<Integer> pulledLOs;
    private List<Integer> pulledPrecips;

    GoogleApiClient googleApiClient;
    public String latitude;
    public String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an instance of GoogleAPIClient
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Connect to the Google API
        googleApiClient.connect();

        //Gather the location


        //Get the Dark Sky Wrapper API ready
        ForecastApi.create("331ebe65d3032e48b3c603c113435992");

        //Form a pull request
//        RequestBuilder weather = new RequestBuilder();
//        Request request = new Request();
//        request.setLat("32.00");
//        request.setLng("-81.00");
//        request.setUnits(Request.Units.US);
//        request.setLanguage(Request.Language.ENGLISH);
//
//        weather.getWeather(request, new Callback<WeatherResponse>() {
//            @Override
//            public void success(WeatherResponse weatherResponse, Response response) {
//                //Do something
//
//            }
//
//            @Override
//            public void failure(RetrofitError retrofitError) {
//                Log.e("It made a death", "Error while calling: " + retrofitError.getUrl());
//            }
//        });

        //TODO - GET THE TIME AND WEATHER CONDITIONS AND SET THE COLOR OF THE ACTIVITY

        //Set content view and customize header color
        setContentView(R.layout.activity_main);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorBlue)));
        icon = null;

        //Customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Customize the app bar
        assert toolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Weather");
        toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlueDark));

        //Set color of system bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBlueDark));

        //Set color of the top layout
        topLayout = (LinearLayout) findViewById(R.id.topContentLayout);
        topLayout.setBackgroundColor(getResources().getColor(R.color.colorBlue));

        //Customize views
        robotoLight = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");
        currentTemp = (TextView) findViewById(R.id.currentTemp);
        currentConditions = (TextView) findViewById(R.id.currentConditions);
        currentWind = (TextView) findViewById(R.id.currentDetailsWindLabel);
        currentHumidity = (TextView) findViewById(R.id.currentDetailsHumidityLabel);
        currentDewpoint = (TextView) findViewById(R.id.currentDetailsDewpointLabel);
        currentPressure = (TextView) findViewById(R.id.currentDetailsPressureLabel);
        currentVisibility = (TextView) findViewById(R.id.currentDetailsVisibilityLabel);
        currentCloudCover = (TextView) findViewById(R.id.currentDetailsCloudCoverLabel);
        currentWindValue = (TextView) findViewById(R.id.currentDetailsWindValue);
        currentHumidityValue = (TextView) findViewById(R.id.currentDetailsHumidityValue);
        currentDewPointValue = (TextView) findViewById(R.id.currentDetailsDewPointValue);
        currentPressureValue = (TextView) findViewById(R.id.currentDetailsPressureValue);
        currentVisibilityValue = (TextView) findViewById(R.id.currentDetailsVisibilityValue);
        currentCloudCoverValue = (TextView) findViewById(R.id.currentDetailsCloudCoverValue);
        currentTemp.setTypeface(robotoLight);
        currentConditions.setTypeface(robotoLight);
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

        //Turn off tab layout collapsing
        appbarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        toolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appbarLayoutParams = (CoordinatorLayout.LayoutParams) appbarLayout.getLayoutParams();
        appbarLayoutParams.setBehavior(null);
        appbarLayout.setLayoutParams(appbarLayoutParams);

        //Setup example hourly data sets
        pulledHours = new ArrayList<>();
        pulledTemps = new ArrayList<>();
        pulledConditions = new ArrayList<>();
        pulledWind = new ArrayList<>();
        //pulledHours
        int hour = 1;
        for (int i = 0; i < 12; i++) {
            pulledHours.add(hour);
            hour++;
        }
        //pulledTemps
        int temp = 65;
        for (int i = 0; i < 12; i++) {
            pulledTemps.add(temp);
            temp += 2;
        }
        //pulledConditions
        for (int i = 0; i < 12; i++) {
            pulledConditions.add("Overcast");
        }
        //pulledWind
        int wind = 4;
        for (int i = 0; i < 12; i++) {
            pulledWind.add(wind);
            wind += 3;
        }

        //Setup example daily datasets
        pulledDays = new ArrayList<>();
        pulleddailyCond = new ArrayList<>();
        pulledHIs = new ArrayList<>();
        pulledLOs = new ArrayList<>();
        pulledPrecips = new ArrayList<>();

        //Days
        int dateInt = 1;
        for(int i = 0; i < 8; i ++){
            String dates = "Sat";
            pulledDays.add(dates);
            dateInt ++;
        }

        //Conditions
        for(int i = 0; i < 8; i++){
            String condition = "clear";
            pulleddailyCond.add(condition);
        }

        //HIs
        int HI = 70;
        for(int i = 0; i < 10; i++){
            pulledHIs.add(HI);
            HI += 3;
        }

        //LOs
        int LO = 50;
        for(int i = 0; i < 10; i++){
            pulledLOs.add(LO);
            LO += 2;
        }

        //Precipitat
        int dailyPrecip = 3;
        for(int i = 0; i < 10; i++){
            pulledPrecips.add(dailyPrecip);
            dailyPrecip+= 3;
        }

        //Setup hourlyRecycler view
        hourlyRecyclerView = (RecyclerView) findViewById(R.id.hourlyRecyclerView);
        hourlyRecyclerView.setHasFixedSize(true);

        //Hourly Layout Manager
        hourlyLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hourlyRecyclerView.setLayoutManager(hourlyLayoutManager);

        //Hourly adapter
        hourlyRecyclerAdapter = new hourlyAdapter(this, pulledHours, pulledTemps, pulledConditions, pulledWind);
        hourlyRecyclerView.setAdapter(hourlyRecyclerAdapter);

        //Setup dailyRecycler view
        dailyRecyclerView = (RecyclerView) findViewById(R.id.dailyRecyclerView);
        dailyRecyclerView.setHasFixedSize(true);

        //Daily Linear Layout Manager
        dailyLayoutManager = new LinearLayoutManager(this);
        dailyRecyclerView.setLayoutManager(dailyLayoutManager);

        //Daily Setup adapter
        dailyRecyclerAdapter = new dailyAdapter(this, pulledDays, pulledConditions, pulledHIs, pulledLOs, pulledPrecips);
        dailyRecyclerView.setAdapter(dailyRecyclerAdapter);
    }

    //Action bar events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Settings
            case R.id.action_settings:
                return true;
            //Rate the app
            case R.id.action_rate:
                return true;
            //Refresh data
            case R.id.action_refresh:
                return true;
            //Choose current location
            case R.id.action_current_location:
                return true;
            //User action not recognized
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_items, menu);
        return true;
    }

    //Google API Client
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int loacationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        //TODO - Implement logic to display blank activity telling the user to pick a location or to enable location services if current location is selected
        if(loacationPermissionCheck == PackageManager.PERMISSION_GRANTED){
            //Gather location and display data properly
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(lastLocation != null){
                latitude = String.valueOf(lastLocation.getLatitude());
                longitude = String.valueOf(lastLocation.getLongitude());

            }
        }
        else{
            //Tell the user to enable location services

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }
}
