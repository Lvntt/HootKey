package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.entity.vault.VaultShort
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
            is VaultsListIntent.OpenDeleteDialog -> openDeleteDialog(intent.vault)
            VaultsListIntent.DeleteVault -> deleteVault()
            VaultsListIntent.DismissDeleteDialog -> dismissDeleteDialog()
            is VaultsListIntent.UpdateVault -> updateVault(
                intent.vaultId,
                intent.newCategoryId,
                intent.oldCategoryId
            )
        }
    }

    private fun updateVault(vaultId: String, newCategoryId: String?, oldCategoryId: String?) {
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                _state.update {
                    it.copy(
                        updatedVaultIds = it.updatedVaultIds + vaultId,
                        deletedVaultCategories = if (oldCategoryId != null) it.deletedVaultCategories + oldCategoryId else it.deletedVaultCategories,
                        addedVaultCategories = if (newCategoryId != null) it.addedVaultCategories + newCategoryId else it.addedVaultCategories
                    )
                }
                if (!categoryId.isNullOrBlank() && newCategoryId != null) null else vaultRepository.getShortById(vaultId)
            }.onSuccess { vault ->
                _state.update {
                    it.copy(
                        vaults =
                        if (vault == null)
                            it.vaults.filter { vaultShort -> vaultShort.id != vaultId }
                        else
                            it.vaults.map { vaultShort ->
                                if (vaultShort.id == vaultId) vault
                                else vaultShort
                            }
                    )
                }
            }.onFailure { throwable ->
                if (throwable is CancellationException) throw throwable
            }
        }
    }

    private fun deleteVault() {
        val vaultToDelete = _state.value.deleteDialogOpenedForVault ?: return
        if (_state.value.isDeletingVault) return
        _state.update { it.copy(isDeletingVault = true) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                vaultRepository.delete(vaultToDelete.id)
            }.onSuccess {
                _state.update {
                    it.copy(
                        isDeletingVault = false,
                        vaults = it.vaults.filter { vault -> vault.id != vaultToDelete.id },
                        deletedVaultIds = it.deletedVaultIds + vaultToDelete.id,
                        deletedVaultCategories = it.deletedVaultCategories + vaultToDelete.categoryId,
                        updatedVaultIds = it.updatedVaultIds.filter { id -> id != vaultToDelete.id }
                    )
                }
                dismissDeleteDialog()
            }.onFailure { throwable ->
                if (throwable is CancellationException) throw throwable
                _state.update { it.copy(isDeletingVault = false) }
            }
        }
    }

    private fun dismissDeleteDialog() {
        if (_state.value.isDeletingVault) return
        _state.update { it.copy(deleteDialogOpenedForVault = null) }
    }

    private fun openDeleteDialog(vault: VaultShort) {
        if (_state.value.deleteDialogOpenedForVault != null || _state.value.isDeletingVault) return
        _state.update { it.copy(deleteDialogOpenedForVault = vault) }
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