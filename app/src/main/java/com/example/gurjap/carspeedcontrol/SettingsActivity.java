package com.example.gurjap.carspeedcontrol;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;


public final class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_activity);

        final ActionBar bar = getActionBar();
        assert bar != null;
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        setPrefenceTitle(getString(R.string.pref_commands_mode));
        setPrefenceTitle(getString(R.string.pref_checksum_mode));
        setPrefenceTitle(getString(R.string.pref_commands_ending));
        setPrefenceTitle(getString(R.string.pref_answer_ending));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
  @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String value) {
        setPrefenceTitle(value);
    }
 private void setPrefenceTitle(String TAG) {
        final Preference preference = findPreference(TAG);
        if (preference == null) return;
        if (preference instanceof ListPreference) {
            if (((ListPreference) preference).getEntry() == null) return;
            final String title = ((ListPreference) preference).getEntry().toString();
            preference.setTitle(title);
        }
    }

}
