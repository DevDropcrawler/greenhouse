package nz.keeleysgreenhouse.app.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import nz.keeleysgreenhouse.app.data.dao.CropDao
import nz.keeleysgreenhouse.app.data.dao.DiseaseDao
import nz.keeleysgreenhouse.app.data.dao.FavouriteDao
import nz.keeleysgreenhouse.app.data.dao.PestDao
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.data.entity.FavouriteRefType
import nz.keeleysgreenhouse.app.data.entity.Pest
import javax.inject.Inject

data class FavouritesUi(
    val crops: List<Crop> = emptyList(),
    val pests: List<Pest> = emptyList(),
    val diseases: List<Disease> = emptyList()
) {
    val isEmpty: Boolean get() = crops.isEmpty() && pests.isEmpty() && diseases.isEmpty()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FavouritesViewModel @Inject constructor(
    favouriteDao: FavouriteDao,
    cropDao: CropDao,
    pestDao: PestDao,
    diseaseDao: DiseaseDao
) : ViewModel() {

    private val cropsFlow = favouriteDao.observeIdsByType(FavouriteRefType.CROP)
        .flatMapLatest { ids ->
            flow { emit(ids.mapNotNull { cropDao.getById(it) }) }
        }

    private val pestsFlow = favouriteDao.observeIdsByType(FavouriteRefType.PEST)
        .flatMapLatest { ids ->
            flow { emit(ids.mapNotNull { pestDao.getById(it) }) }
        }

    private val diseasesFlow = favouriteDao.observeIdsByType(FavouriteRefType.DISEASE)
        .flatMapLatest { ids ->
            flow { emit(ids.mapNotNull { diseaseDao.getById(it) }) }
        }

    val ui: StateFlow<FavouritesUi> = combine(cropsFlow, pestsFlow, diseasesFlow) { c, p, d ->
        FavouritesUi(crops = c, pests = p, diseases = d)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavouritesUi())
}
