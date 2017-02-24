package dean.weather;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.google.firebase.FirebaseApp;

/**
 * Created by Dean Foster on 2/12/2017.
 */

public class AttributionActivity extends AppCompatActivity {
    RelativeLayout clientLayout;
    RelativeLayout SDPLayout;
    RelativeLayout introLayout;
    RelativeLayout arcLoaderLayout;
    RelativeLayout assetStudioLayout;
    RelativeLayout vectorizerLayout;
    RelativeLayout iconsLayout;
    RelativeLayout darkSkyLayout;
    RelativeLayout AOSPLayout;
    LinearLayout apacheLayout;
    LinearLayout MITLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_attribution);

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
        clientLayout = (RelativeLayout) findViewById(R.id.licensesClientLibLayout);
        SDPLayout = (RelativeLayout) findViewById(R.id.licensesSDPLayout);
        introLayout = (RelativeLayout) findViewById(R.id.licensesMaterialIntroLayout);
        arcLoaderLayout = (RelativeLayout) findViewById(R.id.licensesArcLoaderLayout);
        assetStudioLayout = (RelativeLayout) findViewById(R.id.licensesAssetStudioLayout);
        vectorizerLayout = (RelativeLayout) findViewById(R.id.licensesVectorizerLayout);
        iconsLayout = (RelativeLayout) findViewById(R.id.licensesIcons8Layout);
        darkSkyLayout = (RelativeLayout) findViewById(R.id.licensesDarkSkyLayout);
        AOSPLayout = (RelativeLayout) findViewById(R.id.licensesAOSPLayoout);
        apacheLayout = (LinearLayout) findViewById(R.id.licensesApacheLayout);
        MITLayout = (LinearLayout) findViewById(R.id.licensesMITLayout);
    }

    @Override
    protected void onResume() {
        //Click listeners
        clientLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Client", "Clicked");
                //Move to github page of the lib
                String url = "https://github.com/johnhiott/DarkSkyApi";
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(url));
                startActivity(webIntent);
            }
        });

        SDPLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SDP", "Clicked");
                //Move to github page of the lib
                String url = "https://github.com/intuit/sdp";
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(url));
                startActivity(webIntent);
            }
        });

        introLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Intro", "Clicked");
                //Move to github page of the lib
                String url = "https://github.com/HeinrichReimer/material-intro";
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(url));
                startActivity(webIntent);
            }
        });

        arcLoaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Arc", "Clicked");
                //Move to github page of the lib
                String url = "https://github.com/generic-leo/SimpleArcLoader";
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(url));
                startActivity(webIntent);
            }
        });

        assetStudioLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Asset studio", "Clicked");
                //Move to android asset studio page
                String url = "https://romannurik.github.io/AndroidAssetStudio/";
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(url));
                startActivity(webIntent);
            }
        });

        vectorizerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Vectorizer", "Clicked");
                //Move to vectorizer.io
                String url = "https://www.vectorizer.io/";
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(url));
                startActivity(webIntent);
            }
        });

        iconsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Icons", "Clicked");
                //Move to icons8.com
                String url = "https://icons8.com/";
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(url));
                startActivity(webIntent);
            }
        });

        darkSkyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Move to dark sky
                String url = "https://darksky.net/poweredby/";
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(url));
                startActivity(webIntent);
            }
        });

        AOSPLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("AOSP", "Clicked");
                //Move to Android Open Source Project
                String url = "https://source.android.com/";
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(url));
                startActivity(webIntent);
            }
        });

        apacheLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Move to Web view activity showing the Apache 2.0 License
                Log.i("Apache", "Clicked");
                Intent licenseIntent = new Intent(AttributionActivity.this, ApacheViewActivity.class);
                startActivity(licenseIntent);
            }
        });

        MITLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Move to Web view activity showing the Apache 2.0 License
                Log.i("MIT", "Clicked");
                Intent licenseIntent = new Intent(AttributionActivity.this, MITVIewActivity.class);
                startActivity(licenseIntent);
            }
        });
        super.onResume();
    }

    @Override
    protected void onPause() {
        darkSkyLayout.setOnClickListener(null);
        SDPLayout.setOnClickListener(null);
        introLayout.setOnClickListener(null);
        arcLoaderLayout.setOnClickListener(null);
        assetStudioLayout.setOnClickListener(null);
        vectorizerLayout.setOnClickListener(null);
        iconsLayout.setOnClickListener(null);
        darkSkyLayout.setOnClickListener(null);
        AOSPLayout.setOnClickListener(null);
        apacheLayout.setOnClickListener(null);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Attribution Activity", "Finishing");
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
