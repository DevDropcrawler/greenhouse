package nz.keeleysgreenhouse.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import nz.keeleysgreenhouse.app.data.dao.TaskDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.Task
import nz.keeleysgreenhouse.app.domain.usecase.GetThisMonthCropsUseCase
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val month: Int = LocalDate.now().monthValue,
    val today: LocalDate = LocalDate.now(),
    val sow: List<Crop> = emptyList(),
    val transplant: List<Crop> = emptyList(),
    val harvest: List<Crop> = emptyList(),
    val todayTasks: List<Task> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getThisMonth: GetThisMonthCropsUseCase,
    taskDao: TaskDao
) : ViewModel() {

    val state: StateFlow<HomeUiState> = combine(
        getThisMonth(),
        taskDao.observeForDate(LocalDate.now().toString())
    ) { month, tasks ->
        HomeUiState(
            month = month.month,
            today = LocalDate.now(),
            sow = month.sow,
            transplant = month.transplant,
            harvest = month.harvest,
            todayTasks = tasks
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
