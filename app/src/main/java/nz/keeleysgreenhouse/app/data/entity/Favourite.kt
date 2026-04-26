package nz.keeleysgreenhouse.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class Favourite(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val refType: FavouriteRefType,
    val refId: Int,
    val savedAt: Long
)
