package com.alwaystinkering.sandbot.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.alwaystinkering.sandbot.R


/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)
    }
}