package nz.keeleysgreenhouse.app.data

import androidx.room.TypeConverter
import nz.keeleysgreenhouse.app.data.entity.CropCategory
import nz.keeleysgreenhouse.app.data.entity.FavouriteRefType
import nz.keeleysgreenhouse.app.data.entity.SunNeed
import nz.keeleysgreenhouse.app.data.entity.WaterNeed

class Converters {

    @TypeConverter
    fun intListToString(value: List<Int>?): String? =
        value?.joinToString(",")

    @TypeConverter
    fun stringToIntList(value: String?): List<Int>? =
        value?.takeIf { it.isNotEmpty() }?.split(",")?.map { it.toInt() } ?: value?.let { emptyList() }

    @TypeConverter
    fun stringListToString(value: List<String>?): String? =
        value?.joinToString("|")

    @TypeConverter
    fun stringToStringList(value: String?): List<String>? =
        value?.takeIf { it.isNotEmpty() }?.split("|") ?: value?.let { emptyList() }

    @TypeConverter
    fun intRangeToString(value: IntRange?): String? =
        value?.let { "${it.first}..${it.last}" }

    @TypeConverter
    fun stringToIntRange(value: String?): IntRange? =
        value?.split("..")?.let { IntRange(it[0].toInt(), it[1].toInt()) }

    @TypeConverter
    fun floatRangeToString(value: ClosedFloatingPointRange<Float>?): String? =
        value?.let { "${it.start}..${it.endInclusive}" }

    @TypeConverter
    fun stringToFloatRange(value: String?): ClosedFloatingPointRange<Float>? =
        value?.split("..")?.let { it[0].toFloat()..it[1].toFloat() }

    @TypeConverter fun cropCategoryToString(v: CropCategory?): String? = v?.name
    @TypeConverter fun stringToCropCategory(v: String?): CropCategory? = v?.let { CropCategory.valueOf(it) }

    @TypeConverter fun sunNeedToString(v: SunNeed?): String? = v?.name
    @TypeConverter fun stringToSunNeed(v: String?): SunNeed? = v?.let { SunNeed.valueOf(it) }

    @TypeConverter fun waterNeedToString(v: WaterNeed?): String? = v?.name
    @TypeConverter fun stringToWaterNeed(v: String?): WaterNeed? = v?.let { WaterNeed.valueOf(it) }

    @TypeConverter fun favRefTypeToString(v: FavouriteRefType?): String? = v?.name
    @TypeConverter fun stringToFavRefType(v: String?): FavouriteRefType? = v?.let { FavouriteRefType.valueOf(it) }
}
