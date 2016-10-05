package dean.weather;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

        //Customize the app bar and status bar
        assert detailsToolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Details");
        detailsToolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlue));
        //Enable up functions
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }




    //Action bar events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
