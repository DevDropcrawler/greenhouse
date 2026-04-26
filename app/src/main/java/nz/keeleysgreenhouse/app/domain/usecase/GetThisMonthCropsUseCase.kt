package nz.keeleysgreenhouse.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import java.time.LocalDate
import javax.inject.Inject

data class ThisMonthCrops(
    val month: Int,
    val sow: List<Crop>,
    val transplant: List<Crop>,
    val harvest: List<Crop>
)

class GetThisMonthCropsUseCase @Inject constructor(
    private val cropDao: CropDao,
    private val today: () -> LocalDate = LocalDate::now
) {
    operator fun invoke(): Flow<ThisMonthCrops> {
        val month = today().monthValue
        return cropDao.observeAll().map { crops ->
            ThisMonthCrops(
                month = month,
                sow = crops.filter { month in it.seedSowMonths },
                transplant = crops.filter { month in it.seedlingPlantMonths },
                harvest = crops.filter { month in it.harvestMonths }
            )
        }
    }
}
