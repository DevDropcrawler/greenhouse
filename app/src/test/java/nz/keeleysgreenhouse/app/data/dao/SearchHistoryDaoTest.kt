package nz.keeleysgreenhouse.app.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
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
class SearchHistoryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: SearchHistoryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.searchHistoryDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun recordsNewQueryAtTopOfRecent() = runBlocking {
        dao.record("tomato", now = 1000L)
        val recent = dao.observeRecent().first()
        assertEquals(listOf("tomato"), recent.map { it.query })
    }

    @Test
    fun duplicateQueryReplacesPreviousAndStaysUnique() = runBlocking {
        dao.record("tomato", now = 1000L)
        dao.record("basil", now = 2000L)
        dao.record("tomato", now = 3000L)
        val recent = dao.observeRecent().first()
        assertEquals(listOf("tomato", "basil"), recent.map { it.query })
    }

    @Test
    fun cappedAtTenWithOldestDropped() = runBlocking {
        repeat(11) { i ->
            dao.record("query-$i", now = 1000L + i)
        }
        val recent = dao.observeRecent().first()
        assertEquals(10, recent.size)
        assertTrue(recent.none { it.query == "query-0" })
        assertEquals("query-10", recent.first().query)
    }

    @Test
    fun deleteByQueryRemovesOnlyThatEntry() = runBlocking {
        dao.record("tomato", now = 1000L)
        dao.record("basil", now = 2000L)
        dao.record("chilli", now = 3000L)

        dao.deleteByQuery("basil")

        val recent = dao.observeRecent().first()
        assertEquals(listOf("chilli", "tomato"), recent.map { it.query })
    }

    @Test
    fun deleteByQueryIsCaseInsensitive() = runBlocking {
        dao.record("Tomato", now = 1000L)
        dao.deleteByQuery("tomato")
        assertTrue(dao.observeRecent().first().isEmpty())
    }

    @Test
    fun clearAllEmptiesHistory() = runBlocking {
        dao.record("a", now = 1L)
        dao.record("b", now = 2L)
        dao.clearAll()
        assertTrue(dao.observeRecent().first().isEmpty())
    }
}
