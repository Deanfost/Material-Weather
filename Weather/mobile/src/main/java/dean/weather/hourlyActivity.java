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
 * Created by DeanF on 10/1/2016.
 */

//public class hourlyActivity extends AppCompatActivity {
//    private RecyclerView hourlyRecyclerView;
//    private RecyclerView.Adapter hourlyRecyclerAdapter;
//    private RecyclerView.LayoutManager hourlyLayoutManager;
//    //Example data sets for testing
//    private List<Integer> pulledHours;
//    private List<Integer> pulledTemps;
//    private List<String> pulledConditions;
//    private List<String> pulledWind;
//    Context pulledContext;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.hourly_activity);
//
//        //Set toolbar
//        Toolbar hourlyToolbar = (Toolbar) findViewById(R.id.hourlyToolbar);
//        setSupportActionBar(hourlyToolbar);
//
//        //Customize the app bar
//        assert hourlyToolbar != null;
//        assert getSupportActionBar() != null;
//        getSupportActionBar().setTitle("Hourly");
//        hourlyToolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlue));
//        //Enable up functions
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//
//        //Set color of system bar
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(this.getResources().getColor(R.color.colorBlue));
//
//        //Setup example datasets
//        pulledHours = new ArrayList<>();
//        pulledTemps = new ArrayList<>();
//        pulledConditions = new ArrayList<>();
//        pulledWind = new ArrayList<>();
//        //pulledHours
//        int hour = 1;
//        for(int i = 0; i < 12; i++){
//            pulledHours.add(hour);
//            hour++;
//        }
//        //pulledTemps
//        int temp = 65;
//        for(int i = 0; i < 12; i++){
//            pulledTemps.add(temp);
//            temp+= 2;
//        }
//        //pulledConditions
//        for(int i = 0; i < 12; i++){
//            pulledConditions.add("Overcast");
//        }
//        //pulledWind
//        int precip = 4;
//        for(int i = 0; i < 12; i++){
//            pulledWind.add(String.valueOf(precip) + "%");
//            precip+= 3;
//        }
//
//        //Setup recycler view
//        hourlyRecyclerView = (RecyclerView) findViewById(R.id.dailyRecyclerView);
//        hourlyRecyclerView.setHasFixedSize(true);
//
//        //Linear Layout Manager
//        hourlyLayoutManager = new LinearLayoutManager(this);
//        hourlyRecyclerView.setLayoutManager(hourlyLayoutManager);
//
//        //Setup adapter
//        pulledContext = this;
//        hourlyRecyclerAdapter = new hourlyAdapter(pulledContext, pulledHours, pulledTemps, pulledConditions, pulledWind);
//        hourlyRecyclerView.setAdapter(hourlyRecyclerAdapter);
//    }
//
//    //Action bar events
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()){
//            //Up button
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//            //User action not recognized
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//}
