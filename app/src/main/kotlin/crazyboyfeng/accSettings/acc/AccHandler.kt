package crazyboyfeng.accSettings.acc

import android.content.Context
import crazyboyfeng.accSettings.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class AccHandler {
    private suspend fun install(context: Context) {
        suspend fun cacheAssertFile(fileName: String): File = withContext(Dispatchers.IO) {
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
            cacheAssertFile("acc_v${accVersionName}_.${accVersionCode}.tar.gz")
            val installShFile = cacheAssertFile("install-tarball.sh")
            val command = "sh ${installShFile.absolutePath} acc --non-interactive"
            Command.exec(command)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(Command.AccException::class)
    suspend fun serve() {
        Command.exec("test -f /dev/.vr25/acc/acca || /data/adb/vr25/acc/service.sh")
    }

    @Throws(Command.AccException::class)
    suspend fun initial(context: Context) {
        val installedVersionCode = Command.getVersion().first
        val bundledVersionCode = context.resources.getInteger(R.integer.acc_version_code)
        if (bundledVersionCode <= installedVersionCode) {
            return
        }
        install(context)
    }
}