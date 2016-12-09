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

public class DailyActivity extends AppCompatActivity {
    private RecyclerView hourlyRecyclerView;
    private RecyclerView.Adapter hourlyRecyclerAdapter;
    private RecyclerView.LayoutManager hourlyLayoutManager;

    //This day's hourly data sets
    private List<String> pulledHours;
    private List<String> pulledConditions;
    private List<Integer> pulledIcons;
    private List<String> pulledWind;

    //View data
    Context pulledContext;
    private Integer passedDayInt;
    private String passedDay;
    private String pulledDescription;
    private String pulledLocation;
    private Integer pulledHi;
    private Integer pulledLo;
    private String pulledIcon;
    private String pulledCondition;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        //Set toolbar
        Toolbar dailyToolbar = (Toolbar) findViewById(R.id.dailyToolbar);
        setSupportActionBar(dailyToolbar);

        //Customize the app bar
        assert dailyToolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("IS THE DAY");
        dailyToolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlue));
        //Enable up functions
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set color of system bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBlue));

        //Setup recycler view
//        dailyRecyclerView = (RecyclerView) findViewById(R.id.dailyRecyclerView);
//        dailyRecyclerView.setHasFixedSize(true);
//
//        //Linear Layout Manager
//        dailyLayoutManager = new LinearLayoutManager(this);
//        dailyRecyclerView.setLayoutManager(dailyLayoutManager);
//
//        //Setup adapter
//        pulledContext = this;
//        dailyRecyclerAdapter = new DailyAdapter(pulledContext, pulledDates, pulledConditions, pulledHIs, pulledLOs, pulledPrecips);
//        dailyRecyclerView.setAdapter(dailyRecyclerAdapter);
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
