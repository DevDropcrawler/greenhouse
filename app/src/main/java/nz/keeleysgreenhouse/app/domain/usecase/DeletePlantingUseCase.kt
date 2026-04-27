package nz.keeleysgreenhouse.app.domain.usecase

import nz.keeleysgreenhouse.app.data.dao.TaskDao
import nz.keeleysgreenhouse.app.data.dao.UserPlantingDao
import nz.keeleysgreenhouse.app.data.entity.UserPlanting
import javax.inject.Inject

class DeletePlantingUseCase @Inject constructor(
    private val plantingDao: UserPlantingDao,
    private val taskDao: TaskDao
) {
    suspend operator fun invoke(planting: UserPlanting) {
        taskDao.deleteForPlanting(planting.id)
        plantingDao.delete(planting)
    }
}
