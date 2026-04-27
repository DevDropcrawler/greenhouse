package nz.keeleysgreenhouse.app.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import nz.keeleysgreenhouse.app.MainActivity
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.di.WorkerEntryPoint
import java.time.LocalDate

class TaskReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(context, WorkerEntryPoint::class.java)
        val taskDao = entryPoint.taskDao()

        val today = LocalDate.now().toString()
        val tasks = taskDao.observeForDate(today).first().filter { !it.done }
        if (tasks.isEmpty()) return Result.success()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return Result.success()
        }

        NotificationChannels.ensure(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pending = PendingIntent.getActivity(context, 0, intent, pendingFlags)

        val body = if (tasks.size == 1) {
            context.getString(R.string.notification_body_one)
        } else {
            context.getString(R.string.notification_body_many, tasks.size)
        }

        val notification = NotificationCompat.Builder(context, NotificationChannels.TASKS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(body)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(TASK_NOTIFICATION_ID, notification)
        return Result.success()
    }

    companion object {
        const val TASK_NOTIFICATION_ID = 1001
    }
}
