package com.github.crazyboyfeng.accSettings.fragment

import android.os.Bundle
import com.github.crazyboyfeng.accSettings.R
import crazyboyfeng.android.preference.PreferenceFragmentCompat

class ConfigFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        preferenceManager.preferenceDataStore = ConfigDataStore()
        setPreferencesFromResource(R.xml.config_preferences, rootKey)
    }
}