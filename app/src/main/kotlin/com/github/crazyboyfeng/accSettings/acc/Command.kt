package com.github.crazyboyfeng.accSettings.acc

import com.topjohnwu.superuser.Shell

object Command {
    private fun exec(vararg options: String): String {
        val command = buildString {
            append("/dev/.vr25/acc/acca")
            for (option in options) {
                append(" --")
                append(option)
            }
        }
        val result = Shell.su(command).exec()
        if (result.isSuccess) {
            return result.out.joinToString("\n").trim()
        } else throw when (result.code) {
            //TODO is there stdout or stderr?
            1 -> FailureException()
            2 -> IncorrectSyntaxException()
            3 -> MissingBusyboxBinaryException()
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

    fun set(property:String,vararg values:String)=exec(property,values.joinToString(" "))
    fun getVersion(): String = exec("version")
    open class AccException : Exception {
        constructor()
        constructor(message: String) : super(message)
    }

    class FailureException : AccException()
    class IncorrectSyntaxException : AccException()
    class MissingBusyboxBinaryException : AccException()
    class NotRootException : AccException()
    class DisableChargingFailedException : AccException()
    class DaemonExistsException : AccException()
    class DaemonNotExistsException : AccException()
    class TestFailedException : AccException()
    class ECurrentOutOfRangeException : AccException()
    class InitFailedException : AccException()
    class LockFailedException : AccException()
    class ModuleDisabledException : AccException()
}