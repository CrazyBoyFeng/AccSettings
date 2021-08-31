package crazyboyfeng.accSettings.fragment

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreferencePlus
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import crazyboyfeng.accSettings.R
import crazyboyfeng.accSettings.acc.AccHandler
import crazyboyfeng.accSettings.acc.Command
import crazyboyfeng.accSettings.data.AccDataStore
import crazyboyfeng.android.preference.PreferenceFragmentCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var accPreferenceCategory: PreferenceCategory
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        refresh(false)
        checkAcc()
    }

    private fun refresh(checked: Boolean) {
        if (checked) preferenceManager.preferenceDataStore = AccDataStore(resources)
        setPreferencesFromResource(R.xml.settings_preferences, null)
        accPreferenceCategory = findPreference(getString(R.string.acc))!!
        if (!checked) {
            return
        }
        accPreferenceCategory.isEnabled = true
        val infoPreferenceCategory =
            findPreference<PreferenceCategory>(getString(R.string.info_status))
        infoPreferenceCategory?.isVisible = true
        val infoTemp = findPreference<EditTextPreferencePlus>(getString(R.string.info_temp))
        infoTemp?.setSummaryProvider {
            val preference = it as EditTextPreferencePlus
            val text = preference.text
            if (text.isNullOrEmpty()) text else (text.toFloat() / 10).toString()
        }
        updateInfo()
    }

    private fun checkAcc() = lifecycleScope.launchWhenCreated {
        accPreferenceCategory.summary = getString(R.string.updating)
        val message = try {
            AccHandler().update(requireContext())
            null
        } catch (e: Command.FailedException) {
            getString(R.string.command_failed)
        } catch (e: Command.AccException) {
            e.localizedMessage
        }//todo other exceptions?
        if (message != null) {
            accPreferenceCategory.summary = message
            return@launchWhenCreated
        }//updated
        val versions = Command.getVersion()
        val bundledVersionCode = resources.getInteger(R.integer.acc_version_code)
        if (versions.first < bundledVersionCode) {
            accPreferenceCategory.summary =
                getString(R.string.installed_incompatible, versions.second)
            return@launchWhenCreated
        }
        refresh(true)
        if (versions.first > bundledVersionCode) {
            accPreferenceCategory.summary =
                getString(R.string.installed_possibly_incompatible, versions.second)
        }
    }

    private fun updateInfo() = lifecycleScope.launchWhenStarted {
        while (isActive) {
            val properties = Command.getInfo()
            Log.d(TAG, "updateInfo ${properties.size}")
            for (property in properties) {
                val key = property.key as String
                when (val preference = findPreference<Preference>(key)) {
                    is EditTextPreferencePlus -> preference.text = property.value as String
                    else -> preference?.summary = property.value as String
                }
            }
            delay(1000)
        }
    }

    private companion object {
        const val TAG = "SettingsFragment"
    }
}