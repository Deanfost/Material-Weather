package dean.weather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by DeanF on 10/30/2016.
 */

public class OnboardingFragThree extends Fragment {
    Button btnRequestPermissions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.onboarding_frag_permissions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnRequestPermissions = (Button) getView().findViewById(R.id.btnOnboardingPermission);

        btnRequestPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnboardingActivity.requestPermissions();
            }
        });
    }
}
