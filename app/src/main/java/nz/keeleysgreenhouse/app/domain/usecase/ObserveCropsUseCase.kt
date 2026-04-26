package nz.keeleysgreenhouse.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import javax.inject.Inject

class ObserveCropsUseCase @Inject constructor(
    private val cropDao: CropDao
) {
    operator fun invoke(): Flow<List<Crop>> = cropDao.observeAll()
}
