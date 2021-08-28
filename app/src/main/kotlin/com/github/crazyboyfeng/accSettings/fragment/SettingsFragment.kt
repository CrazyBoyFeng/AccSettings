package com.github.crazyboyfeng.accSettings.fragment

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreferencePlus
import com.github.crazyboyfeng.accSettings.R
import com.github.crazyboyfeng.accSettings.acc.Command
import com.github.crazyboyfeng.accSettings.data.AccDataStore
import com.topjohnwu.superuser.Shell
import crazyboyfeng.android.preference.PreferenceFragmentCompat
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
                val preference = findPreference<EditTextPreferencePlus>(key)
                preference?.text=property.value as String
            }
            delay(1000)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = AccDataStore(resources)
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        val infoTemp=findPreference<EditTextPreferencePlus>(getString(R.string.info_temp))
        infoTemp?.setSummaryProvider {
            val preference = it as EditTextPreferencePlus
            (preference.text.toFloat()/10).toString()
        }
    }

    private companion object {
        const val TAG = "SettingsFragment"
    }
}