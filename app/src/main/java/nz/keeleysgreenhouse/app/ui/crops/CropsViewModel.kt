package nz.keeleysgreenhouse.app.ui.crops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.CropCategory
import nz.keeleysgreenhouse.app.domain.usecase.ObserveCropsUseCase
import javax.inject.Inject

data class CropsUiState(
    val crops: List<Crop> = emptyList(),
    val selectedCategory: CropCategory? = null
)

@HiltViewModel
class CropsViewModel @Inject constructor(
    observeCrops: ObserveCropsUseCase
) : ViewModel() {

    private val selected = MutableStateFlow<CropCategory?>(null)

    val state: StateFlow<CropsUiState> = combine(
        observeCrops(),
        selected
    ) { crops, category ->
        val filtered = if (category == null) crops else crops.filter { it.category == category }
        CropsUiState(crops = filtered, selectedCategory = category)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CropsUiState())

    fun selectCategory(category: CropCategory?) {
        selected.value = category
    }
}
