package crazyboyfeng.accSettings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.topjohnwu.superuser.Shell
import crazyboyfeng.accSettings.acc.AccHandler
import crazyboyfeng.accSettings.acc.Command
import kotlinx.coroutines.runBlocking

class WorkerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent!!.action) {
            Intent.ACTION_MY_PACKAGE_REPLACED -> run(context, InitialWorker::class.java)
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_LOCKED_BOOT_COMPLETED -> run(
                context,
                ServeWorker::class.java
            )
        }
    }

    private fun run(context: Context, worker: Class<out Worker>) {
        Shell.rootAccess()
        val request = OneTimeWorkRequest.Builder(worker).build()
        WorkManager.getInstance(context).enqueue(request)
    }

    class InitialWorker(private val context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {
        override fun doWork(): Result = runBlocking {
            try {
                AccHandler().initial(context)
                Result.success()
            } catch (e: Command.AccException) {
                Result.failure()
            }
        }
    }

    class ServeWorker(private val context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {
        override fun doWork(): Result = runBlocking {
            try {
                AccHandler().serve(context)
                Result.success()
            } catch (e: Command.AccException) {
                Result.failure()
            }
        }
    }
}