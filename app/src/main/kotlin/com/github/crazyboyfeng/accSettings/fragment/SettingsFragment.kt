package com.github.crazyboyfeng.accSettings.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.github.crazyboyfeng.accSettings.R
import com.github.crazyboyfeng.accSettings.data.AccDataStore

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        preferenceManager.preferenceDataStore = AccDataStore(requireContext())
    }
}