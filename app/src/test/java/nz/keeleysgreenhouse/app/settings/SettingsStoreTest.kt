package nz.keeleysgreenhouse.app.settings

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsStoreTest {

    @Test
    fun defaultIsEightAm() {
        val store = SettingsStore(ApplicationProvider.getApplicationContext())
        val (h, m) = store.reminderTime()
        assertEquals(SettingsStore.DEFAULT_HOUR, h)
        assertEquals(SettingsStore.DEFAULT_MINUTE, m)
    }

    @Test
    fun setRoundTrips() {
        val store = SettingsStore(ApplicationProvider.getApplicationContext())
        store.setReminderTime(9, 30)
        val (h, m) = store.reminderTime()
        assertEquals(9, h)
        assertEquals(30, m)
    }
}
