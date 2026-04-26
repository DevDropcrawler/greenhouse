package nz.keeleysgreenhouse.app.domain.usecase

import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.DiseaseDao
import nz.keeleysgreenhouse.app.data.dao.PestDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.data.entity.Pest
import javax.inject.Inject

data class CropDetail(
    val crop: Crop,
    val companions: List<Crop>,
    val avoid: List<Crop>,
    val pests: List<Pest>,
    val diseases: List<Disease>
)

class GetCropDetailUseCase @Inject constructor(
    private val cropDao: CropDao,
    private val pestDao: PestDao,
    private val diseaseDao: DiseaseDao
) {
    suspend operator fun invoke(cropId: Int): CropDetail? {
        val crop = cropDao.getById(cropId) ?: return null
        val companions = crop.companionPlants.mapNotNull { cropDao.getById(it) }
        val avoid = crop.avoidPlants.mapNotNull { cropDao.getById(it) }
        val pests = crop.commonPests.mapNotNull { pestDao.getById(it) }
        val diseases = crop.commonDiseases.mapNotNull { diseaseDao.getById(it) }
        return CropDetail(crop, companions, avoid, pests, diseases)
    }
}
