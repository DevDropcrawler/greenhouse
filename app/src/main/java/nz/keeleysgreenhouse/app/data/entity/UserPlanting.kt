package nz.keeleysgreenhouse.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_plantings")
data class UserPlanting(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cropId: Int,
    val sownDate: String,
    val transplantedDate: String?,
    val expectedHarvestStart: String,
    val expectedHarvestEnd: String,
    val location: String,
    val notes: String,
    val photoUri: String?
)
