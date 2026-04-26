package nz.keeleysgreenhouse.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.CropCategory
import nz.keeleysgreenhouse.app.data.entity.CropFts
import nz.keeleysgreenhouse.app.data.entity.SunNeed
import nz.keeleysgreenhouse.app.data.entity.WaterNeed
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAllMonthsCropsUseCaseTest {

    private fun crop(
        id: Int,
        sow: List<Int> = emptyList(),
        plant: List<Int> = emptyList(),
        harvest: List<Int> = emptyList()
    ) = Crop(
        id = id,
        commonName = "C$id",
        maoriName = null,
        aliases = emptyList(),
        family = "Test",
        category = CropCategory.HERB,
        isFruitTree = false,
        greenhouseRecommended = true,
        outdoorAlsoOk = false,
        seedSowMonths = sow,
        seedlingPlantMonths = plant,
        harvestMonths = harvest,
        daysSeedToSeedling = 1..2,
        daysSeedlingToHarvest = 1..2,
        yearsToFirstFruit = null,
        sowDepthMm = 1,
        spacingCm = 10,
        rowSpacingCm = null,
        potSizeLitres = null,
        dayTempC = 18..25,
        nightTempC = 12..18,
        humidityPct = 50..70,
        sunNeed = SunNeed.FULL,
        soilPhRange = 6.0f..7.0f,
        waterNeed = WaterNeed.MEDIUM,
        feedingNotes = "",
        sowingNotes = "",
        greenhouseNotes = "",
        pruningNotes = null,
        pollinationNotes = null,
        companionPlants = emptyList(),
        avoidPlants = emptyList(),
        commonPests = emptyList(),
        commonDiseases = emptyList(),
        imageResName = "",
        thumbnailResName = "",
        youtubeVideoIds = emptyList(),
        youtubeSearchQuery = ""
    )

    private class FakeCropDao(initial: List<Crop>) : CropDao {
        private val flow = MutableStateFlow(initial)
        override fun observeAll(): Flow<List<Crop>> = flow
        override suspend fun getById(id: Int): Crop? = flow.value.firstOrNull { it.id == id }
        override suspend fun count(): Int = flow.value.size
        override suspend fun insertAll(crops: List<Crop>) { flow.value = crops }
        override suspend fun insertFtsAll(rows: List<CropFts>) {}
        override suspend fun searchIds(query: String): List<Int> = emptyList()
    }

    @Test
    fun returnsTwelveMonthSummariesWithCorrectCounts() = runBlocking {
        val crops = listOf(
            crop(1, sow = listOf(4, 5)),
            crop(2, sow = listOf(4), plant = listOf(5)),
            crop(3, harvest = listOf(4, 5, 6))
        )
        val result = GetAllMonthsCropsUseCase(FakeCropDao(crops)).invoke().first()

        assertEquals(12, result.size)
        val april = result.first { it.month == 4 }
        assertEquals(2, april.seedCount)
        assertEquals(0, april.seedlingCount)
        assertEquals(1, april.harvestCount)
        val may = result.first { it.month == 5 }
        assertEquals(1, may.seedCount)
        assertEquals(1, may.seedlingCount)
        assertEquals(1, may.harvestCount)
        val january = result.first { it.month == 1 }
        assertEquals(0, january.seedCount + january.seedlingCount + january.harvestCount)
    }
}
