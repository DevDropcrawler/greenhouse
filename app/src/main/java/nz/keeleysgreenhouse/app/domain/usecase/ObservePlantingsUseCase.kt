package nz.keeleysgreenhouse.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.UserPlantingDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.UserPlanting
import javax.inject.Inject

data class PlantingWithCrop(val planting: UserPlanting, val crop: Crop)

class ObservePlantingsUseCase @Inject constructor(
    private val plantingDao: UserPlantingDao,
    private val cropDao: CropDao
) {
    operator fun invoke(): Flow<List<PlantingWithCrop>> =
        combine(plantingDao.observeAll(), cropDao.observeAll()) { plantings, crops ->
            val byId = crops.associateBy { it.id }
            plantings.mapNotNull { p -> byId[p.cropId]?.let { PlantingWithCrop(p, it) } }
        }
}
