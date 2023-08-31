package it.outset.t1_core.ui.settings;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceFragmentCompat;

import it.outset.t1_core.R;

public class SettingsRemoteFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.remote_preferences, rootKey);
        if (getActivity() != null)
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

/*
        SeekBarPreference displaySettingsPreference = findPreference("display_brightness");
        if (brightnessPreference != null) {
            brightnessPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    SettingsHelpers.getInstance().setScreenBrightness(getActivity().getWindow(), (int) newValue);
                    return false;
                }
            });
            brightnessPreference.setSummaryProvider((Preference.SummaryProvider<SeekBarPreference>) preference -> {
                int brightness = SettingsHelpers.getInstance().getScreenBrightness(getActivity().getWindow());
                return String.format("%d%%", brightness);
            });
        }
*/

    }
}