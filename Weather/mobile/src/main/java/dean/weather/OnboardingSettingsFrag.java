package dean.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

/**
 * Created by Dean Foster on 3/5/2017.
 */

public class OnboardingSettingsFrag extends Fragment {
    Button btnFixSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.onboarding_frag_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnFixSettings = (Button) getView().findViewById(R.id.btnOnboardingSettings);

        btnFixSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnboardingActivity) getActivity()).startSettingsResolution();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getContext());
    }
}
