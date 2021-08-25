package com.github.crazyboyfeng.accSettings.acc

import com.topjohnwu.superuser.Shell

object Command {
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

    fun exec(command: String): String {
        val result = Shell.su(command).exec()
        if (result.isSuccess) {
            return result.out.joinToString("\n").trim()
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
            else -> AccException("exit code: ${result.code}")
        }
    }

    private fun execAcc(vararg options: String): String {
        val command = buildString {
            append("/dev/.vr25/acc/acca")
            for (option in options) {
                append(" --")
                append(option)
            }
        }
        return exec(command)
    }

    fun setConfig(property: String, vararg values: String?) =
        execAcc("set \"$property=${values.joinToString(" ")}\"")

    private fun getConfigValue(config: String) = config.split('=', limit = 2)[1]

    fun getConfig(property: String): String = getConfigValue(execAcc("set", "print $property"))

    fun getDefaultConfig(property: String): String =
        getConfigValue(execAcc("set", "print-default $property"))

    fun getVersion(): Pair<Int, String?> {
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

    fun isDaemonRunning(): Boolean = try {
        execAcc("daemon")
        true
    } catch (e: DaemonNotExistsException) {
        false
    }
}