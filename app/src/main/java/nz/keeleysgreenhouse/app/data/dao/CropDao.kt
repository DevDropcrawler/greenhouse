package nz.keeleysgreenhouse.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.CropFts

@Dao
interface CropDao {
    @Query("SELECT * FROM crops ORDER BY commonName")
    fun observeAll(): Flow<List<Crop>>

    @Query("SELECT * FROM crops WHERE id = :id")
    suspend fun getById(id: Int): Crop?

    @Query("SELECT COUNT(*) FROM crops")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(crops: List<Crop>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFtsAll(rows: List<CropFts>)

    @Query("SELECT cropId FROM crops_fts WHERE crops_fts MATCH :query")
    suspend fun searchIds(query: String): List<Int>
}
