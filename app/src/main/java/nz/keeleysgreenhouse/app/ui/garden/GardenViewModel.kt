package nz.keeleysgreenhouse.app.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.UserPlanting
import nz.keeleysgreenhouse.app.domain.usecase.ObservePlantingsUseCase
import java.time.LocalDate
import javax.inject.Inject

enum class PlantingStage { SEED, SEEDLING, GROWING, HARVEST }

data class PlantingRow(
    val planting: UserPlanting,
    val crop: Crop,
    val stage: PlantingStage,
    val progress: Float
)

@HiltViewModel
class GardenViewModel @Inject constructor(
    observePlantings: ObservePlantingsUseCase
) : ViewModel() {

    val state: StateFlow<List<PlantingRow>> = observePlantings()
        .map { list -> list.map { toRow(it.planting, it.crop, LocalDate.now()) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    companion object {
        fun toRow(planting: UserPlanting, crop: Crop, today: LocalDate): PlantingRow {
            val sown = LocalDate.parse(planting.sownDate)
            val harvestStart = LocalDate.parse(planting.expectedHarvestStart)
            val harvestEnd = LocalDate.parse(planting.expectedHarvestEnd)
            val transplant = planting.transplantedDate?.let { LocalDate.parse(it) }
                ?: sown.plusDays(((crop.daysSeedToSeedling.first + crop.daysSeedToSeedling.last) / 2).toLong())

            val stage = when {
                today.isBefore(transplant) -> PlantingStage.SEED
                today.isBefore(harvestStart) -> {
                    val midpoint = transplant.plusDays(
                        java.time.temporal.ChronoUnit.DAYS.between(transplant, harvestStart) / 2
                    )
                    if (today.isBefore(midpoint)) PlantingStage.SEEDLING else PlantingStage.GROWING
                }
                !today.isAfter(harvestEnd) -> PlantingStage.HARVEST
                else -> PlantingStage.HARVEST
            }

            val totalDays = java.time.temporal.ChronoUnit.DAYS.between(sown, harvestEnd).coerceAtLeast(1)
            val elapsed = java.time.temporal.ChronoUnit.DAYS.between(sown, today)
                .coerceIn(0, totalDays)
            val progress = elapsed.toFloat() / totalDays.toFloat()

            return PlantingRow(planting, crop, stage, progress)
        }
    }
}
