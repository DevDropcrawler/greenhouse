package nz.keeleysgreenhouse.app.domain.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.DiseaseDao
import nz.keeleysgreenhouse.app.data.dao.PestDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.data.entity.Pest

data class SearchResults(
    val crops: List<Crop> = emptyList(),
    val pests: List<Pest> = emptyList(),
    val diseases: List<Disease> = emptyList()
) {
    val isEmpty: Boolean get() = crops.isEmpty() && pests.isEmpty() && diseases.isEmpty()
}

class SearchAllUseCase(
    private val cropDao: CropDao,
    private val pestDao: PestDao,
    private val diseaseDao: DiseaseDao
) {
    suspend operator fun invoke(rawQuery: String): SearchResults = coroutineScope {
        val cleaned = sanitise(rawQuery) ?: return@coroutineScope SearchResults()

        val cropsDeferred = async {
            cropDao.searchIds(cleaned).mapNotNull { cropDao.getById(it) }
        }
        val pestsDeferred = async {
            pestDao.searchIds(cleaned).mapNotNull { pestDao.getById(it) }
        }
        val diseasesDeferred = async {
            diseaseDao.searchIds(cleaned).mapNotNull { diseaseDao.getById(it) }
        }

        SearchResults(
            crops = cropsDeferred.await(),
            pests = pestsDeferred.await(),
            diseases = diseasesDeferred.await()
        )
    }

    private fun sanitise(raw: String): String? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        val stripped = trimmed.replace(Regex("[\"*^()-]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        if (stripped.isEmpty()) return null
        val tokens = stripped.split(' ')
        return tokens.joinToString(" ") { "$it*" }
    }
}
