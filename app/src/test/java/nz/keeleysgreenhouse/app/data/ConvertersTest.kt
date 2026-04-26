package nz.keeleysgreenhouse.app.data

import nz.keeleysgreenhouse.app.data.entity.CropCategory
import nz.keeleysgreenhouse.app.data.entity.FavouriteRefType
import nz.keeleysgreenhouse.app.data.entity.SunNeed
import nz.keeleysgreenhouse.app.data.entity.WaterNeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ConvertersTest {

    private val c = Converters()

    @Test fun intList_roundTrip() {
        val original = listOf(1, 5, 12)
        assertEquals(original, c.stringToIntList(c.intListToString(original)))
    }

    @Test fun intList_empty() {
        assertEquals(emptyList<Int>(), c.stringToIntList(c.intListToString(emptyList())))
    }

    @Test fun intList_null() {
        assertNull(c.intListToString(null))
        assertNull(c.stringToIntList(null))
    }

    @Test fun stringList_roundTrip() {
        val original = listOf("Genovese", "Sweet basil", "Thai")
        assertEquals(original, c.stringToStringList(c.stringListToString(original)))
    }

    @Test fun stringList_empty() {
        assertEquals(emptyList<String>(), c.stringToStringList(c.stringListToString(emptyList())))
    }

    @Test fun intRange_roundTrip() {
        val original = 6..14
        assertEquals(original, c.stringToIntRange(c.intRangeToString(original)))
    }

    @Test fun floatRange_roundTrip() {
        val original = 5.5f..7.0f
        val result = c.stringToFloatRange(c.floatRangeToString(original))!!
        assertEquals(original.start, result.start, 0.0001f)
        assertEquals(original.endInclusive, result.endInclusive, 0.0001f)
    }

    @Test fun cropCategory_roundTrip() {
        for (v in CropCategory.values()) {
            assertEquals(v, c.stringToCropCategory(c.cropCategoryToString(v)))
        }
    }

    @Test fun sunNeed_roundTrip() {
        for (v in SunNeed.values()) {
            assertEquals(v, c.stringToSunNeed(c.sunNeedToString(v)))
        }
    }

    @Test fun waterNeed_roundTrip() {
        for (v in WaterNeed.values()) {
            assertEquals(v, c.stringToWaterNeed(c.waterNeedToString(v)))
        }
    }

    @Test fun favouriteRefType_roundTrip() {
        for (v in FavouriteRefType.values()) {
            assertEquals(v, c.stringToFavRefType(c.favRefTypeToString(v)))
        }
    }
}
