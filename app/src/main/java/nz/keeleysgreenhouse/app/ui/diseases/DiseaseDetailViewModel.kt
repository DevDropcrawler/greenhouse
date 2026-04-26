package nz.keeleysgreenhouse.app.ui.diseases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nz.keeleysgreenhouse.app.domain.usecase.DiseaseDetail
import nz.keeleysgreenhouse.app.domain.usecase.GetDiseaseDetailUseCase
import javax.inject.Inject

data class DiseaseDetailUiState(
    val loading: Boolean = true,
    val detail: DiseaseDetail? = null
)

@HiltViewModel
class DiseaseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDiseaseDetail: GetDiseaseDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DiseaseDetailUiState())
    val state: StateFlow<DiseaseDetailUiState> = _state.asStateFlow()

    init {
        val diseaseId: Int = savedStateHandle.get<Int>("diseaseId") ?: -1
        viewModelScope.launch {
            val detail = if (diseaseId >= 0) getDiseaseDetail(diseaseId) else null
            _state.value = DiseaseDetailUiState(loading = false, detail = detail)
        }
    }
}
