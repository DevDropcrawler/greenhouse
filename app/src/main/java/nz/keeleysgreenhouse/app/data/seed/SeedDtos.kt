package nz.keeleysgreenhouse.app.data.seed

import kotlinx.serialization.Serializable

@Serializable
data class CropSeed(
    val id: Int,
    val commonName: String,
    val maoriName: String? = null,
    val aliases: List<String> = emptyList(),
    val family: String = "",
    val category: String,
    val isFruitTree: Boolean = false,
    val greenhouseRecommended: Boolean = true,
    val outdoorAlsoOk: Boolean = true,
    val seedSowMonths: List<Int> = emptyList(),
    val seedlingPlantMonths: List<Int> = emptyList(),
    val harvestMonths: List<Int> = emptyList(),
    val daysSeedToSeedlingMin: Int = 0,
    val daysSeedToSeedlingMax: Int = 0,
    val daysSeedlingToHarvestMin: Int = 0,
    val daysSeedlingToHarvestMax: Int = 0,
    val yearsToFirstFruit: Int? = null,
    val sowDepthMm: Int? = null,
    val spacingCm: Int = 30,
    val rowSpacingCm: Int? = null,
    val potSizeLitres: Int? = null,
    val dayTempCMin: Int = 18,
    val dayTempCMax: Int = 25,
    val nightTempCMin: Int = 14,
    val nightTempCMax: Int = 18,
    val humidityPctMin: Int = 50,
    val humidityPctMax: Int = 70,
    val sunNeed: String = "FULL",
    val soilPhMin: Float = 6.0f,
    val soilPhMax: Float = 7.0f,
    val waterNeed: String = "MEDIUM",
    val feedingNotes: String = "",
    val growingNotes: String = "",
    val sowingNotes: String = "",
    val greenhouseNotes: String = "",
    val pruningNotes: String? = null,
    val pollinationNotes: String? = null,
    val companionPlants: List<Int> = emptyList(),
    val avoidPlants: List<Int> = emptyList(),
    val commonPests: List<Int> = emptyList(),
    val commonDiseases: List<Int> = emptyList(),
    val imageResName: String = "",
    val thumbnailResName: String = "",
    val youtubeVideoIds: List<String> = emptyList(),
    val youtubeSearchQuery: String = ""
)

@Serializable
data class PestSeed(
    val id: Int,
    val name: String,
    val aliases: List<String> = emptyList(),
    val shortDescription: String = "",
    val symptoms: String = "",
    val pestImageResName: String = "",
    val damageImageResName: String = "",
    val organicControls: List<String> = emptyList(),
    val biologicalControls: List<String> = emptyList(),
    val chemicalNotes: String = "",
    val preventionTips: List<String> = emptyList(),
    val affectedCrops: List<Int> = emptyList(),
    val youtubeVideoIds: List<String> = emptyList(),
    val youtubeSearchQuery: String = ""
)

@Serializable
data class DiseaseSeed(
    val id: Int,
    val name: String,
    val description: String = "",
    val symptoms: String = "",
    val conditions: String = "",
    val controls: List<String> = emptyList(),
    val imageResName: String = "",
    val affectedCrops: List<Int> = emptyList(),
    val youtubeVideoIds: List<String> = emptyList(),
    val youtubeSearchQuery: String = ""
)
