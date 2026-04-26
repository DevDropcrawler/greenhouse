package nz.keeleysgreenhouse.app.ui.crops

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nz.keeleysgreenhouse.app.domain.usecase.CropDetail
import nz.keeleysgreenhouse.app.domain.usecase.GetCropDetailUseCase
import javax.inject.Inject

data class CropDetailUiState(
    val loading: Boolean = true,
    val detail: CropDetail? = null
)

@HiltViewModel
class CropDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCropDetail: GetCropDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CropDetailUiState())
    val state: StateFlow<CropDetailUiState> = _state.asStateFlow()

    init {
        val cropId: Int = savedStateHandle.get<Int>("cropId") ?: -1
        viewModelScope.launch {
            val detail = if (cropId >= 0) getCropDetail(cropId) else null
            _state.value = CropDetailUiState(loading = false, detail = detail)
        }
    }
}
