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
    private List<Integer> pulledDates;
    private List<Integer> pulledDescriptions;
    private List<String> pulledConditions;
    private List<String> pulledHILOs;
    private List<String> pulledPrecip;
    private List<String> pulledWind;
    Context pulledContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourly_activity);

        //Set toolbar
        Toolbar dailyToolbar = (Toolbar) findViewById(R.id.dailyToolbar);
        setSupportActionBar(dailyToolbar);

        //Customize the app bar
        assert dailyToolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Hourly");
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
        pulledHILOs = new ArrayList<>();
        pulledPrecip = new ArrayList<>();
        pulledWind = new ArrayList<>();

        //TODO setup example datasets
        

        //Setup recycler view
        dailyRecyclerView = (RecyclerView) findViewById(R.id.dailyRecyclerView);
        dailyRecyclerView.setHasFixedSize(true);

        //Linear Layout Manager
        dailyLayoutManager = new LinearLayoutManager(this);
        dailyRecyclerView.setLayoutManager(dailyLayoutManager);

        //Setup adapter
        pulledContext = this;
        dailyRecyclerAdapter = new hourlyAdapter(pulledContext, pulledDates, pulledDescriptions, pulledConditions, pulledHILOs);
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
