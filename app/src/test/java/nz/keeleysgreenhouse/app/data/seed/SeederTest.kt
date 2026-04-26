package nz.keeleysgreenhouse.app.data.seed

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import nz.keeleysgreenhouse.app.data.AppDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SeederTest {

    private lateinit var db: AppDatabase
    private lateinit var seeder: DatabaseSeeder

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        seeder = DatabaseSeeder(db) { name -> context.assets.open(name) }
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun seed_loadsAllExpectedCounts() = runBlocking {
        seeder.seed()

        assertEquals(153, db.cropDao().count())
        assertEquals(12, db.pestDao().count())
        assertEquals(8, db.diseaseDao().count())

        val tomatoes = db.cropDao().searchIds("Tomato")
        assertTrue("expected at least one crop named Tomato", tomatoes.isNotEmpty())

        // Round-trip a crop to ensure converters cope with real seed data.
        val tomato = db.cropDao().getById(1)
        assertEquals("Tomato", tomato?.commonName)
    }

    @Test
    fun seed_isIdempotent() = runBlocking {
        seeder.seed()
        val firstCount = db.cropDao().count()
        seeder.seed()
        assertEquals(firstCount, db.cropDao().count())
    }
}
