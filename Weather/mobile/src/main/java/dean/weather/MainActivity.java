package dean.weather;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity{
    //View pager
    static final int NUM_TABS = 4;
    pagerAdapter mainPagerAdapter;
    ViewPager mainViewPager;
    TabLayout mainTabLayout;
    ImageView backgroundImage;
    AppBarLayout appbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Customize the app bar
        assert toolbar != null;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Boston, MA");
//        toolbar.setSubtitle("May 5, 2016");
        toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorBlue));

        //Set color of system bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBlue));

        //Setup pager and adapter
        mainPagerAdapter = new pagerAdapter(getSupportFragmentManager());
        mainViewPager = (ViewPager) findViewById(R.id.viewPager);
        //Setup tab navigation
        mainTabLayout = (TabLayout) findViewById(R.id.tabs);
        mainPagerAdapter.addFragment(new locationsFrag(), "Locations");
        mainPagerAdapter.addFragment(new detailsFrag(), "Details");
        mainPagerAdapter.addFragment(new hourlyFrag(), "Hourly");
        mainPagerAdapter.addFragment(new dailyFrag(), "Daily");
        mainViewPager.setAdapter(mainPagerAdapter);
        mainTabLayout.setupWithViewPager(mainViewPager);
        mainTabLayout.getTabAt(0).setIcon(R.drawable.ic_currentlocation);
        mainTabLayout.getTabAt(1).setIcon(R.drawable.ic_details);
        mainTabLayout.getTabAt(2).setIcon(R.drawable.ic_hourly);
        mainTabLayout.getTabAt(3).setIcon(R.drawable.ic_daily);
        mainViewPager.setCurrentItem(1);

        //Turn off tab layout collapsing
        appbarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        toolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appbarLayoutParams = (CoordinatorLayout.LayoutParams) appbarLayout.getLayoutParams();
        appbarLayoutParams.setBehavior(null);
        appbarLayout.setLayoutParams(appbarLayoutParams);

        //Load background image
        backgroundImage = (ImageView) findViewById(R.id.background_image_view);
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        final int screenWidth = size.x;
//        final int screenHeight = size.y;

        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), R.drawable.foggy_mountains, options);
//                int imageHeight = options.outHeight;
//                int imageWidth = options.outWidth;
//                String imageType = options.outMimeType;

                backgroundImage.setImageBitmap(
                        decodeSampledBitmapFromResource(getResources(), R.drawable.foggy_mountains, 500,500));
            }
        });

    }
    //Action bar events
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

    //Image loading
        public static int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            //Height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Subsample the image
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                             int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        }

}
