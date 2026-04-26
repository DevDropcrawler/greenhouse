package nz.keeleysgreenhouse.app.ui.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import nz.keeleysgreenhouse.app.domain.usecase.GetMonthCropsUseCase
import nz.keeleysgreenhouse.app.domain.usecase.MonthCrops
import javax.inject.Inject

data class MonthDetailUiState(
    val month: Int = 1,
    val crops: MonthCrops = MonthCrops(1, emptyList(), emptyList(), emptyList())
)

@HiltViewModel
class MonthDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMonthCrops: GetMonthCropsUseCase
) : ViewModel() {

    private val month: Int = savedStateHandle.get<Int>("month")?.coerceIn(1, 12) ?: 1

    val state: StateFlow<MonthDetailUiState> = getMonthCrops(month)
        .let { flow ->
            kotlinx.coroutines.flow.combine(flow, kotlinx.coroutines.flow.flowOf(month)) { c, m ->
                MonthDetailUiState(month = m, crops = c)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MonthDetailUiState(month = month)
        )
}
