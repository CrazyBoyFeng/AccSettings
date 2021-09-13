package crazyboyfeng.accSettings.data

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceDataStore
import crazyboyfeng.accSettings.R
import crazyboyfeng.accSettings.acc.Command
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ConfigDataStore(private val context: Context) : PreferenceDataStore() {
    private var capacityVoltage: Boolean = false
    override fun putBoolean(key: String, value: Boolean) {
        Log.v(TAG, "putBoolean: $key=$value")
        GlobalScope.launch {
            when (key) {
                context.getString(R.string.capacity_voltage) -> capacityVoltage = value
                else -> Command.setConfig(key, value.toString())
            }
            onConfigChangeListener?.onConfigChanged(key)
        }
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        Log.v(TAG, "getBoolean: $key=$defValue?")
        return runBlocking {
            when (key) {
                context.getString(R.string.capacity_voltage) -> capacityVoltage
                else -> {
                    val value = Command.getConfig(key)
                    if (value.isEmpty()) {
                        defValue
                    } else {
                        value.toBoolean()
                    }
                }
            }
        }
    }

    override fun putInt(key: String, value: Int) {
        Log.v(TAG, "putInt: $key=$value")
        GlobalScope.launch {
            Command.setConfig(key, value.toString())
            onConfigChangeListener?.onConfigChanged(key)
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
            onConfigChangeListener?.onConfigChanged(key)
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

    fun interface OnConfigChangeListener {
        fun onConfigChanged(key: String)
    }

    var onConfigChangeListener: OnConfigChangeListener? = null

    private companion object {
        const val TAG = "ConfigDataStore"
    }
}