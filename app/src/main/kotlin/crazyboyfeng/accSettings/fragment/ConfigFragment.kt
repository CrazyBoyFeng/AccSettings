package crazyboyfeng.accSettings.fragment

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.preference.NumberPickerPreference
import androidx.preference.Preference
import crazyboyfeng.accSettings.R
import crazyboyfeng.accSettings.acc.Command
import crazyboyfeng.accSettings.data.ConfigDataStore
import crazyboyfeng.android.preference.PreferenceFragmentCompat

class ConfigFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = ConfigDataStore()
        setPreferencesFromResource(R.xml.config_preferences, rootKey)
        loadDefault()
    }

    private fun loadDefault() = lifecycleScope.launchWhenCreated {
        val properties = Command.getDefaultConfig()
        Log.d(TAG, "loadDefault ${properties.size}")
        for (property in properties) {
            val value = property.value as String
            if (value.isEmpty()) {
                continue
            }// value not empty
            val key = property.key as String
            when (val preference = findPreference<Preference>(key)) {
                is NumberPickerPreference -> preference.setDefaultValue(value.toInt())
            }
        }
    }

    private companion object {
        const val TAG = "ConfigFragment"
    }
}