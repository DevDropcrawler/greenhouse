package nz.keeleysgreenhouse.app.data

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

// Phase-0 stub. Phase 1 replaces this with the real entity set
// (Crop, Pest, Disease, UserPlanting, Task, Favourite + FTS).
@Entity(tableName = "_phase0_stub")
data class Phase0Stub(@PrimaryKey val id: Int = 0)

@Database(entities = [Phase0Stub::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase()
