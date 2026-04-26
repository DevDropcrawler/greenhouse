package nz.keeleysgreenhouse.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.DiseaseDao
import nz.keeleysgreenhouse.app.data.dao.PestDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.CropCategory
import nz.keeleysgreenhouse.app.data.entity.CropFts
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.data.entity.DiseaseFts
import nz.keeleysgreenhouse.app.data.entity.Pest
import nz.keeleysgreenhouse.app.data.entity.PestFts
import nz.keeleysgreenhouse.app.data.entity.SunNeed
import nz.keeleysgreenhouse.app.data.entity.WaterNeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchAllUseCaseTest {

    @Test
    fun blankQueryReturnsEmpty() = runBlocking {
        val useCase = SearchAllUseCase(
            cropDao = FakeCropDao(emptyList(), expectedQuery = null),
            pestDao = FakePestDao(emptyList(), expectedQuery = null),
            diseaseDao = FakeDiseaseDao(emptyList(), expectedQuery = null)
        )
        val result = useCase("   ")
        assertTrue(result.isEmpty)
    }

    @Test
    fun appendsPrefixWildcardToTokens() = runBlocking {
        val expected = "tom*"
        val crops = listOf(crop(1, "Tomato"))
        val useCase = SearchAllUseCase(
            cropDao = FakeCropDao(crops, expectedQuery = expected, matchIds = listOf(1)),
            pestDao = FakePestDao(emptyList(), expectedQuery = expected),
            diseaseDao = FakeDiseaseDao(emptyList(), expectedQuery = expected)
        )
        val result = useCase("tom")
        assertEquals(listOf("Tomato"), result.crops.map { it.commonName })
    }

    @Test
    fun multiTokenQueryWildcardsEach() = runBlocking {
        val expected = "early* blight*"
        val diseases = listOf(disease(7, "Early Blight"))
        val useCase = SearchAllUseCase(
            cropDao = FakeCropDao(emptyList(), expectedQuery = expected),
            pestDao = FakePestDao(emptyList(), expectedQuery = expected),
            diseaseDao = FakeDiseaseDao(diseases, expectedQuery = expected, matchIds = listOf(7))
        )
        val result = useCase("early blight")
        assertEquals(listOf("Early Blight"), result.diseases.map { it.name })
    }

    @Test
    fun fetchesEntitiesAcrossAllThreeTypes() = runBlocking {
        val expected = "leaf*"
        val useCase = SearchAllUseCase(
            cropDao = FakeCropDao(listOf(crop(1, "Lettuce")), expected, listOf(1)),
            pestDao = FakePestDao(listOf(pest(2, "Leafminer")), expected, listOf(2)),
            diseaseDao = FakeDiseaseDao(listOf(disease(3, "Leaf Spot")), expected, listOf(3))
        )
        val result = useCase("leaf")
        assertEquals(1, result.crops.size)
        assertEquals(1, result.pests.size)
        assertEquals(1, result.diseases.size)
    }

    private fun crop(id: Int, name: String) = Crop(
        id = id, commonName = name, maoriName = null, aliases = emptyList(),
        family = "Test", category = CropCategory.HERB, isFruitTree = false,
        greenhouseRecommended = true, outdoorAlsoOk = false,
        seedSowMonths = emptyList(), seedlingPlantMonths = emptyList(), harvestMonths = emptyList(),
        daysSeedToSeedling = 1..2, daysSeedlingToHarvest = 1..2, yearsToFirstFruit = null,
        sowDepthMm = 1, spacingCm = 10, rowSpacingCm = null, potSizeLitres = null,
        dayTempC = 18..25, nightTempC = 12..18, humidityPct = 50..70,
        sunNeed = SunNeed.FULL, soilPhRange = 6.0f..7.0f, waterNeed = WaterNeed.MEDIUM,
        feedingNotes = "", sowingNotes = "", greenhouseNotes = "", pruningNotes = null,
        pollinationNotes = null, companionPlants = emptyList(), avoidPlants = emptyList(),
        commonPests = emptyList(), commonDiseases = emptyList(),
        imageResName = "x", thumbnailResName = "x", youtubeVideoIds = emptyList(),
        youtubeSearchQuery = ""
    )

    private fun pest(id: Int, name: String) = Pest(
        id = id, name = name, aliases = emptyList(), shortDescription = "",
        symptoms = "", pestImageResName = "x", damageImageResName = "x",
        organicControls = emptyList(), biologicalControls = emptyList(),
        chemicalNotes = "", preventionTips = emptyList(), affectedCrops = emptyList(),
        youtubeVideoIds = emptyList(), youtubeSearchQuery = ""
    )

    private fun disease(id: Int, name: String) = Disease(
        id = id, name = name, description = "", symptoms = "", conditions = "",
        controls = emptyList(), imageResName = "x", affectedCrops = emptyList(),
        youtubeVideoIds = emptyList(), youtubeSearchQuery = ""
    )

    private class FakeCropDao(
        private val data: List<Crop>,
        private val expectedQuery: String?,
        private val matchIds: List<Int> = emptyList()
    ) : CropDao {
        override fun observeAll(): Flow<List<Crop>> = MutableStateFlow(data)
        override suspend fun getById(id: Int): Crop? = data.firstOrNull { it.id == id }
        override suspend fun count(): Int = data.size
        override suspend fun insertAll(crops: List<Crop>) {}
        override suspend fun insertFtsAll(rows: List<CropFts>) {}
        override suspend fun searchIds(query: String): List<Int> {
            if (expectedQuery != null) assertEquals(expectedQuery, query)
            return matchIds
        }
    }

    private class FakePestDao(
        private val data: List<Pest>,
        private val expectedQuery: String?,
        private val matchIds: List<Int> = emptyList()
    ) : PestDao {
        override fun observeAll(): Flow<List<Pest>> = MutableStateFlow(data)
        override suspend fun getById(id: Int): Pest? = data.firstOrNull { it.id == id }
        override suspend fun count(): Int = data.size
        override suspend fun insertAll(pests: List<Pest>) {}
        override suspend fun insertFtsAll(rows: List<PestFts>) {}
        override suspend fun searchIds(query: String): List<Int> {
            if (expectedQuery != null) assertEquals(expectedQuery, query)
            return matchIds
        }
    }

    private class FakeDiseaseDao(
        private val data: List<Disease>,
        private val expectedQuery: String?,
        private val matchIds: List<Int> = emptyList()
    ) : DiseaseDao {
        override fun observeAll(): Flow<List<Disease>> = MutableStateFlow(data)
        override suspend fun getById(id: Int): Disease? = data.firstOrNull { it.id == id }
        override suspend fun count(): Int = data.size
        override suspend fun insertAll(diseases: List<Disease>) {}
        override suspend fun insertFtsAll(rows: List<DiseaseFts>) {}
        override suspend fun searchIds(query: String): List<Int> {
            if (expectedQuery != null) assertEquals(expectedQuery, query)
            return matchIds
        }
    }
}
