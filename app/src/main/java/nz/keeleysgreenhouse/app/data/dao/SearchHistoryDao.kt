package nz.keeleysgreenhouse.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import nz.keeleysgreenhouse.app.data.entity.SearchHistory

const val SEARCH_HISTORY_CAP = 10

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = SEARCH_HISTORY_CAP): Flow<List<SearchHistory>>

    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = SEARCH_HISTORY_CAP): List<SearchHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SearchHistory): Long

    @Query("DELETE FROM search_history WHERE query = :query COLLATE NOCASE")
    suspend fun deleteByQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()

    @Query(
        "DELETE FROM search_history WHERE id NOT IN " +
            "(SELECT id FROM search_history ORDER BY searchedAt DESC LIMIT :cap)"
    )
    suspend fun trim(cap: Int = SEARCH_HISTORY_CAP)

    @Transaction
    suspend fun record(query: String, now: Long, cap: Int = SEARCH_HISTORY_CAP) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        deleteByQuery(trimmed)
        insert(SearchHistory(query = trimmed, searchedAt = now))
        trim(cap)
    }
}
