package com.alwaystinkering.sandbot.ui.settings;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.alwaystinkering.sandbot.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey);
    }

}
