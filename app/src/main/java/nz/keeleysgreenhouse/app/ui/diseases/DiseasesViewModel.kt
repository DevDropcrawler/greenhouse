package nz.keeleysgreenhouse.app.ui.diseases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.domain.usecase.ObserveDiseasesUseCase
import javax.inject.Inject

data class DiseasesUiState(
    val diseases: List<Disease> = emptyList()
)

@HiltViewModel
class DiseasesViewModel @Inject constructor(
    observeDiseases: ObserveDiseasesUseCase
) : ViewModel() {

    val state: StateFlow<DiseasesUiState> = observeDiseases()
        .map { DiseasesUiState(diseases = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DiseasesUiState())
}
