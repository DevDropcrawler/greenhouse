package nz.keeleysgreenhouse.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import nz.keeleysgreenhouse.app.data.dao.PestDao
import nz.keeleysgreenhouse.app.data.entity.Pest
import javax.inject.Inject

class ObservePestsUseCase @Inject constructor(
    private val pestDao: PestDao
) {
    operator fun invoke(): Flow<List<Pest>> = pestDao.observeAll()
}
