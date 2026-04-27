package nz.keeleysgreenhouse.app.domain.usecase

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import nz.keeleysgreenhouse.app.data.AppDatabase
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.CropCategory
import nz.keeleysgreenhouse.app.data.entity.SunNeed
import nz.keeleysgreenhouse.app.data.entity.WaterNeed
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class AddPlantingUseCaseTest {

    private lateinit var db: AppDatabase
    private lateinit var useCase: AddPlantingUseCase

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        db.cropDao().insertAll(listOf(testCrop))
        useCase = AddPlantingUseCase(db.cropDao(), db.userPlantingDao(), db.taskDao())
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertsPlantingAndGeneratesTasks() = runBlocking {
        val sown = LocalDate.of(2026, 1, 1)
        val plantingId = useCase(cropId = 1, sownDate = sown, location = "Greenhouse", notes = "")

        val planting = db.userPlantingDao().getById(plantingId)
        assertNotNull(planting)
        assertEquals("2026-01-01", planting!!.sownDate)
        // daysSeedToSeedling 20..30 -> midpoint 25 -> transplant 2026-01-26
        // daysSeedlingToHarvest 60..80 -> harvest start 2026-01-26 + 60 = 2026-03-27
        // harvest end 2026-01-26 + 80 = 2026-04-16
        assertEquals("2026-03-27", planting.expectedHarvestStart)
        assertEquals("2026-04-16", planting.expectedHarvestEnd)

        val tasks = db.taskDao().observeAll().first()
        // FRUITING crop -> 3 tasks (transplant, harvest start, feed midpoint)
        assertEquals(3, tasks.size)
        assertTrue(tasks.any { it.title.startsWith("Transplant ") && it.dueDate == "2026-01-26" })
        assertTrue(tasks.any { it.title.startsWith("Start harvesting ") && it.dueDate == "2026-03-27" })
        assertTrue(tasks.any { it.title.startsWith("Feed ") })
        assertTrue(tasks.all { it.plantingId == plantingId })
    }

    private val testCrop = Crop(
        id = 1,
        commonName = "Tomato 'Money Maker'",
        maoriName = null,
        aliases = emptyList(),
        family = "Solanaceae",
        category = CropCategory.FRUITING,
        isFruitTree = false,
        greenhouseRecommended = true,
        outdoorAlsoOk = false,
        seedSowMonths = listOf(7, 8, 9, 10),
        seedlingPlantMonths = listOf(9, 10, 11, 12),
        harvestMonths = listOf(12, 1, 2, 3, 4, 5),
        daysSeedToSeedling = 20..30,
        daysSeedlingToHarvest = 60..80,
        yearsToFirstFruit = null,
        sowDepthMm = 5,
        spacingCm = 50,
        rowSpacingCm = null,
        potSizeLitres = null,
        dayTempC = 20..25,
        nightTempC = 16..18,
        humidityPct = 60..70,
        sunNeed = SunNeed.FULL,
        soilPhRange = 6.0f..6.8f,
        waterNeed = WaterNeed.HIGH,
        feedingNotes = "",
        sowingNotes = "",
        greenhouseNotes = "",
        pruningNotes = null,
        pollinationNotes = null,
        companionPlants = emptyList(),
        avoidPlants = emptyList(),
        commonPests = emptyList(),
        commonDiseases = emptyList(),
        imageResName = "crop_placeholder",
        thumbnailResName = "thumb_placeholder",
        youtubeVideoIds = emptyList(),
        youtubeSearchQuery = ""
    )
}
