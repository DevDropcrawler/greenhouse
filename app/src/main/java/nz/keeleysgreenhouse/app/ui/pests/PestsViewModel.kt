package nz.keeleysgreenhouse.app.ui.pests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nz.keeleysgreenhouse.app.data.entity.Pest
import nz.keeleysgreenhouse.app.domain.usecase.ObservePestsUseCase
import javax.inject.Inject

data class PestsUiState(
    val pests: List<Pest> = emptyList()
)

@HiltViewModel
class PestsViewModel @Inject constructor(
    observePests: ObservePestsUseCase
) : ViewModel() {

    val state: StateFlow<PestsUiState> = observePests()
        .map { PestsUiState(pests = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PestsUiState())
}
