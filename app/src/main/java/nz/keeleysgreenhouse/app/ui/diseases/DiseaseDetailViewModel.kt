package nz.keeleysgreenhouse.app.ui.diseases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nz.keeleysgreenhouse.app.data.dao.FavouriteDao
import nz.keeleysgreenhouse.app.data.entity.Favourite
import nz.keeleysgreenhouse.app.data.entity.FavouriteRefType
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
    private val getDiseaseDetail: GetDiseaseDetailUseCase,
    private val favouriteDao: FavouriteDao
) : ViewModel() {

    private val diseaseId: Int = savedStateHandle.get<Int>("diseaseId") ?: -1

    private val _state = MutableStateFlow(DiseaseDetailUiState())
    val state: StateFlow<DiseaseDetailUiState> = _state.asStateFlow()

    val isFavourite: StateFlow<Boolean> =
        if (diseaseId >= 0) favouriteDao.observeIsFavourite(FavouriteRefType.DISEASE, diseaseId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
        else flowOf(false).stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            val detail = if (diseaseId >= 0) getDiseaseDetail(diseaseId) else null
            _state.value = DiseaseDetailUiState(loading = false, detail = detail)
        }
    }

    fun toggleFavourite() {
        if (diseaseId < 0) return
        viewModelScope.launch {
            val existing = favouriteDao.find(FavouriteRefType.DISEASE, diseaseId)
            if (existing != null) favouriteDao.delete(existing)
            else favouriteDao.insert(
                Favourite(refType = FavouriteRefType.DISEASE, refId = diseaseId, savedAt = System.currentTimeMillis())
            )
        }
    }
}
