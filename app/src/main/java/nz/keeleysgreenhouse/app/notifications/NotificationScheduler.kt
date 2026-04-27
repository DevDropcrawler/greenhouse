package nz.keeleysgreenhouse.app.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import nz.keeleysgreenhouse.app.settings.SettingsStore
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val WORK_NAME = "daily_task_reminder"

    fun scheduleDaily(context: Context) {
        val (hour, minute) = SettingsStore(context).reminderTime()
        scheduleAt(context, hour, minute, replace = false)
    }

    fun reschedule(context: Context, hour: Int, minute: Int) {
        SettingsStore(context).setReminderTime(hour, minute)
        scheduleAt(context, hour, minute, replace = true)
    }

    private fun scheduleAt(context: Context, hour: Int, minute: Int, replace: Boolean) {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(LocalTime.of(hour, minute))
        if (!next.isAfter(now)) next = next.plusDays(1)
        val initialDelayMs = Duration.between(now, next).toMillis()

        val request = PeriodicWorkRequestBuilder<TaskReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .build()

        val policy = if (replace) ExistingPeriodicWorkPolicy.UPDATE else ExistingPeriodicWorkPolicy.KEEP

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, policy, request)
    }
}
