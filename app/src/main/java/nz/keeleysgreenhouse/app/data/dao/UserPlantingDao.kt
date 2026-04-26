package nz.keeleysgreenhouse.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import nz.keeleysgreenhouse.app.data.entity.UserPlanting

@Dao
interface UserPlantingDao {
    @Query("SELECT * FROM user_plantings ORDER BY sownDate DESC")
    fun observeAll(): Flow<List<UserPlanting>>

    @Query("SELECT * FROM user_plantings WHERE id = :id")
    suspend fun getById(id: Long): UserPlanting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(planting: UserPlanting): Long

    @Update
    suspend fun update(planting: UserPlanting)

    @Delete
    suspend fun delete(planting: UserPlanting)
}
