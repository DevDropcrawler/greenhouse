package nz.keeleysgreenhouse.app.domain.usecase

import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.PestDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.Pest
import javax.inject.Inject

data class PestDetail(
    val pest: Pest,
    val affectedCrops: List<Crop>
)

class GetPestDetailUseCase @Inject constructor(
    private val pestDao: PestDao,
    private val cropDao: CropDao
) {
    suspend operator fun invoke(pestId: Int): PestDetail? {
        val pest = pestDao.getById(pestId) ?: return null
        val crops = pest.affectedCrops.mapNotNull { cropDao.getById(it) }
        return PestDetail(pest, crops)
    }
}
