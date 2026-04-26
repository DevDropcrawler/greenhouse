package nz.keeleysgreenhouse.app.data.entity

enum class CropCategory {
    FRUITING,
    LEAFY,
    ROOT,
    BRASSICA,
    LEGUME,
    ALLIUM,
    HERB,
    CUCURBIT,
    FRUIT_TREE,
    VINE_BERRY,
    PERENNIAL,
    MICROGREEN
}

enum class SunNeed { FULL, PARTIAL }

enum class WaterNeed { LOW, MEDIUM, HIGH }

enum class FavouriteRefType { CROP, PEST, DISEASE }
