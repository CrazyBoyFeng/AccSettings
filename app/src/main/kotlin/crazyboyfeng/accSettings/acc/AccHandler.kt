package crazyboyfeng.accSettings.acc

import android.content.Context
import crazyboyfeng.accSettings.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AccHandler {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun install(context: Context) {
        suspend fun cacheAssetFile(fileName: String): File = withContext(Dispatchers.IO) {
            val cachedFile = File(context.cacheDir, fileName)
            context.assets.open(fileName).use { input ->
                FileOutputStream(cachedFile).use { output ->
                    input.copyTo(output)
                }
            }
            cachedFile
        }

        val resources = context.resources
        val accVersionCode = resources.getInteger(R.integer.acc_version_code)
        val accVersionName = resources.getString(R.string.acc_version_name)
        try {
            cacheAssetFile("acc_v${accVersionName}_${accVersionCode}.tgz")
            val installShFile = cacheAssetFile("install-tarball.sh")
            val command = "sh ${installShFile.absolutePath}"
            Command.exec(command)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(Command.AccException::class)
    suspend fun serve(context: Context) {
        if (Command.isAccInstalled()) {
            Command.exec("test -f /dev/.vr25/acc/acca || /data/adb/vr25/acc/service.sh")
        } else {
            install(context)
        }
    }

    @Throws(Command.AccException::class)
    suspend fun initial(context: Context) {
        val installedVersionCode = Command.getVersion(context).first
        val bundledVersionCode = context.resources.getInteger(R.integer.acc_version_code)
        if (bundledVersionCode <= installedVersionCode) {
            return
        }
        install(context)
    }
}