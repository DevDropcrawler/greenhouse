package nz.keeleysgreenhouse.app.ui.pests

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
    private val getPestDetail: GetPestDetailUseCase,
    private val favouriteDao: FavouriteDao
) : ViewModel() {

    private val pestId: Int = savedStateHandle.get<Int>("pestId") ?: -1

    private val _state = MutableStateFlow(PestDetailUiState())
    val state: StateFlow<PestDetailUiState> = _state.asStateFlow()

    val isFavourite: StateFlow<Boolean> =
        if (pestId >= 0) favouriteDao.observeIsFavourite(FavouriteRefType.PEST, pestId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
        else flowOf(false).stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            val detail = if (pestId >= 0) getPestDetail(pestId) else null
            _state.value = PestDetailUiState(loading = false, detail = detail)
        }
    }

    fun toggleFavourite() {
        if (pestId < 0) return
        viewModelScope.launch {
            val existing = favouriteDao.find(FavouriteRefType.PEST, pestId)
            if (existing != null) favouriteDao.delete(existing)
            else favouriteDao.insert(
                Favourite(refType = FavouriteRefType.PEST, refId = pestId, savedAt = System.currentTimeMillis())
            )
        }
    }
}
