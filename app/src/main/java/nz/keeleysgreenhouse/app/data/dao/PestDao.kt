package nz.keeleysgreenhouse.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nz.keeleysgreenhouse.app.data.entity.Pest
import nz.keeleysgreenhouse.app.data.entity.PestFts

@Dao
interface PestDao {
    @Query("SELECT * FROM pests ORDER BY name")
    fun observeAll(): Flow<List<Pest>>

    @Query("SELECT * FROM pests WHERE id = :id")
    suspend fun getById(id: Int): Pest?

    @Query("SELECT COUNT(*) FROM pests")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pests: List<Pest>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFtsAll(rows: List<PestFts>)

    @Query("SELECT pestId FROM pests_fts WHERE pests_fts MATCH :query")
    suspend fun searchIds(query: String): List<Int>
}
