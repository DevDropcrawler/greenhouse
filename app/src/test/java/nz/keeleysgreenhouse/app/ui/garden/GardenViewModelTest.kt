package nz.keeleysgreenhouse.app.ui.garden

import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.CropCategory
import nz.keeleysgreenhouse.app.data.entity.SunNeed
import nz.keeleysgreenhouse.app.data.entity.UserPlanting
import nz.keeleysgreenhouse.app.data.entity.WaterNeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GardenViewModelTest {

    private val crop = Crop(
        id = 1, commonName = "Tomato", maoriName = null, aliases = emptyList(),
        family = "Solanaceae", category = CropCategory.FRUITING,
        isFruitTree = false, greenhouseRecommended = true, outdoorAlsoOk = false,
        seedSowMonths = emptyList(), seedlingPlantMonths = emptyList(), harvestMonths = emptyList(),
        daysSeedToSeedling = 20..30, daysSeedlingToHarvest = 60..80,
        yearsToFirstFruit = null, sowDepthMm = 5, spacingCm = 50, rowSpacingCm = null, potSizeLitres = null,
        dayTempC = 20..25, nightTempC = 16..18, humidityPct = 60..70,
        sunNeed = SunNeed.FULL, soilPhRange = 6.0f..6.8f, waterNeed = WaterNeed.HIGH,
        feedingNotes = "", sowingNotes = "", greenhouseNotes = "",
        pruningNotes = null, pollinationNotes = null,
        companionPlants = emptyList(), avoidPlants = emptyList(),
        commonPests = emptyList(), commonDiseases = emptyList(),
        imageResName = "x", thumbnailResName = "x",
        youtubeVideoIds = emptyList(), youtubeSearchQuery = ""
    )

    private fun planting(sown: LocalDate, transplanted: LocalDate? = null): UserPlanting {
        val tDate = transplanted ?: sown.plusDays(25)
        val hStart = tDate.plusDays(60)
        val hEnd = tDate.plusDays(80)
        return UserPlanting(
            cropId = 1,
            sownDate = sown.toString(),
            transplantedDate = transplanted?.toString(),
            expectedHarvestStart = hStart.toString(),
            expectedHarvestEnd = hEnd.toString(),
            location = "Greenhouse",
            notes = "",
            photoUri = null
        )
    }

    @Test
    fun stageIsSeedBeforeTransplant() {
        val sown = LocalDate.of(2026, 4, 1)
        val row = GardenViewModel.toRow(planting(sown), crop, today = LocalDate.of(2026, 4, 10))
        assertEquals(PlantingStage.SEED, row.stage)
        assertTrue(row.progress in 0f..1f)
    }

    @Test
    fun stageIsHarvestInsideHarvestWindow() {
        val sown = LocalDate.of(2026, 1, 1)
        // transplant 2026-01-26, harvest start 2026-03-27, end 2026-04-16
        val row = GardenViewModel.toRow(planting(sown), crop, today = LocalDate.of(2026, 4, 1))
        assertEquals(PlantingStage.HARVEST, row.stage)
    }

    @Test
    fun stageIsGrowingPastMidpointBeforeHarvest() {
        val sown = LocalDate.of(2026, 1, 1)
        // transplant 2026-01-26, harvest start 2026-03-27 -> midpoint ~ 2026-02-25
        val row = GardenViewModel.toRow(planting(sown), crop, today = LocalDate.of(2026, 3, 15))
        assertEquals(PlantingStage.GROWING, row.stage)
    }
}
