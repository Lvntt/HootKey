package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.repository.VaultRepository
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.entity.UiFilterType
import dev.banger.hootkey.presentation.intent.VaultsListIntent
import dev.banger.hootkey.presentation.state.vaults_list.VaultsListState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class VaultsListViewModel(
    private val vaultRepository: VaultRepository,
    private val defaultDispatcher: CoroutineDispatcher,
    private val categoryId: String?
) : ViewModel() {

    private val _state = MutableStateFlow(VaultsListState())
    val state = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var latestLoadingJob: Job? = null

    init {
        _searchQuery.debounce(1000).onEach {
            changeSearchQuery(it)
        }.launchIn(viewModelScope)
    }

    fun dispatch(intent: VaultsListIntent) {
        when (intent) {
            is VaultsListIntent.ChangeFilterType -> changeFilterType(intent.filterType)
            is VaultsListIntent.ChangeSearchQuery -> _searchQuery.value = intent.query
            VaultsListIntent.LoadVaultsNextPage -> loadVaultsNextPage()
        }
    }

    private fun loadVaultsNextPage() {
        if (state.value.vaultsPageLoadingState == LceState.LOADING) return
        if (latestLoadingJob != null && latestLoadingJob?.isCompleted == false) return
        _state.value = _state.value.copy(vaultsPageLoadingState = LceState.LOADING)
        latestLoadingJob = viewModelScope.launch(defaultDispatcher) {
            runCatching {
                if (categoryId.isNullOrBlank()) {
                    vaultRepository.getAll(
                        query = state.value.searchQuery,
                        filter = state.value.uiFilterType.filterType,
                        pageKey = state.value.nextPageKey
                    )
                } else {
                    vaultRepository.getAllByCategory(
                        categoryId = categoryId,
                        query = state.value.searchQuery,
                        filter = state.value.uiFilterType.filterType,
                        pageKey = state.value.nextPageKey
                    )
                }
            }.onSuccess { (vaults, nextPageKey, endReached) ->
                _state.update {
                    it.copy(
                        vaultsPageLoadingState = LceState.CONTENT,
                        vaults = it.vaults + vaults,
                        nextPageKey = nextPageKey,
                        endReached = endReached
                    )
                }
            }.onFailure { throwable ->
                if (throwable is CancellationException) throw throwable
                _state.update {
                    it.copy(vaultsPageLoadingState = LceState.ERROR)
                }
            }
        }
    }

    private fun changeSearchQuery(query: String) {
        _state.update {
            it.copy(searchQuery = query.takeIf { query -> query.isNotBlank() })
        }
        resetPagination()
    }

    private fun changeFilterType(filterType: UiFilterType) {
        _state.update {
            it.copy(uiFilterType = filterType)
        }
        resetPagination()
    }

    private fun resetPagination() {
        viewModelScope.launch(defaultDispatcher) {
            latestLoadingJob?.cancelAndJoin()
            _state.update {
                it.copy(
                    vaultsPageLoadingState = LceState.CONTENT,
                    vaults = emptyList(),
                    nextPageKey = null,
                    endReached = false
                )
            }
            loadVaultsNextPage()
        }
    }

}