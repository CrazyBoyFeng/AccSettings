package crazyboyfeng.accSettings.data

import android.util.Log
import androidx.preference.PreferenceDataStore
import crazyboyfeng.accSettings.acc.Command
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ConfigDataStore : PreferenceDataStore() {
    override fun putBoolean(key: String, value: Boolean) {
        Log.v(TAG, "putBoolean: $key=$value")
        GlobalScope.launch {
            Command.setConfig(key, value.toString())
        }
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        Log.v(TAG, "getBoolean: $key=$defValue?")
        return runBlocking {
            val value = Command.getConfig(key)
            if (value.isEmpty()) {
                defValue
            } else {
                value.toBoolean()
            }
        }
    }

    override fun putInt(key: String, value: Int) {
        Log.v(TAG, "putInt: $key=$value")
        GlobalScope.launch {
            Command.setConfig(key, value.toString())
        }
    }

    override fun getInt(key: String, defValue: Int): Int {
        Log.v(TAG, "getInt: $key=$defValue?")
        return runBlocking {
            val value = Command.getConfig(key)
            if (value.isEmpty()) {
                defValue
            } else {
                value.toInt()
            }
        }
    }

    override fun putString(key: String, value: String?) {
        Log.v(TAG, "putString: $key=$value")
        GlobalScope.launch {
            Command.setConfig(key, value)
        }
    }

    override fun getString(key: String, defValue: String?): String? {
        Log.v(TAG, "getString: $key=$defValue?")
        return runBlocking {
            val value = Command.getConfig(key)
            if (value.isEmpty()) {
                defValue
            } else {
                value
            }
        }
    }

    private companion object {
        const val TAG = "ConfigDataStore"
    }
}