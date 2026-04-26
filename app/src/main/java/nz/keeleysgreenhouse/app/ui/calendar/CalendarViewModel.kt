package nz.keeleysgreenhouse.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nz.keeleysgreenhouse.app.domain.usecase.GetAllMonthsCropsUseCase
import nz.keeleysgreenhouse.app.domain.usecase.MonthSummary
import java.time.LocalDate
import javax.inject.Inject

data class CalendarUiState(
    val months: List<MonthSummary> = emptyList(),
    val currentMonth: Int = LocalDate.now().monthValue
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    getAllMonths: GetAllMonthsCropsUseCase
) : ViewModel() {

    val state: StateFlow<CalendarUiState> = getAllMonths()
        .map { CalendarUiState(months = it, currentMonth = LocalDate.now().monthValue) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())
}
