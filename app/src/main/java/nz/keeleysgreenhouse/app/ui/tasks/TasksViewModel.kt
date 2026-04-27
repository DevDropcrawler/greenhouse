package nz.keeleysgreenhouse.app.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nz.keeleysgreenhouse.app.data.dao.TaskDao
import nz.keeleysgreenhouse.app.data.entity.Task
import nz.keeleysgreenhouse.app.domain.usecase.ToggleTaskDoneUseCase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

enum class TaskGroup { TODAY, TOMORROW, THIS_WEEK, LATER }

data class TaskGroups(val groups: Map<TaskGroup, List<Task>>) {
    val today: List<Task> get() = groups[TaskGroup.TODAY].orEmpty()
}

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val toggleTaskDone: ToggleTaskDoneUseCase
) : ViewModel() {

    val state: StateFlow<TaskGroups> = taskDao.observeAll()
        .map { tasks -> TaskGroups(group(tasks, LocalDate.now())) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TaskGroups(emptyMap()))

    fun toggle(task: Task) {
        viewModelScope.launch { toggleTaskDone(task) }
    }

    companion object {
        fun group(tasks: List<Task>, today: LocalDate): Map<TaskGroup, List<Task>> {
            val tomorrow = today.plusDays(1)
            val weekEnd = today.plusDays(7)
            return tasks
                .groupBy { task ->
                    val due = LocalDate.parse(task.dueDate)
                    when {
                        !due.isAfter(today) -> TaskGroup.TODAY
                        due == tomorrow -> TaskGroup.TOMORROW
                        ChronoUnit.DAYS.between(today, due) <= 7 -> TaskGroup.THIS_WEEK
                        else -> TaskGroup.LATER
                    }
                }
                .mapValues { (_, list) -> list.sortedBy { it.dueDate } }
        }
    }
}
