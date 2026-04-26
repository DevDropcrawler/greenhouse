package nz.keeleysgreenhouse.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import nz.keeleysgreenhouse.app.data.dao.DiseaseDao
import nz.keeleysgreenhouse.app.data.entity.Disease
import javax.inject.Inject

class ObserveDiseasesUseCase @Inject constructor(
    private val diseaseDao: DiseaseDao
) {
    operator fun invoke(): Flow<List<Disease>> = diseaseDao.observeAll()
}
