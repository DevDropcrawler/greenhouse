package nz.keeleysgreenhouse.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.keeleysgreenhouse.app.data.dao.CropDao
import javax.inject.Inject

data class MonthSummary(
    val month: Int,
    val seedCount: Int,
    val seedlingCount: Int,
    val harvestCount: Int
)

class GetAllMonthsCropsUseCase @Inject constructor(
    private val cropDao: CropDao
) {
    operator fun invoke(): Flow<List<MonthSummary>> =
        cropDao.observeAll().map { crops ->
            (1..12).map { m ->
                MonthSummary(
                    month = m,
                    seedCount = crops.count { m in it.seedSowMonths },
                    seedlingCount = crops.count { m in it.seedlingPlantMonths },
                    harvestCount = crops.count { m in it.harvestMonths }
                )
            }
        }
}
