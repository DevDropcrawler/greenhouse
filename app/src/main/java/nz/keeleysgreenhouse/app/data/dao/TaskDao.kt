package nz.keeleysgreenhouse.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import nz.keeleysgreenhouse.app.data.entity.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate")
    fun observeAll(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY id")
    fun observeForDate(date: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): Task?

    @Query("DELETE FROM tasks WHERE plantingId = :plantingId")
    suspend fun deleteForPlanting(plantingId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
