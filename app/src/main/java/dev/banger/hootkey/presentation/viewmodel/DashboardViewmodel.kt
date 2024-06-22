package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.entity.vault.FilterType
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.intent.DashboardIntent
import dev.banger.hootkey.presentation.state.dashboard.DashboardState
import dev.banger.hootkey.presentation.ui.utils.toUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewmodel(
    private val categoryRepository: CategoryRepository,
    private val vaultRepository: VaultRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadCategories()
        loadNextVaultsPage()
    }

    fun dispatch(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadCategories -> loadCategories()
            is DashboardIntent.LoadNextVaultsPage -> loadNextVaultsPage()
        }
    }

    private fun loadNextVaultsPage() {
        if (_state.value.vaultsPageLoadingState == LceState.LOADING) return
        _state.value = _state.value.copy(vaultsPageLoadingState = LceState.LOADING)
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                vaultRepository.getAll(FilterType.RECENT, null, _state.value.nextPageKey)
            }.onSuccess { (vaults, nextPageKey, endReached) ->
                _state.update { state ->
                    state.copy(
                        vaults = state.vaults + vaults,
                        nextPageKey = nextPageKey,
                        isEndReached = endReached,
                        vaultsPageLoadingState = LceState.CONTENT
                    )
                }
            }.onFailure {
                if (it is CancellationException) throw it
                _state.update { state -> state.copy(vaultsPageLoadingState = LceState.ERROR) }
            }
        }
    }

    private fun loadCategories() {
        if (_state.value.categoriesLoadingState == LceState.LOADING) return
        _state.update { it.copy(categoriesLoadingState = LceState.LOADING) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                categoryRepository.getAllShort()
            }.onSuccess { categories ->
                val uiCategories = categories.map { it.toUi() }
                _state.update { state ->
                    state.copy(
                        categories = uiCategories, categoriesLoadingState = LceState.CONTENT
                    )
                }
            }.onFailure {
                if (it is CancellationException) throw it
                _state.update { state -> state.copy(categoriesLoadingState = LceState.ERROR) }
            }
        }
    }

}