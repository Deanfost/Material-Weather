package dean.weather;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    //View pager
    static final int NUM_TABS = 4;
    pagerAdapter mainPagerAdapter;
    ViewPager mainViewPager;
    TabLayout mainTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Customize the app bar and status bar
        assert toolbar != null;
        getSupportActionBar().setTitle("Boston, MA");
        toolbar.setSubtitle("May 5, 2016");
        toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
        //Set color of system bar
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        //Setup pager and adapter
        mainPagerAdapter = new pagerAdapter(getSupportFragmentManager());
        mainViewPager = (ViewPager) findViewById(R.id.viewPager);
        //Setup tab navigation
        mainTabLayout = (TabLayout) findViewById(R.id.tabs);
        mainPagerAdapter.addFragment(new locationsFrag(), "Daily");
        mainPagerAdapter.addFragment(new detailsFrag(), "Details");
        mainPagerAdapter.addFragment(new hourlyFrag(), "Hourly");
        mainPagerAdapter.addFragment(new dailyFrag(), "Daily");
        mainViewPager.setAdapter(mainPagerAdapter);
        mainTabLayout.setupWithViewPager(mainViewPager);

    }
    //Action bar events

        /**
         * Handles action selection.
         * @param item
         * @return
         */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()){
                //Settings
                case R.id.action_settings:
                    return true;
                //Remove ads
                case R.id.action_remove_ads:
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
}
