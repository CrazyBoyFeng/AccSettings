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
        reload()
        checkAcc()
    }

    private fun reload() {
        setPreferencesFromResource(R.xml.settings_preferences, null)
        accPreferenceCategory = findPreference(getString(R.string.acc))!!
    }

    private fun enableAcc() {
        preferenceManager.preferenceDataStore = AccDataStore(resources)
        reload()
        accPreferenceCategory.isEnabled = true
        val info = findPreference<PreferenceCategory>(getString(R.string.info_status))!!
        info.isVisible = true
        val infoTemp = findPreference<EditTextPreferencePlus>(getString(R.string.info_temp))!!
        infoTemp.setSummaryProvider {
            val preference = it as EditTextPreferencePlus
            val text = preference.text
            if (text.isNullOrEmpty()) text else (text.toFloat() / 10).toString()
        }
        updateInfo()
    }

    private fun checkAcc() = lifecycleScope.launchWhenCreated {
        accPreferenceCategory.summary = getString(R.string.initializing)
        val message = try {
            AccHandler().initial(requireContext())
            null
        } catch (e: Command.FailedException) {
            getString(R.string.command_failed)
        } catch (e: Command.AccException) {
            e.localizedMessage
        }//todo other exceptions?
        if (message != null) {//todo notice user to read log when failed
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
        enableAcc()
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
                val value = property.value as String
                if (value.isEmpty()) {
                    continue
                }//value not empty
                val key = property.key as String
                when (val preference = findPreference<Preference>(key)) {
                    is EditTextPreferencePlus -> preference.text = value
                    else -> preference?.summary = value
                }
            }
            delay(1000)
        }
    }

    private companion object {
        const val TAG = "SettingsFragment"
    }
}