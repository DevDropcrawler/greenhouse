package nz.keeleysgreenhouse.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.DiseaseDao
import nz.keeleysgreenhouse.app.data.dao.FavouriteDao
import nz.keeleysgreenhouse.app.data.dao.PestDao
import nz.keeleysgreenhouse.app.data.dao.SearchHistoryDao
import nz.keeleysgreenhouse.app.data.dao.TaskDao
import nz.keeleysgreenhouse.app.data.dao.UserPlantingDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.CropFts
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.data.entity.DiseaseFts
import nz.keeleysgreenhouse.app.data.entity.Favourite
import nz.keeleysgreenhouse.app.data.entity.Pest
import nz.keeleysgreenhouse.app.data.entity.PestFts
import nz.keeleysgreenhouse.app.data.entity.SearchHistory
import nz.keeleysgreenhouse.app.data.entity.Task
import nz.keeleysgreenhouse.app.data.entity.UserPlanting

@Database(
    entities = [
        Crop::class,
        CropFts::class,
        Pest::class,
        PestFts::class,
        Disease::class,
        DiseaseFts::class,
        UserPlanting::class,
        Task::class,
        Favourite::class,
        SearchHistory::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cropDao(): CropDao
    abstract fun pestDao(): PestDao
    abstract fun diseaseDao(): DiseaseDao
    abstract fun userPlantingDao(): UserPlantingDao
    abstract fun taskDao(): TaskDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
