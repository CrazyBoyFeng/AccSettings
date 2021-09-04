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
        val shutdownBelow =
            findPreference<NumberPickerPreference>(getString(R.string.set_shutdown_capacity))!!
        val cooldownAbove =
            findPreference<NumberPickerPreference>(getString(R.string.set_cooldown_capacity))!!
        val chargeBelow =
            findPreference<NumberPickerPreference>(getString(R.string.set_resume_capacity))!!
        val pauseAbove =
            findPreference<NumberPickerPreference>(getString(R.string.set_pause_capacity))!!
        shutdownBelow.setOnPreferenceChangeListener { _, newValue ->
            val shutdown = newValue as Int
            cooldownAbove.minValue = shutdown + 1
            true
        }
        cooldownAbove.setOnPreferenceChangeListener { _, newValue ->
            val cooldown = newValue as Int
            shutdownBelow.maxValue = cooldown - 1
            chargeBelow.minValue = cooldown + 1
            true
        }
        chargeBelow.setOnPreferenceChangeListener { _, newValue ->
            val charge = newValue as Int
            cooldownAbove.maxValue = charge - 1
            pauseAbove.minValue = charge + 1
            true
        }
        pauseAbove.setOnPreferenceChangeListener { _, newValue ->
            val pause = newValue as Int
            chargeBelow.maxValue = pause - 1
            true
        }
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