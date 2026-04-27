package nz.keeleysgreenhouse.app.domain.usecase

import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.TaskDao
import nz.keeleysgreenhouse.app.data.dao.UserPlantingDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.CropCategory
import nz.keeleysgreenhouse.app.data.entity.Task
import nz.keeleysgreenhouse.app.data.entity.UserPlanting
import java.time.LocalDate
import javax.inject.Inject

class AddPlantingUseCase @Inject constructor(
    private val cropDao: CropDao,
    private val plantingDao: UserPlantingDao,
    private val taskDao: TaskDao
) {
    suspend operator fun invoke(
        cropId: Int,
        sownDate: LocalDate,
        location: String,
        notes: String
    ): Long {
        val crop = requireNotNull(cropDao.getById(cropId)) { "Unknown crop $cropId" }

        val seedToSeedlingDays = crop.daysSeedToSeedling.midpoint()
        val seedlingToHarvestDays = crop.daysSeedlingToHarvest

        val transplantDate = sownDate.plusDays(seedToSeedlingDays.toLong())
        val harvestStart = transplantDate.plusDays(seedlingToHarvestDays.first.toLong())
        val harvestEnd = transplantDate.plusDays(seedlingToHarvestDays.last.toLong())

        val plantingId = plantingDao.insert(
            UserPlanting(
                cropId = crop.id,
                sownDate = sownDate.toString(),
                transplantedDate = null,
                expectedHarvestStart = harvestStart.toString(),
                expectedHarvestEnd = harvestEnd.toString(),
                location = location,
                notes = notes,
                photoUri = null
            )
        )

        taskDao.insertAll(buildTasks(plantingId, crop, transplantDate, harvestStart))
        return plantingId
    }

    private fun buildTasks(
        plantingId: Long,
        crop: Crop,
        transplantDate: LocalDate,
        harvestStart: LocalDate
    ): List<Task> {
        val list = mutableListOf<Task>()
        list += Task(
            plantingId = plantingId,
            cropId = crop.id,
            dueDate = transplantDate.toString(),
            title = "Transplant ${crop.commonName}",
            done = false
        )
        list += Task(
            plantingId = plantingId,
            cropId = crop.id,
            dueDate = harvestStart.toString(),
            title = "Start harvesting ${crop.commonName}",
            done = false
        )
        if (crop.category == CropCategory.FRUITING) {
            val midDays = (transplantDate.toEpochDay() + harvestStart.toEpochDay()) / 2
            list += Task(
                plantingId = plantingId,
                cropId = crop.id,
                dueDate = LocalDate.ofEpochDay(midDays).toString(),
                title = "Feed ${crop.commonName}",
                done = false
            )
        }
        return list
    }

    private fun IntRange.midpoint(): Int = (first + last) / 2
}
