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

class GetMonthCropsUseCaseTest {

    private fun crop(
        id: Int,
        name: String,
        sow: List<Int> = emptyList(),
        plant: List<Int> = emptyList(),
        harvest: List<Int> = emptyList()
    ) = Crop(
        id = id,
        commonName = name,
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
    fun bucketsCropsForGivenMonth() = runBlocking {
        val crops = listOf(
            crop(1, "Basil", sow = listOf(4)),
            crop(2, "Tomato", plant = listOf(4)),
            crop(3, "Lemon", harvest = listOf(4)),
            crop(4, "Garlic", sow = listOf(6))
        )
        val result = GetMonthCropsUseCase(FakeCropDao(crops)).invoke(4).first()

        assertEquals(4, result.month)
        assertEquals(listOf("Basil"), result.seed.map { it.commonName })
        assertEquals(listOf("Tomato"), result.seedling.map { it.commonName })
        assertEquals(listOf("Lemon"), result.harvest.map { it.commonName })
    }
}
