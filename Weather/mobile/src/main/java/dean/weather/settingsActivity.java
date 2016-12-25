package dean.weather;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Dean on 12/23/2016.
 */

public class settingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Setup preference change listener
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            //The follow notification pref was changed
            case "key_notif_follow":
                Log.i("preferenceActivity", "follow_notif_key changed");
                if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_notif_follow", false)){
                    //Setup an alarm to schedule forecast pull tasks
                    AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
                    Intent serviceIntent = new Intent(this, notificationService.class);
                    PendingIntent alarmIntent = PendingIntent.getService(this, 0, serviceIntent, 0);

                    //Setup an alarm to fire in one hour, and then every hour after that
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR,
                            AlarmManager.INTERVAL_HOUR, alarmIntent);

                    //Start the first pull
                    this.startService(serviceIntent);
                }
                else{
                    //Cancel the alarms
                    Log.i("preferenceActivity", "Cancelling repeat notif");
                    AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
                    Intent serviceIntent = new Intent(this, notificationService.class);
                    PendingIntent alarmIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
                    alarmManager.cancel(alarmIntent);

                    //Clear the notif
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(MainActivity.NOTIF_ID);

                }
                break;
            //The weather alert notif pref was changed
            case "key_notif_alert":
                Log.i("preferenceActivity", "alert_notif_key changed");
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;
            //The weather summary notif pref was changed
            case "key_notif_summary":
                Log.i("preferenceActivity", "summary_notif_key changed");
                //TODO - CREATE CUSTOM PREF(TIME SELECTOR), AND SETUP ALARM TO TRIGGER IT
                if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_notif_summary", false)) {
                    Intent summaryIntent = new Intent(this, summaryService.class);
                    startService(summaryIntent);
                }
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        // If the user has clicked on a preference screen, set up the screen
        if (preference instanceof PreferenceScreen) {
            setUpNestedScreen((PreferenceScreen) preference);
        }

        return false;
    }

    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();

        Toolbar bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        bar.setTitle(preferenceScreen.getTitle());

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}