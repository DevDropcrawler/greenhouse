package nz.keeleysgreenhouse.app.ui.pests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nz.keeleysgreenhouse.app.domain.usecase.GetPestDetailUseCase
import nz.keeleysgreenhouse.app.domain.usecase.PestDetail
import javax.inject.Inject

data class PestDetailUiState(
    val loading: Boolean = true,
    val detail: PestDetail? = null
)

@HiltViewModel
class PestDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPestDetail: GetPestDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PestDetailUiState())
    val state: StateFlow<PestDetailUiState> = _state.asStateFlow()

    init {
        val pestId: Int = savedStateHandle.get<Int>("pestId") ?: -1
        viewModelScope.launch {
            val detail = if (pestId >= 0) getPestDetail(pestId) else null
            _state.value = PestDetailUiState(loading = false, detail = detail)
        }
    }
}
