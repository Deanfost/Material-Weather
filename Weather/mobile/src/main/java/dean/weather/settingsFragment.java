package dean.weather;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Dean on 12/23/2016.
 */

public class settingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
