package nz.keeleysgreenhouse.app.domain.usecase

import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.DiseaseDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.Disease
import javax.inject.Inject

data class DiseaseDetail(
    val disease: Disease,
    val affectedCrops: List<Crop>
)

class GetDiseaseDetailUseCase @Inject constructor(
    private val diseaseDao: DiseaseDao,
    private val cropDao: CropDao
) {
    suspend operator fun invoke(diseaseId: Int): DiseaseDetail? {
        val disease = diseaseDao.getById(diseaseId) ?: return null
        val crops = disease.affectedCrops.mapNotNull { cropDao.getById(it) }
        return DiseaseDetail(disease, crops)
    }
}
