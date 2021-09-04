package crazyboyfeng.accSettings.fragment

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.preference.NumberPickerPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import crazyboyfeng.accSettings.R
import crazyboyfeng.accSettings.acc.Command
import crazyboyfeng.accSettings.data.ConfigDataStore
import crazyboyfeng.android.preference.PreferenceFragmentCompat

class ConfigFragment : PreferenceFragmentCompat() {
    private lateinit var shutdownBelow: NumberPickerPreference
    private lateinit var cooldownAbove: NumberPickerPreference
    private lateinit var chargeBelow: NumberPickerPreference
    private lateinit var pauseAbove: NumberPickerPreference
    private lateinit var preventShutdown: SwitchPreference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = ConfigDataStore()
        setPreferencesFromResource(R.xml.config_preferences, rootKey)
        shutdownBelow = findPreference(getString(R.string.set_shutdown_capacity))!!
        cooldownAbove = findPreference(getString(R.string.set_cooldown_capacity))!!
        chargeBelow = findPreference(getString(R.string.set_resume_capacity))!!
        pauseAbove = findPreference(getString(R.string.set_pause_capacity))!!
        preventShutdown = findPreference(getString(R.string.set_capacity_freeze2))!!
        onShutdownBelowSet()
        onCooldownAboveSet()
        onChargeBelowSet()
        onPauseAboveSet()
        shutdownBelow.setOnPreferenceChangeListener { _, newValue ->
            onShutdownBelowSet(newValue as Int)
            true
        }
        cooldownAbove.setOnPreferenceChangeListener { _, newValue ->
            onCooldownAboveSet(newValue as Int)
            true
        }
        chargeBelow.setOnPreferenceChangeListener { _, newValue ->
            onChargeBelowSet(newValue as Int)
            true
        }
        pauseAbove.setOnPreferenceChangeListener { _, newValue ->
            onPauseAboveSet(newValue as Int)
            true
        }
        loadDefault()
    }

    private fun onShutdownBelowSet(value: Int = shutdownBelow.value) {
        cooldownAbove.minValue = value + 1
        preventShutdown.isEnabled = value == 0
    }

    private fun onCooldownAboveSet(value: Int = cooldownAbove.value) {
        shutdownBelow.maxValue = value - 1
        chargeBelow.minValue = value + 1
    }

    private fun onChargeBelowSet(value: Int = chargeBelow.value) {
        cooldownAbove.maxValue = value - 1
        pauseAbove.minValue = value + 1
    }

    private fun onPauseAboveSet(value: Int = pauseAbove.value) {
        chargeBelow.maxValue = value - 1
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