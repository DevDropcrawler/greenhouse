package nz.keeleysgreenhouse.app.data.seed

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import nz.keeleysgreenhouse.app.data.AppDatabase
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.CropCategory
import nz.keeleysgreenhouse.app.data.entity.CropFts
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.data.entity.DiseaseFts
import nz.keeleysgreenhouse.app.data.entity.Pest
import nz.keeleysgreenhouse.app.data.entity.PestFts
import nz.keeleysgreenhouse.app.data.entity.SunNeed
import nz.keeleysgreenhouse.app.data.entity.WaterNeed
import java.io.InputStream

fun interface SeedSource {
    fun open(name: String): InputStream
}

class DatabaseSeeder(
    private val db: AppDatabase,
    private val source: SeedSource
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun seed() = withContext(Dispatchers.IO) {
        if (db.cropDao().count() > 0) return@withContext

        val cropSeeds: List<CropSeed> = source.open("seed/crops.json").use {
            json.decodeFromString(it.readBytes().decodeToString())
        }
        val pestSeeds: List<PestSeed> = source.open("seed/pests.json").use {
            json.decodeFromString(it.readBytes().decodeToString())
        }
        val diseaseSeeds: List<DiseaseSeed> = source.open("seed/diseases.json").use {
            json.decodeFromString(it.readBytes().decodeToString())
        }

        val pestNamesById = pestSeeds.associate { it.id to it.name }
        val diseaseNamesById = diseaseSeeds.associate { it.id to it.name }

        val crops = cropSeeds.map { it.toEntity() }
        db.cropDao().insertAll(crops)
        db.cropDao().insertFtsAll(cropSeeds.map { it.toFts(pestNamesById, diseaseNamesById) })

        db.pestDao().insertAll(pestSeeds.map { it.toEntity() })
        db.pestDao().insertFtsAll(pestSeeds.map { it.toFts() })

        db.diseaseDao().insertAll(diseaseSeeds.map { it.toEntity() })
        db.diseaseDao().insertFtsAll(diseaseSeeds.map { it.toFts() })
    }
}

private fun CropSeed.toEntity() = Crop(
    id = id,
    commonName = commonName,
    maoriName = maoriName,
    aliases = aliases,
    family = family,
    category = CropCategory.valueOf(category),
    isFruitTree = isFruitTree,
    greenhouseRecommended = greenhouseRecommended,
    outdoorAlsoOk = outdoorAlsoOk,
    seedSowMonths = seedSowMonths,
    seedlingPlantMonths = seedlingPlantMonths,
    harvestMonths = harvestMonths,
    outdoorSowMonths = outdoorSowMonths,
    outdoorSeedlingMonths = outdoorSeedlingMonths,
    outdoorHarvestMonths = outdoorHarvestMonths,
    daysSeedToSeedling = daysSeedToSeedlingMin..daysSeedToSeedlingMax,
    daysSeedlingToHarvest = daysSeedlingToHarvestMin..daysSeedlingToHarvestMax,
    yearsToFirstFruit = yearsToFirstFruit,
    sowDepthMm = sowDepthMm,
    spacingCm = spacingCm,
    rowSpacingCm = rowSpacingCm,
    potSizeLitres = potSizeLitres,
    dayTempC = dayTempCMin..dayTempCMax,
    nightTempC = nightTempCMin..nightTempCMax,
    humidityPct = humidityPctMin..humidityPctMax,
    sunNeed = SunNeed.valueOf(sunNeed),
    soilPhRange = soilPhMin..soilPhMax,
    waterNeed = WaterNeed.valueOf(waterNeed),
    feedingNotes = feedingNotes,
    growingNotes = growingNotes,
    sowingNotes = sowingNotes,
    greenhouseNotes = greenhouseNotes,
    outdoorNotes = outdoorNotes,
    pruningNotes = pruningNotes,
    pollinationNotes = pollinationNotes,
    companionPlants = companionPlants,
    avoidPlants = avoidPlants,
    commonPests = commonPests,
    commonDiseases = commonDiseases,
    imageResName = imageResName.ifBlank { "crop_placeholder" },
    thumbnailResName = thumbnailResName.ifBlank { "thumb_placeholder" },
    youtubeVideoIds = youtubeVideoIds,
    youtubeSearchQuery = youtubeSearchQuery.ifBlank { "growing $commonName new zealand" }
)

private fun CropSeed.toFts(
    pestNames: Map<Int, String>,
    diseaseNames: Map<Int, String>
) = CropFts(
    rowid = id,
    cropId = id,
    commonName = commonName,
    maoriName = maoriName.orEmpty(),
    aliases = aliases.joinToString(" "),
    family = family,
    pestNames = commonPests.mapNotNull { pestNames[it] }.joinToString(" "),
    diseaseNames = commonDiseases.mapNotNull { diseaseNames[it] }.joinToString(" ")
)

private fun PestSeed.toEntity() = Pest(
    id = id,
    name = name,
    aliases = aliases,
    shortDescription = shortDescription,
    symptoms = symptoms,
    pestImageResName = pestImageResName.ifBlank { "pest_placeholder" },
    damageImageResName = damageImageResName.ifBlank { "pest_placeholder" },
    organicControls = organicControls,
    biologicalControls = biologicalControls,
    chemicalNotes = chemicalNotes,
    preventionTips = preventionTips,
    affectedCrops = affectedCrops,
    youtubeVideoIds = youtubeVideoIds,
    youtubeSearchQuery = youtubeSearchQuery.ifBlank { "$name control new zealand" }
)

private fun PestSeed.toFts() = PestFts(
    rowid = id,
    pestId = id,
    name = name,
    aliases = aliases.joinToString(" ")
)

private fun DiseaseSeed.toEntity() = Disease(
    id = id,
    name = name,
    description = description,
    symptoms = symptoms,
    conditions = conditions,
    controls = controls,
    imageResName = imageResName.ifBlank { "disease_placeholder" },
    affectedCrops = affectedCrops,
    youtubeVideoIds = youtubeVideoIds,
    youtubeSearchQuery = youtubeSearchQuery.ifBlank { "$name plants control" }
)

private fun DiseaseSeed.toFts() = DiseaseFts(
    rowid = id,
    diseaseId = id,
    name = name
)
