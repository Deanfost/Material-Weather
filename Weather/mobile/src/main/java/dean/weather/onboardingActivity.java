package dean.weather;


import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Created by DeanF on 10/29/2016.
 */

public class onboardingActivity  extends IntroActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create slide 1
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(R.color.colorBlue)
                .background(R.color.colorBlueLight)
                .fragment(R.layout.onboarding_frag_one)
                .build());
    }
}
