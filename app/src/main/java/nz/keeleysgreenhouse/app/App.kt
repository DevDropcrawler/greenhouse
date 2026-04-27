package nz.keeleysgreenhouse.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import nz.keeleysgreenhouse.app.data.seed.DatabaseSeeder
import nz.keeleysgreenhouse.app.notifications.NotificationChannels
import nz.keeleysgreenhouse.app.notifications.NotificationScheduler
import javax.inject.Inject

@HiltAndroidApp
class GreenhouseApp : Application() {

    @Inject lateinit var seeder: DatabaseSeeder

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch { seeder.seed() }
        runCatching {
            NotificationChannels.ensure(this)
            NotificationScheduler.scheduleDaily(this)
        }
    }
}
