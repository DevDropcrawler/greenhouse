package nz.keeleysgreenhouse.app.domain.usecase

import nz.keeleysgreenhouse.app.data.dao.TaskDao
import nz.keeleysgreenhouse.app.data.entity.Task
import javax.inject.Inject

class ToggleTaskDoneUseCase @Inject constructor(private val taskDao: TaskDao) {
    suspend operator fun invoke(task: Task) {
        taskDao.update(task.copy(done = !task.done))
    }
}
