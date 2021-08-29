package crazyboyfeng.accSettings.acc

import android.util.Log
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.Shell.Result.JOB_NOT_EXECUTED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object Command {
    private const val TAG = "Command"

    open class AccException : Exception {
        constructor()
        constructor(message: String) : super(message)
    }

    class FailureException : AccException()
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
        //todo what dispatcher should be use
        Log.v(TAG, command)
        val result = Shell.su(command).exec()
        if (result.isSuccess) {
            return@withContext result.out.joinToString("\n").trim()
        } else throw when (result.code) {
            1 -> FailureException()
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
            else -> AccException("${result.code}")
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

    suspend fun setConfig(property: String, vararg values: String?) =
        execAcc("set \"$property=${values.joinToString(" ")}\"")

    private fun getPropertyValue(property: String) = property.split('=', limit = 2)[1]

    suspend fun getConfig(property: String): String =
        getPropertyValue(execAcc("set", "print $property"))

    suspend fun getDefaultConfig(property: String): String =
        getPropertyValue(execAcc("set", "print-default $property"))

    suspend fun getInfo(): Properties = withContext(Dispatchers.IO) {
        val properties = Properties()
        properties.load(execAcc("info").reader())
        return@withContext properties
    }

    suspend fun getVersion(): Pair<Int, String?> {
        val version = execAcc("version")
        if (version.startsWith('v')) {
            try {
                val versionCode = version.substringAfter('(').substringBefore(')').toInt()
                val versionName = version.substringAfter('v').substringBefore(' ')
                return Pair(versionCode, versionName)
            } catch (e: NumberFormatException) {
                e.printStackTrace()//may be cause by upgraded acc
            }
        }
        return Pair(0, null)
    }

    suspend fun setDaemonRunning(daemonRunning: Boolean) {
        if (daemonRunning) {
            try {
                execAcc("daemon start")
            } catch (e: DaemonExistsException) {
                Log.i(TAG, e.localizedMessage!!)
            }
        } else {
            try {
                execAcc("daemon stop")
            } catch (e: DaemonNotExistsException) {
                Log.i(TAG, e.localizedMessage!!)
            }
        }
    }

    suspend fun isDaemonRunning(): Boolean {
        return try {
            execAcc("daemon")
            true
        } catch (e: DaemonNotExistsException) {
            false
        }
    }
    fun isRoot():Boolean =Shell.su().exec().code==JOB_NOT_EXECUTED
}