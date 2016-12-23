package dean.weather;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Dean on 12/23/2016.
 */

public class settingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
