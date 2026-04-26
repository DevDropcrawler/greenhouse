package nz.keeleysgreenhouse.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nz.keeleysgreenhouse.app.data.entity.Favourite
import nz.keeleysgreenhouse.app.data.entity.FavouriteRefType

@Dao
interface FavouriteDao {
    @Query("SELECT * FROM favourites ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<Favourite>>

    @Query("SELECT * FROM favourites WHERE refType = :type AND refId = :id LIMIT 1")
    suspend fun find(type: FavouriteRefType, id: Int): Favourite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favourite: Favourite): Long

    @Delete
    suspend fun delete(favourite: Favourite)

    @Query("DELETE FROM favourites WHERE refType = :type AND refId = :id")
    suspend fun deleteByRef(type: FavouriteRefType, id: Int)
}
