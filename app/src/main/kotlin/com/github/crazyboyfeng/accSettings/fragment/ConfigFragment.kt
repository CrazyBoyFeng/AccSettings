package com.github.crazyboyfeng.accSettings.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.github.crazyboyfeng.accSettings.R
import com.github.crazyboyfeng.accSettings.data.ConfigDataStore

class ConfigFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.config_preferences, rootKey)
        requireActivity().title=preferenceScreen.title//todo change on activity
        preferenceManager.preferenceDataStore = ConfigDataStore()
    }
}