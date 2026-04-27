package nz.keeleysgreenhouse.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nz.keeleysgreenhouse.app.data.AppDatabase
import nz.keeleysgreenhouse.app.data.seed.DatabaseSeeder
import nz.keeleysgreenhouse.app.notifications.NotificationScheduler
import nz.keeleysgreenhouse.app.settings.SettingsStore
import javax.inject.Inject

data class SettingsUiState(
    val reminderHour: Int = SettingsStore.DEFAULT_HOUR,
    val reminderMinute: Int = SettingsStore.DEFAULT_MINUTE,
    val resetting: Boolean = false,
    val resetCompleted: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val db: AppDatabase,
    private val seeder: DatabaseSeeder
) : AndroidViewModel(application) {

    private val store = SettingsStore(application)

    private val _state = MutableStateFlow(
        store.reminderTime().let { (h, m) ->
            SettingsUiState(reminderHour = h, reminderMinute = m)
        }
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun setReminderTime(hour: Int, minute: Int) {
        NotificationScheduler.reschedule(getApplication(), hour, minute)
        _state.value = _state.value.copy(reminderHour = hour, reminderMinute = minute)
    }

    fun resetData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(resetting = true, resetCompleted = false)
            db.clearAllTables()
            seeder.seed()
            _state.value = _state.value.copy(resetting = false, resetCompleted = true)
        }
    }

    fun acknowledgeReset() {
        _state.value = _state.value.copy(resetCompleted = false)
    }
}
