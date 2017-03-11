package dean.weather;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.google.firebase.FirebaseApp;

/**
 * Created by Dean Foster on 2/13/2017.
 */

public class MITVIewActivity extends AppCompatActivity {
    WebView apacheWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.license_view_activity);

        //Setup app bar
        Toolbar licensesToolbar = (Toolbar) findViewById(R.id.licenseVIewToolbar);
        setSupportActionBar(licensesToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("MIT License");
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
        window.setStatusBarColor(getResources().getColor(R.color.colorWhiteDark));

        //References
        apacheWebView = (WebView) findViewById(R.id.apacheWebView);

        //Load in html
        apacheWebView.getSettings().setBuiltInZoomControls(true);
        apacheWebView.loadUrl("file:///android_asset/mit.html");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Apache Activity", "Finishing");
    }
}