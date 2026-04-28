package nz.keeleysgreenhouse.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "crops")
data class Crop(
    @PrimaryKey val id: Int,
    val commonName: String,
    val maoriName: String?,
    val aliases: List<String>,
    val family: String,
    val category: CropCategory,
    val isFruitTree: Boolean,
    val greenhouseRecommended: Boolean,
    val outdoorAlsoOk: Boolean,
    val seedSowMonths: List<Int>,
    val seedlingPlantMonths: List<Int>,
    val harvestMonths: List<Int>,
    val daysSeedToSeedling: IntRange,
    val daysSeedlingToHarvest: IntRange,
    val yearsToFirstFruit: Int?,
    val sowDepthMm: Int?,
    val spacingCm: Int,
    val rowSpacingCm: Int?,
    val potSizeLitres: Int?,
    val dayTempC: IntRange,
    val nightTempC: IntRange,
    val humidityPct: IntRange,
    val sunNeed: SunNeed,
    val soilPhRange: ClosedFloatingPointRange<Float>,
    val waterNeed: WaterNeed,
    val feedingNotes: String,
    val growingNotes: String,
    val sowingNotes: String,
    val greenhouseNotes: String,
    val pruningNotes: String?,
    val pollinationNotes: String?,
    val companionPlants: List<Int>,
    val avoidPlants: List<Int>,
    val commonPests: List<Int>,
    val commonDiseases: List<Int>,
    val imageResName: String,
    val thumbnailResName: String,
    val youtubeVideoIds: List<String>,
    val youtubeSearchQuery: String
)

@Entity(tableName = "crops_fts")
@Fts4
data class CropFts(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowid: Int,
    val cropId: Int,
    val commonName: String,
    val maoriName: String,
    val aliases: String,
    val family: String,
    val pestNames: String,
    val diseaseNames: String
)
