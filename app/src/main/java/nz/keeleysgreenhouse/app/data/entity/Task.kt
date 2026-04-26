package nz.keeleysgreenhouse.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantingId: Long?,
    val cropId: Int?,
    val dueDate: String,
    val title: String,
    val done: Boolean
)
