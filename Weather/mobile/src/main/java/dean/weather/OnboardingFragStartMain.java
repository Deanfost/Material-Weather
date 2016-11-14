package dean.weather;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by DeanF on 11/13/2016.
 */

public class OnboardingFragStartMain extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startMain = new Intent(getActivity(), MainActivity.class);
        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
