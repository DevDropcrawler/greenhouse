package nz.keeleysgreenhouse.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import nz.keeleysgreenhouse.app.data.dao.SearchHistoryDao
import nz.keeleysgreenhouse.app.data.entity.SearchHistory
import nz.keeleysgreenhouse.app.domain.usecase.SearchAllUseCase
import nz.keeleysgreenhouse.app.domain.usecase.SearchResults
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: SearchResults = SearchResults(),
    val recent: List<SearchHistory> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchAll: SearchAllUseCase,
    private val historyDao: SearchHistoryDao
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    init {
        historyDao.observeRecent()
            .onEach { recent -> _state.value = _state.value.copy(recent = recent) }
            .launchIn(viewModelScope)

        queryFlow
            .debounce(250)
            .distinctUntilChanged()
            .onEach { q -> runSearch(q) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(value: String) {
        _state.value = _state.value.copy(query = value)
        queryFlow.value = value
    }

    fun onSubmit() {
        val q = _state.value.query.trim()
        if (q.isEmpty()) return
        viewModelScope.launch {
            historyDao.record(q, System.currentTimeMillis())
        }
    }

    fun onRecentClick(query: String) {
        onQueryChange(query)
    }

    fun onRecentRemove(query: String) {
        viewModelScope.launch { historyDao.deleteByQuery(query) }
    }

    fun onClearAllRecent() {
        viewModelScope.launch { historyDao.clearAll() }
    }

    fun onClearQuery() {
        onQueryChange("")
    }

    private suspend fun runSearch(q: String) {
        if (q.trim().isEmpty()) {
            _state.value = _state.value.copy(results = SearchResults(), isLoading = false)
            return
        }
        _state.value = _state.value.copy(isLoading = true)
        val results = searchAll(q)
        _state.value = _state.value.copy(results = results, isLoading = false)
        historyDao.record(q.trim(), System.currentTimeMillis())
    }
}
