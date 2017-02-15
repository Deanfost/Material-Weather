package dean.weather;

import android.app.Activity;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.*;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

/**
 * Created by Dean on 12/23/2016.
 */

public class tutorialActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {
    //Similar to OnboardingActivity, but does not request permissions
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

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
                .fragment(R.layout.onboarding_frag_four)
                .build());

        //Create slide 5
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(R.color.colorBlue)
                .background(R.color.colorBlueLight)
                .fragment(R.layout.onboarding_frag_five)
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
                        return true;
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
                    case 4:
                        return true;
                }
                return false;
            }
        });
    }
}
