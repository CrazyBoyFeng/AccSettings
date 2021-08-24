package com.github.crazyboyfeng.accSettings.data

import android.content.Context
import androidx.preference.PreferenceDataStore
import com.github.crazyboyfeng.accSettings.R

class AccDataStore(private val context: Context):PreferenceDataStore() {
    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        if (key==context.getString(R.string.daemon)){
            //todo if daemon
        }
        return super.getBoolean(key, defValue)
    }

    override fun putBoolean(key: String?, value: Boolean) {
        if (key==context.getString(R.string.daemon)){
            //todo if daemon
        }
        super.putBoolean(key, value)
    }
}