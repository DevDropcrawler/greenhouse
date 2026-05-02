package nz.keeleysgreenhouse.app.ui.crops

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
    private val getCropDetail: GetCropDetailUseCase,
    private val favouriteDao: FavouriteDao
) : ViewModel() {

    private val cropId: Int = savedStateHandle.get<Int>("cropId") ?: -1

    private val _state = MutableStateFlow(CropDetailUiState())
    val state: StateFlow<CropDetailUiState> = _state.asStateFlow()

    val isFavourite: StateFlow<Boolean> =
        if (cropId >= 0) favouriteDao.observeIsFavourite(FavouriteRefType.CROP, cropId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
        else flowOf(false).stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            val detail = if (cropId >= 0) getCropDetail(cropId) else null
            _state.value = CropDetailUiState(loading = false, detail = detail)
        }
    }

    fun toggleFavourite() {
        if (cropId < 0) return
        viewModelScope.launch {
            val existing = favouriteDao.find(FavouriteRefType.CROP, cropId)
            if (existing != null) favouriteDao.delete(existing)
            else favouriteDao.insert(
                Favourite(refType = FavouriteRefType.CROP, refId = cropId, savedAt = System.currentTimeMillis())
            )
        }
    }
}
