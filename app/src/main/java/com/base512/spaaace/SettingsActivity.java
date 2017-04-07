package com.base512.spaaace;

import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        PreferenceManager pm = getPreferenceManager();
        Preference setAsWallpaper = (Preference) pm.findPreference("set_as_wallpaper");
        setAsWallpaper.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference pref) {
                Intent i = new Intent();
                try {
                    if (Build.VERSION.SDK_INT > 15) {
                        i.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);

                        String p = SpaaaceWallpaperService.class.getPackage().getName();
                        String c = SpaaaceWallpaperService.class.getCanonicalName();
                        i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(p, c));
                    } else {
                        i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    }
                } catch (ActivityNotFoundException e) {
                    // Fallback to the old method, some devices greater than SDK 15 are crashing
                    i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                }
                startActivity(i);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_reset_to_defaults) {
            resetToDefaults();
            //refreshPreferences();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /** Sets the preferences to their default values without updating the GUI */
    private void resetToDefaults() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        PreferenceManager.setDefaultValues(this, R.xml.prefs, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        //refreshPreferences();
    }

    @Override
    public void onStop() {
        super.onStop();
        SpaaaceWallpaperService.reset();
    }
}
