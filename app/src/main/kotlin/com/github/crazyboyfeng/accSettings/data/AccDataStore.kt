package com.github.crazyboyfeng.accSettings.data

import android.content.res.Resources
import androidx.preference.PreferenceDataStore
import com.github.crazyboyfeng.accSettings.R
import com.github.crazyboyfeng.accSettings.acc.Command

class AccDataStore(private val resources: Resources) : PreferenceDataStore() {
    override fun getBoolean(key: String, defValue: Boolean): Boolean = when (key) {
        resources.getString(R.string.acc_daemon) -> Command.isDaemonRunning()
        else -> super.getBoolean(key, defValue)
    }

    override fun putBoolean(key: String, value: Boolean) = when (key) {
        resources.getString(R.string.acc_daemon) -> Command.setDaemonRunning(value)
        else -> super.putBoolean(key, value)
    }
}