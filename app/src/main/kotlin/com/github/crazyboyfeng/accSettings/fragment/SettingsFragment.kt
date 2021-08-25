package com.github.crazyboyfeng.accSettings.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.github.crazyboyfeng.accSettings.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }
}