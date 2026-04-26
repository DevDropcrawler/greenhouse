package nz.keeleysgreenhouse.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "diseases")
data class Disease(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val symptoms: String,
    val conditions: String,
    val controls: List<String>,
    val imageResName: String,
    val affectedCrops: List<Int>,
    val youtubeVideoIds: List<String>,
    val youtubeSearchQuery: String
)

@Entity(tableName = "diseases_fts")
@Fts4
data class DiseaseFts(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowid: Int,
    val diseaseId: Int,
    val name: String
)
