package crazyboyfeng.accSettings.data

import android.content.res.Resources
import android.util.Log
import androidx.preference.PreferenceDataStore
import crazyboyfeng.accSettings.R
import crazyboyfeng.accSettings.acc.Command
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AccDataStore(private val resources: Resources) : PreferenceDataStore() {
    override fun getBoolean(key: String, defValue: Boolean): Boolean = runBlocking {
        Log.v(TAG, "getBoolean: $key=$defValue?")
        when (key) {
            resources.getString(R.string.acc_daemon) -> Command.isDaemonRunning()
            else -> super.getBoolean(key, defValue)
        }
    }

    override fun putBoolean(key: String, value: Boolean) {
        Log.v(TAG, "putBoolean: $key=$value")
        GlobalScope.launch {
            when (key) {
                resources.getString(R.string.acc_daemon) -> Command.setDaemonRunning(value)
                else -> super.putBoolean(key, value)
            }
        }
    }

    private companion object {
        const val TAG = "AccDataStore"
    }
}