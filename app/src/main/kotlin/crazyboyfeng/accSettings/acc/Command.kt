package crazyboyfeng.accSettings.acc

import android.content.Context
import android.util.Log
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object Command {
    private const val TAG = "Command"

    open class AccException : Exception {
        constructor()
        constructor(message: String) : super(message)
    }

    class FailedException : AccException()
    class IncorrectSyntaxException : AccException()
    class NoBusyboxException : AccException()
    class NotRootException : AccException()
    class DisableChargingFailedException : AccException()
    class DaemonExistsException : AccException()
    class DaemonNotExistsException : AccException()
    class TestFailedException : AccException()
    class ECurrentOutOfRangeException : AccException()
    class InitFailedException : AccException()
    class LockFailedException : AccException()
    class ModuleDisabledException : AccException()

    suspend fun exec(command: String): String = withContext(Dispatchers.IO) {
        Log.v(TAG, command)
        val result = Shell.su(command).exec()
        if (result.isSuccess) {
            return@withContext result.out.joinToString("\n").trim()
        } else throw when (result.code) {
            1 -> FailedException()
            2 -> IncorrectSyntaxException()
            3 -> NoBusyboxException()
            4 -> NotRootException()
            7 -> DisableChargingFailedException()
            8 -> DaemonExistsException()
            9 -> DaemonNotExistsException()
            10 -> TestFailedException()
            11 -> ECurrentOutOfRangeException()
            12 -> InitFailedException()
            13 -> LockFailedException()
            14 -> ModuleDisabledException()
            else -> AccException("Exit code: ${result.code}")
        }
    }

    private suspend fun execAcc(vararg options: String): String {
        val command = buildString {
            append("/dev/.vr25/acc/acca")
            for (option in options) {
                append(" --")
                append(option)
            }
        }
        return exec(command)
    }

    suspend fun isAccInstalled(): Boolean = withContext(Dispatchers.IO) {
        Shell.sh("test -f /dev/.vr25/acc/acca").exec().isSuccess
    }

    suspend fun setConfig(property: String, vararg values: String?) =
        execAcc("set \"$property=${values.joinToString(" ")}\"")

    private fun getPropertyValue(property: String) = property.split('=', '\n')[1]

    suspend fun getConfig(property: String): String =
        getPropertyValue(execAcc("set", "print $property"))

    suspend fun getDefaultConfig(): Properties {
        val properties = Properties()
        @Suppress("BlockingMethodInNonBlockingContext")
        properties.load(execAcc("set", "print-default").reader())
        return properties
    }

    suspend fun getInfo(): Properties {
        val properties = Properties()
        @Suppress("BlockingMethodInNonBlockingContext")
        properties.load(execAcc("info").reader())
        return properties
    }

    // TODO Handle exceptions in better way. if dev/.vr25/ delete, app fails to handle it.
    suspend fun getVersion(context: Context): Pair<Int, String?> {
        if (isAccInstalled()) {
            val version = execAcc("version")
            if (version.startsWith('v')) {
                return try {
                    val versionCode = version.substringAfter('(').substringBefore(')').toInt()
                    val versionName = version.substringAfter('v').substringBefore(' ')
                    Pair(versionCode, versionName)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()//may be cause by upgraded acc
                    Pair(0, null)
                }
            }
        } else {
            AccHandler().install(context)
        }
        return Pair(0, null)
    }

    private suspend fun setDaemon(option: String) = try {
        execAcc("daemon $option")
    } catch (e: DaemonExistsException) {
        Log.i(TAG, "daemon exists")
    } catch (e: DaemonNotExistsException) {
        Log.i(TAG, "daemon not exists")
    }

    suspend fun setDaemonRunning(daemonRunning: Boolean) =
        setDaemon(if (daemonRunning) "start" else "stop")

    suspend fun isDaemonRunning(): Boolean = try {
        execAcc("daemon")
        true
    } catch (e: DaemonNotExistsException) {
        false
    }

    suspend fun restartDaemon() = setDaemon("restart")

    suspend fun reinitialize() = exec("/dev/.vr25/acc/accd --init")
}