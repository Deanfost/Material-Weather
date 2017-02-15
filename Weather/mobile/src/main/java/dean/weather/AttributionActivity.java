package dean.weather;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Dean Foster on 2/12/2017.
 */

public class AttributionActivity extends AppCompatActivity {
    RelativeLayout clientLayout;
    RelativeLayout SDPLayout;
    RelativeLayout introLayout;
    RelativeLayout arcLoaderLayout;
    RelativeLayout assetStudioLayot;
    RelativeLayout vectorizerLayout;
    RelativeLayout iconsLayout;
    RelativeLayout AOSPLayout;
    LinearLayout apacheLayout;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);

        //Setup app bar
        Toolbar licensesToolbar = (Toolbar) findViewById(R.id.licensesToolbar);
        setSupportActionBar(licensesToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attribution");
        //Set color of appbar title
        licensesToolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));

        //Set color of upp arrow
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorLightBlack), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        //Set the color of the layout
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorGrey));

        //References
        apacheLayout = (LinearLayout) findViewById(R.id.licensesApacheLayout);

        apacheLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Move to Web view activity showing the Apache 2.0 License
                Log.i("Apache", "Clicked");
                Intent licenseIntent = new Intent(AttributionActivity.this, ApacheViewActivity.class);
                startActivity(licenseIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
