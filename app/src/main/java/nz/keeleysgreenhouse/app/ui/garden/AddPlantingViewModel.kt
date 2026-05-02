package nz.keeleysgreenhouse.app.ui.garden

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.domain.usecase.AddPlantingUseCase
import nz.keeleysgreenhouse.app.domain.usecase.ObserveCropsUseCase
import java.time.LocalDate
import javax.inject.Inject

data class AddPlantingFormState(
    val cropId: Int? = null,
    val sownDate: LocalDate = LocalDate.now(),
    val location: String = "Greenhouse",
    val bay: String = "",
    val notes: String = ""
) {
    val canSave: Boolean get() = cropId != null
}

@HiltViewModel
class AddPlantingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeCrops: ObserveCropsUseCase,
    private val addPlanting: AddPlantingUseCase
) : ViewModel() {

    val crops: StateFlow<List<Crop>> = observeCrops()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val initialCropId: Int? = savedStateHandle.get<Int>("cropId")?.takeIf { it >= 0 }
    private val _form = MutableStateFlow(AddPlantingFormState(cropId = initialCropId))
    val form: StateFlow<AddPlantingFormState> = _form.asStateFlow()

    private val _saved = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saved: SharedFlow<Unit> = _saved

    fun update(transform: (AddPlantingFormState) -> AddPlantingFormState) {
        _form.value = transform(_form.value)
    }

    fun save() {
        val state = _form.value
        val cropId = state.cropId ?: return
        viewModelScope.launch {
            val combinedLocation = if (state.bay.isBlank()) state.location
            else "${state.location} · ${state.bay}"
            addPlanting(
                cropId = cropId,
                sownDate = state.sownDate,
                location = combinedLocation,
                notes = state.notes
            )
            _saved.emit(Unit)
        }
    }
}
