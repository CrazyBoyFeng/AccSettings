package crazyboyfeng.accSettings.fragment

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import crazyboyfeng.accSettings.R
import crazyboyfeng.accSettings.acc.Command
import crazyboyfeng.accSettings.data.ConfigDataStore
import crazyboyfeng.android.preference.PreferenceFragmentCompat

@Suppress("unused")
class ConfigFragment : PreferenceFragmentCompat() {
    private lateinit var shutdownCapacity: NumberPickerPreference
    private lateinit var cooldownCapacity: NumberPickerPreference
    private lateinit var resumeCapacity: NumberPickerPreference
    private lateinit var pauseCapacity: NumberPickerPreference
    private lateinit var cooldownTemp: NumberPickerPreference
    private lateinit var maxTemp: NumberPickerPreference
    private lateinit var shutdownTemp: NumberPickerPreference
    private lateinit var cooldownCharge: EditTextPreferencePlus
    private lateinit var cooldownPause: EditTextPreferencePlus
    private lateinit var cooldownCustom: EditTextPreference
    private lateinit var maxChargingVoltage: EditTextPreferencePlus
    private lateinit var capacityFreeze2: SwitchPreference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = ConfigDataStore()
        setPreferencesFromResource(R.xml.config_preferences, rootKey)
        shutdownCapacity = findPreference(getString(R.string.set_shutdown_capacity))!!
        cooldownCapacity = findPreference(getString(R.string.set_cooldown_capacity))!!
        resumeCapacity = findPreference(getString(R.string.set_resume_capacity))!!
        pauseCapacity = findPreference(getString(R.string.set_pause_capacity))!!
        cooldownTemp = findPreference(getString(R.string.set_cooldown_temp))!!
        maxTemp = findPreference(getString(R.string.set_max_temp))!!
        shutdownTemp = findPreference(getString(R.string.set_shutdown_temp))!!
        cooldownCharge = findPreference(getString(R.string.set_cooldown_charge))!!
        cooldownPause = findPreference(getString(R.string.set_cooldown_pause))!!
        cooldownCustom = findPreference(getString(R.string.set_cooldown_custom))!!
        maxChargingVoltage = findPreference(getString(R.string.set_max_charging_voltage))!!
        capacityFreeze2 = findPreference(getString(R.string.set_capacity_freeze2))!!

        onShutdownCapacitySet()
        shutdownCapacity.setOnPreferenceChangeListener { _, newValue ->
            onShutdownCapacitySet(newValue as Int)
            true
        }
        onMiddleCapacitySet(cooldownCapacity.value)
        onMiddleCapacitySet(resumeCapacity.value)
        val onMiddleCapacityChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            onMiddleCapacitySet(newValue as Int)
            true
        }
        cooldownCapacity.onPreferenceChangeListener = onMiddleCapacityChangeListener
        resumeCapacity.onPreferenceChangeListener = onMiddleCapacityChangeListener
        onPauseCapacitySet()
        pauseCapacity.setOnPreferenceChangeListener { _, newValue ->
            onPauseCapacitySet(newValue as Int)
            true
        }

        onCooldownTempSet()
        cooldownTemp.setOnPreferenceChangeListener { _, newValue ->
            onCooldownTempSet(newValue as Int)
            true
        }
        onMaxTempSet()
        maxTemp.setOnPreferenceChangeListener { _, newValue ->
            onMaxTempSet(newValue as Int)
            true
        }
        onShutdownTempSet()
        shutdownTemp.setOnPreferenceChangeListener { _, newValue ->
            onShutdownTempSet(newValue as Int)
            true
        }

        onCooldownChargeSet()
        cooldownCharge.setOnPreferenceChangeListener { _, newValue ->
            onCooldownChargeSet(newValue as CharSequence)
            true
        }
        onCooldownPauseSet()
        cooldownPause.setOnPreferenceChangeListener { _, newValue ->
            onCooldownPauseSet(newValue as CharSequence)
            true
        }
        onCooldownCustomSet()
        cooldownCustom.setOnPreferenceChangeListener { _, newValue ->
            onCooldownCustomSet(newValue as CharSequence)
            true
        }

        maxChargingVoltage.setOnBindEditTextListener {
            it.doOnTextChanged { text, _, _, _ ->
                it.error =
                    if (text.isNullOrEmpty() || text.toString().toInt() in VOLT_MIN..VOLT_MAX) {
                        null
                    } else {
                        getString(R.string.hint_between, VOLT_MIN, VOLT_MAX)
                    }
                it.rootView.findViewById<Button>(android.R.id.button1)?.isEnabled =
                    it.error.isNullOrEmpty()
            }
        }

        loadDefault()
    }

    private fun onShutdownCapacitySet(value: Int = shutdownCapacity.value) {
        cooldownCapacity.minValue = value + 1
        resumeCapacity.maxValue = value + 1
        capacityFreeze2.isEnabled = value == 0
    }


    private fun onMiddleCapacitySet(value: Int) {
        if (value - 1 < shutdownCapacity.maxValue) {
            shutdownCapacity.maxValue = value - 1
        } else if (pauseCapacity.minValue < value + 1) {
            pauseCapacity.minValue = value + 1
        }
    }

    private fun onPauseCapacitySet(value: Int = pauseCapacity.value) {
        cooldownCapacity.maxValue = value - 1
        resumeCapacity.maxValue = value - 1
    }

    private fun onCooldownTempSet(value: Int = cooldownTemp.value) {
        maxTemp.minValue = value + 1
    }

    private fun onMaxTempSet(value: Int = maxTemp.value) {
        cooldownTemp.maxValue = value - 1
        shutdownTemp.minValue = value + 1
    }

    private fun onShutdownTempSet(value: Int = shutdownTemp.value) {
        maxTemp.maxValue = value - 1
    }

    private fun onCooldownChargeSet(value: CharSequence? = cooldownCharge.text) {
        val isValueEmpty = value.isNullOrEmpty()
        val isCooldownPauseEmpty = cooldownPause.text.isNullOrEmpty()
        cooldownCustom.isEnabled = isValueEmpty && isCooldownPauseEmpty
    }

    private fun onCooldownPauseSet(value: CharSequence? = cooldownPause.text) {
        val isCooldownChargeEmpty = cooldownCharge.text.isNullOrEmpty()
        val isValueEmpty = value.isNullOrEmpty()
        cooldownCustom.isEnabled = isCooldownChargeEmpty && isValueEmpty
    }

    private fun onCooldownCustomSet(value: CharSequence? = cooldownCustom.text) {
        val isValueEmpty = value.isNullOrEmpty()
        cooldownCharge.isEnabled = isValueEmpty
        cooldownPause.isEnabled = isValueEmpty
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
        const val VOLT_MIN = 3700
        const val VOLT_MAX = 4200
    }
}