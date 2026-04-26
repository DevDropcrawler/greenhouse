package nz.keeleysgreenhouse.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import javax.inject.Inject

data class MonthCrops(
    val month: Int,
    val seed: List<Crop>,
    val seedling: List<Crop>,
    val harvest: List<Crop>
)

class GetMonthCropsUseCase @Inject constructor(
    private val cropDao: CropDao
) {
    operator fun invoke(month: Int): Flow<MonthCrops> =
        cropDao.observeAll().map { crops ->
            MonthCrops(
                month = month,
                seed = crops.filter { month in it.seedSowMonths },
                seedling = crops.filter { month in it.seedlingPlantMonths },
                harvest = crops.filter { month in it.harvestMonths }
            )
        }
}
