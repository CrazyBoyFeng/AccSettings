package com.github.crazyboyfeng.accSettings.data

import androidx.preference.PreferenceDataStore
import com.github.crazyboyfeng.accSettings.acc.Command

class ConfigDataStore: PreferenceDataStore() {
    override fun putString(key: String, value: String?) {
        Command.setConfig(key,value)
    }

    override fun getString(key: String, defValue: String?): String? {
        val config = Command.getConfig(key)
        val value= config.split('=', limit = 2)[1]
        return if(value.isEmpty()){
            defValue
        }else{
            value
        }
    }

    override fun putStringSet(key: String, values: MutableSet<String>?) {
        Command.setConfig(key, *values!!.toTypedArray())
    }

    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String> {
        return super.getStringSet(key, defValues)!!
        //TODO
    }

}