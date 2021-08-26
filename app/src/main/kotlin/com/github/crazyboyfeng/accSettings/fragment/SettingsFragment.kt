package com.github.crazyboyfeng.accSettings.fragment

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.crazyboyfeng.accSettings.R
import com.github.crazyboyfeng.accSettings.acc.Command
import com.github.crazyboyfeng.accSettings.data.AccDataStore
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class SettingsFragment : PreferenceFragmentCompat() {
    init {
        Shell.sh().exec()
        updateInfo()
    }

    private fun updateInfo() = lifecycleScope.launchWhenStarted {
        while (isActive) {
            val properties = Command.getInfo()
            Log.d(TAG, "updateInfo ${properties.size}")
            for (property in properties) {
                val key = property.key as String
                val preference = findPreference<Preference>(key)
                if (preference != null) {
                    var summary = property.value as String
                    if (key == getString(R.string.info_temp)) {
                        summary = (summary.toFloat() / 10).toString()
                    }
                    preference.summary = summary
                }
            }
            delay(1000)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = AccDataStore(resources)
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    private companion object {
        const val TAG = "SettingsFragment"
    }
}