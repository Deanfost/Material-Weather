package dean.weather;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

/**
 * Created by Dean Foster on 2/13/2017.
 */

public class ApacheViewActivity extends AppCompatActivity {
    WebView apacheWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apache_activity);

        //Setup app bar
        Toolbar licensesToolbar = (Toolbar) findViewById(R.id.apacheToolbar);
        setSupportActionBar(licensesToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Apache 2.0");
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
        apacheWebView = (WebView) findViewById(R.id.apacheWebView);

        //Load in html
        String license = getResources().getString(R.string.apache_2_0);
        String other = "<html><body>asuh<ul><li>woah</li></ul></body></html>";
        apacheWebView.loadData(other, "text/html", null);
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
