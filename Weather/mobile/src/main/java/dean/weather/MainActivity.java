package dean.weather;

import android.Manifest;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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

    private RecyclerView hourlyRecyclerView;
    private RecyclerView.Adapter hourlyRecyclerAdapter;
    private RecyclerView.LayoutManager hourlyLayoutManager;

    TabLayout mainTabLayout;
    ImageView backgroundImage;
    AppBarLayout appbarLayout;
    LinearLayout topLayout;
    ImageView currentConditionsIcon;
    Typeface robotoLight;
    TextView currentTemp;
    TextView currentConditions;

    public List<Integer> pulledHours;
    public List<Integer> pulledTemps;
    public List<String> pulledConditions;
    public List<String> pulledPrecip;

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
        currentTemp.setTypeface(robotoLight);
        currentConditions.setTypeface(robotoLight);

        //Setup pager and adapter
//        mainPagerAdapter = new pagerAdapter(getSupportFragmentManager());
//        mainViewPager = (ViewPager) findViewById(R.id.viewPager);

        //Setup example hourly data sets
        pulledHours = new ArrayList<>();
        pulledTemps = new ArrayList<>();
        pulledConditions = new ArrayList<>();
        pulledPrecip = new ArrayList<>();
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
        //pulledPrecip
        int precip = 4;
        for (int i = 0; i < 12; i++) {
            pulledPrecip.add(String.valueOf(precip) + "%");
            precip += 3;
        }

        //Setup recycler view
        hourlyRecyclerView = (RecyclerView) findViewById(R.id.hourlyRecyclerView);
        hourlyRecyclerView.setHasFixedSize(true);

        //Linear Layout Manager
        hourlyLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hourlyRecyclerView.setLayoutManager(hourlyLayoutManager);

        //Setup adapter
        hourlyRecyclerAdapter = new hourlyAdapter(this, pulledHours, pulledTemps, pulledConditions, pulledPrecip);
        hourlyRecyclerView.setAdapter(hourlyRecyclerAdapter);
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
