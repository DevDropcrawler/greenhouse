package nz.keeleysgreenhouse.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "pests")
data class Pest(
    @PrimaryKey val id: Int,
    val name: String,
    val aliases: List<String>,
    val shortDescription: String,
    val symptoms: String,
    val pestImageResName: String,
    val damageImageResName: String,
    val organicControls: List<String>,
    val biologicalControls: List<String>,
    val chemicalNotes: String,
    val preventionTips: List<String>,
    val affectedCrops: List<Int>,
    val youtubeVideoIds: List<String>,
    val youtubeSearchQuery: String
)

@Entity(tableName = "pests_fts")
@Fts4
data class PestFts(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowid: Int,
    val pestId: Int,
    val name: String,
    val aliases: String
)
