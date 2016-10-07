package dean.weather;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by DeanF on 10/1/2016.
 */

public class detailsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        //Set toolbar
        Toolbar detailsToolbar = (Toolbar) findViewById(R.id.detailsToolbar);
        setSupportActionBar(detailsToolbar);

        //Customize the app bar
        assert detailsToolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Details");
        detailsToolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlue));
        //Enable up functions
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set color of system bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBlue));

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
