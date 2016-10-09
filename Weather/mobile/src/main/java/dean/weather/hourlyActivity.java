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

/**
 * Created by DeanF on 10/1/2016.
 */

public class hourlyActivity extends AppCompatActivity {
    private RecyclerView detailsRecyclerView;
    private RecyclerView.Adapter detailsRecyclerAdapter;
    private RecyclerView.LayoutManager detailsLayoutManager;
    //Example data sets for testing
    private int[] pulledHours;
    private int[] pulledTemps;
    private String[] pulledConditions;
    Context pulledContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourly_activity);

        //Set toolbar
        Toolbar detailsToolbar = (Toolbar) findViewById(R.id.detailsToolbar);
        setSupportActionBar(detailsToolbar);

        //Customize the app bar
        assert detailsToolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Hourly");
        detailsToolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlue));
        //Enable up functions
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set color of system bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBlue));

        //Setup example datasets
        //pulledHours
        int hour = 1;
        for(int i = 0; i < 12; i++){
            pulledHours[i] = hour;
            hour++;
        }
        //pulledTemps
        int temp = 65;
        for(int i = 0; i < 12; i++){
            pulledTemps[i] = temp;
            temp+= 2;
        }
        //pulledConditions
        for(int i = 0; i < 12; i++){
            pulledConditions[i] = "Overcast";
        }

        //Setup recycler view
        detailsRecyclerView = (RecyclerView) findViewById(R.id.detailsRecyclerView);
        detailsRecyclerView.setHasFixedSize(true);

        //Linear Layout Manager
        detailsLayoutManager = new LinearLayoutManager(this);
        detailsRecyclerView.setLayoutManager(detailsLayoutManager);

        //Setup adapter
        pulledContext = this;
        detailsRecyclerAdapter = new hourlyAdapter(pulledContext, pulledHours, pulledTemps, pulledConditions);
        detailsRecyclerView.setAdapter(detailsRecyclerAdapter);
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
