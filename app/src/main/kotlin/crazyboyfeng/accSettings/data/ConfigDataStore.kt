package crazyboyfeng.accSettings.data

import androidx.preference.PreferenceDataStore
import crazyboyfeng.accSettings.acc.Command
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ConfigDataStore : PreferenceDataStore() {
    override fun putString(key: String, value: String?) {
        GlobalScope.launch {
            Command.setConfig(key, value)
        }
    }

    override fun getString(key: String, defValue: String?): String? = runBlocking {
        val value = Command.getConfig(key)
        if (value.isEmpty()) {
            defValue
        } else {
            value
        }
    }

    override fun putStringSet(key: String, values: MutableSet<String>?) {
        GlobalScope.launch {
            Command.setConfig(key, *values!!.toTypedArray())
        }
    }

    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String> {
        return super.getStringSet(key, defValues)!!
        //TODO
    }

}