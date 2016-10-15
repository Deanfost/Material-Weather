package dean.weather;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DeanF on 10/13/2016.
 */

public class dailyActivity extends AppCompatActivity {
    private RecyclerView dailyRecyclerView;
    private RecyclerView.Adapter dailyRecyclerAdapter;
    private RecyclerView.LayoutManager dailyLayoutManager;
    //Example data sets for testing
    private List<String> pulledDates;
    private List<String> pulledDescriptions;
    private List<String> pulledConditions;
    private List<Integer> pulledHIs;
    private List<Integer> pulledLOs;
    private List<Integer> pulledPrecips;
    private List<String> pulledWind;
    Context pulledContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_activity);

        //Set toolbar
        Toolbar dailyToolbar = (Toolbar) findViewById(R.id.dailyToolbar);
        setSupportActionBar(dailyToolbar);

        //Customize the app bar
        assert dailyToolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Daily");
        dailyToolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlue));
        //Enable up functions
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set color of system bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBlue));

        //Setup example datasets
        pulledDates = new ArrayList<>();
        pulledDescriptions = new ArrayList<>();
        pulledConditions = new ArrayList<>();
        pulledHIs = new ArrayList<>();
        pulledLOs = new ArrayList<>();
        pulledPrecips = new ArrayList<>();
        pulledWind = new ArrayList<>();

        //Dates
        int dateInt = 1;
        for(int i = 0; i < 8; i ++){
            String dates = "October " + String.valueOf(dateInt);
            pulledDates.add(dates);
            dateInt ++;
        }

        //Descriptions
        for(int i = 0; i < 8; i ++){
            String description = "Clear throughout the day.";
            pulledDescriptions.add(description);
        }

        //Conditions
        for(int i = 0; i < 8; i++){
            String condition = "clear";
            pulledConditions.add(condition);
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

        //Precipitation
        int precip = 3;
        for(int i = 0; i < 10; i++){
            pulledPrecips.add(precip);
            precip+= 3;
        }

        //Wind
        int speed = 1;
        for(int i = 0; i < 10; i++){
            pulledWind.add("N" + String.valueOf(speed) + "MPH");
            speed+= 2;
        }

        //Setup recycler view
        dailyRecyclerView = (RecyclerView) findViewById(R.id.dailyRecyclerView);
        dailyRecyclerView.setHasFixedSize(true);

        //Linear Layout Manager
        dailyLayoutManager = new LinearLayoutManager(this);
        dailyRecyclerView.setLayoutManager(dailyLayoutManager);

        //Setup adapter
        pulledContext = this;
        dailyRecyclerAdapter = new dailyAdapter(pulledContext, pulledDates, pulledDescriptions, pulledConditions, pulledHIs, pulledLOs, pulledPrecips, pulledWind);
        dailyRecyclerView.setAdapter(dailyRecyclerAdapter);
    }

    //Action bar events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //Up button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            //User action not recognized
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
