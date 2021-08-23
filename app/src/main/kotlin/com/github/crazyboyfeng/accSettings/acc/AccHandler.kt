package com.github.crazyboyfeng.accSettings.acc

import android.content.Context
import com.github.crazyboyfeng.accSettings.R
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.Shell.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class AccHandler {
    fun initial(context:Context){
        val currentVersion=getVersionCode()
        val accVersionCode=context.resources.getInteger(R.integer.acc_version_code)
        //TODO check version
        // installed version > bundle -> return
    }
    private fun getVersionCode(): Int {
        val version=Command.getVersion()
            if(version.contains('(') && version.contains(')')){
                val versionCode= version.substringAfter('(').substringBefore(')')
                try {
                    return versionCode.toInt()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()//may be cause by upgraded acc
                }
            }
        return 0
    }
    private suspend fun install(context: Context): Boolean = withContext(Dispatchers.IO){
        suspend fun cacheAssertFile(fileName:String):File= withContext(Dispatchers.IO){
            val cachedFile = File(context.cacheDir, fileName)
            context.assets.open(fileName).use { input ->
                FileOutputStream(cachedFile).use { output ->
                    input.copyTo(output)
                }
            }
            cachedFile
        }
        val resources = context.resources
        val accVersionCode=resources.getInteger(R.integer.acc_version_code)
        val accVersionName=resources.getString(R.string.acc_version_name)
        try {
            cacheAssertFile("acc_v${accVersionName}_.${accVersionCode}.tar.gz")
            val installShFile=cacheAssertFile("install-tarball.sh")
            val command="sh ${installShFile.absolutePath} acc --non-interactive"
            //TODO using exit code? where to find log?
            Command.exec(command)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}