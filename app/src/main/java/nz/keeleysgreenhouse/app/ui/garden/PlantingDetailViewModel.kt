package nz.keeleysgreenhouse.app.ui.garden

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.UserPlantingDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.UserPlanting
import nz.keeleysgreenhouse.app.domain.usecase.DeletePlantingUseCase
import nz.keeleysgreenhouse.app.ui.navigation.PlantingDetailArg
import java.time.LocalDate
import javax.inject.Inject

data class PlantingDetailUiState(
    val planting: UserPlanting? = null,
    val crop: Crop? = null
)

@HiltViewModel
class PlantingDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plantingDao: UserPlantingDao,
    private val cropDao: CropDao,
    private val deletePlanting: DeletePlantingUseCase
) : ViewModel() {

    private val plantingId: Long = checkNotNull(savedStateHandle[PlantingDetailArg])

    private val _state = MutableStateFlow(PlantingDetailUiState())
    val state: StateFlow<PlantingDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val planting = plantingDao.getById(plantingId) ?: return@launch
            val crop = cropDao.getById(planting.cropId)
            _state.value = PlantingDetailUiState(planting, crop)
        }
    }

    fun markTransplanted() {
        viewModelScope.launch {
            val current = _state.value.planting ?: return@launch
            val updated = current.copy(transplantedDate = LocalDate.now().toString())
            plantingDao.update(updated)
            _state.value = _state.value.copy(planting = updated)
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val current = _state.value.planting ?: return@launch
            deletePlanting(current)
            onDeleted()
        }
    }
}
