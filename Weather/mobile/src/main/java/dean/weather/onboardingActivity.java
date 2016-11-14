package dean.weather;


import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

/**
 * Created by DeanF on 10/29/2016.
 */

public class OnboardingActivity extends IntroActivity{
    static int LOCATION_PERMISSIONS_REQUEST = 42;
    private Fragment onBoardingFragPermissions;
    private Class onBoardingFragThreeClass;
    public static Activity currentActivity;
    private android.app.Fragment OnboardingFragStartMain;
    private Class OnboardingFragStartMainClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
        try {
            onBoardingFragThreeClass = OnboardingFragThree.class;
            onBoardingFragPermissions = (Fragment) onBoardingFragThreeClass.newInstance();
            OnboardingFragStartMainClass = OnboardingFragStartMain.class;
            OnboardingFragStartMain = (Fragment) OnboardingFragStartMainClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Create slide 1
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(R.color.colorBlue)
                .background(R.color.colorBlueLight)
                .fragment(R.layout.onboarding_frag_one)
                .build());

        //Create slide 2
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(R.color.colorBlue)
                .background(R.color.colorBlueLight)
                .fragment(R.layout.onboarding_frag_two)
                .build());

        //Create slide 3
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(R.color.colorBlue)
                .background(R.color.colorBlueLight)
                .fragment(R.layout.onboarding_frag_three)
                .build());

        //Create slide 4
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(R.color.colorBlue)
                .background(R.color.colorBlueLight)
                .fragment(onBoardingFragPermissions)
                .build());

        //Create slide 5
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(R.color.colorBlue)
                .background(R.color.colorBlueLight)
                .fragment(R.layout.onboarding_frag_done)
                .build());

        //Create slide 6 to create intent to start main
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(R.color.colorBlue)
                .background(R.color.colorBlueLight)
                .fragment(OnboardingFragStartMain)
                .build());

        setSkipEnabled(false);

        setNavigationPolicy(new NavigationPolicy() {
            @Override
            public boolean canGoForward(int position) {
                switch(position){
                    case 0:
                        return true;
                    case 1:
                        return true;
                    case 2:
                        return true;
                    case 3:
                        return false;
                    case 4:
                        return true;
                }
                return true;
            }

            @Override
            public boolean canGoBackward(int position) {
                switch (position){
                    case 0:
                        return false;
                    case 1:
                        return true;
                    case 2:
                        return true;
                    case 3:
                        return true;
                }
                return false;
            }
        });
    }

    //Handle request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If the user didn't cancel the dialog
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //Permission granted
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.first_launch_key), "1");
                editor.apply();

                setNavigationPolicy(new NavigationPolicy() {
                    @Override
                    public boolean canGoForward(int i) {
                        return true;
                    }

                    @Override
                    public boolean canGoBackward(int i) {
                        return false;
                    }
                });
                nextSlide();
            }
        }
    }

    public static void requestPermissions(){
        ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);
    }
}
