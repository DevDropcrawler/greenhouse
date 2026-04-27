package nz.keeleysgreenhouse.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import nz.keeleysgreenhouse.app.R

object NotificationChannels {
    const val TASKS = "task_reminders"

    fun ensure(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(TASKS) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    TASKS,
                    context.getString(R.string.notification_channel_tasks),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }
}
