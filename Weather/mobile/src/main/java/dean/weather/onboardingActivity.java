package dean.weather;


import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Created by DeanF on 10/29/2016.
 */

public class onboardingActivity  extends IntroActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder()
        .title("Material Weather")
        .description("Welcome to Material Weather, a clean, and simple to use weather app.")
        .image(R.drawable.ic_sunny_color)
        .background(R.color.colorBlue)
        .backgroundDark(R.color.colorBlueDark)
        .build());
    }
}
