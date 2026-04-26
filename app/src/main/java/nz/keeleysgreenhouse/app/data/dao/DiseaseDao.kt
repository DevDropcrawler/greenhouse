package nz.keeleysgreenhouse.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.data.entity.DiseaseFts

@Dao
interface DiseaseDao {
    @Query("SELECT * FROM diseases ORDER BY name")
    fun observeAll(): Flow<List<Disease>>

    @Query("SELECT * FROM diseases WHERE id = :id")
    suspend fun getById(id: Int): Disease?

    @Query("SELECT COUNT(*) FROM diseases")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diseases: List<Disease>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFtsAll(rows: List<DiseaseFts>)

    @Query("SELECT diseaseId FROM diseases_fts WHERE diseases_fts MATCH :query")
    suspend fun searchIds(query: String): List<Int>
}
