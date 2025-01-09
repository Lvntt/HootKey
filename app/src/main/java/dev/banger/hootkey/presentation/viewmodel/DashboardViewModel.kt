package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.di.qualifiers.IoDispatcher
import dev.banger.hootkey.domain.entity.category.CategoryShort
import dev.banger.hootkey.domain.entity.vault.FilterType
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.PasswordRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.helpers.DashboardStateHelper
import dev.banger.hootkey.presentation.intent.DashboardIntent
import dev.banger.hootkey.presentation.ui.utils.toUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val vaultRepository: VaultRepository,
    private val passwordRepository: PasswordRepository,
    @IoDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val stateHelper: DashboardStateHelper,
) : ViewModel() {

    val passwordHealthScore = passwordRepository.passwordHealthScore

    private val _state = MutableStateFlow(stateHelper.getInitialState())
    val state = _state.asStateFlow()

    init {
        loadCategories()
        loadNextVaultsPage()
        viewModelScope.launch(defaultDispatcher) {
            passwordRepository.calculatePasswordHealthScore()
        }
    }

    fun dispatch(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadCategories -> loadCategories()
            is DashboardIntent.LoadNextVaultsPage -> loadNextVaultsPage()
            DashboardIntent.DeleteVault -> deleteVault()
            DashboardIntent.DismissDeleteDialog -> dismissDeleteDialog()
            is DashboardIntent.OpenDeleteDialog -> openDeleteDialog(intent.vault)
            is DashboardIntent.AddNewVault -> addNewVault(intent.vaultId)
            is DashboardIntent.IncrementCategoryVaultsCount -> incrementCategoryVaultsCount(intent.categoryId)
            is DashboardIntent.ChangeCategoriesVaultsCount -> changeCategoriesVaultsCount(
                intent.deletedCategoryIds,
                intent.addedCategoryIds
            )

            is DashboardIntent.RemoveDeletedVaults -> removeDeletedVaults(intent.vaultIds)
            is DashboardIntent.DecrementCategoryVaultsCount -> decrementCategoryVaultsCount(intent.categoryId)
            is DashboardIntent.UpdateVault -> updateVault(intent.vaultId)
            is DashboardIntent.UpdateVaults -> updateVaults(intent.vaultIds)
            DashboardIntent.DismissVaultDetails -> dismissVaultDetails()
            is DashboardIntent.OpenVaultDetails -> openVaultDetails(intent.vault)
        }
    }

    private fun dismissVaultDetails() {
        _state.update { stateHelper.updateStateByDismissingVaultDetails(it) }
    }

    private fun openVaultDetails(vault: VaultShort) {
        _state.update { stateHelper.updateStateByOpeningVaultDetails(it, vault) }
    }

    private fun changeCategoriesVaultsCount(
        deletedCategoryIds: List<String>,
        addedCategoryIds: List<String>
    ) {
        if (deletedCategoryIds.isEmpty() && addedCategoryIds.isEmpty()) return
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                val newCategories =
                    addedCategoryIds.toSet().minus(_state.value.categories.map { it.id }.toSet())
                _state.update { state ->
                    stateHelper.updateStateByChangingCategoriesVaultsCount(
                        oldModel = state,
                        deletedCategoryIds = deletedCategoryIds,
                        addedCategoryIds = addedCategoryIds,
                    )
                }
                if (newCategories.isNotEmpty()) {
                    val newCategoryShorts =
                        newCategories.map { categoryRepository.getShortById(it)?.toUi() }
                    _state.update { stateHelper.updateStateByAddingNewCategories(it, newCategoryShorts) }
                }
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    private fun removeDeletedVaults(vaultIds: List<String>) {
        viewModelScope.launch {
            runCatching {
                _state.update { stateHelper.updateStateByRemovingDeletedVaults(it, vaultIds) }
            }
        }
    }

    private fun updateVaults(vaultIds: List<String>) {
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                vaultRepository.getShortByIds(vaultIds)
            }.onSuccess { vaults ->
                _state.update { stateHelper.updateStateByUpdatingVaults(it, vaults) }
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }


    private fun addNewVault(vaultId: String) {
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                vaultRepository.getShortById(vaultId)
            }.onSuccess { vault ->
                _state.update { stateHelper.updateStateByAddingNewVault(it, vault) }
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    private fun updateVault(vaultId: String) {
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                vaultRepository.getShortById(vaultId)
            }.onSuccess { vault ->
                _state.update { stateHelper.updateStateByUpdatingVault(it, vault) }
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    private fun decrementCategoryVaultsCount(categoryId: String) {
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                val categoryIndex = _state.value.categories.indexOfFirst { it.id == categoryId }
                if (categoryIndex != -1) {
                    _state.update { state ->
                        stateHelper.updateStateByDecrementingCategoryVaultsCount(
                            oldModel = state,
                            categoryIndex = categoryIndex,
                        )
                    }
                }
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    private fun incrementCategoryVaultsCount(categoryId: String) {
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                val categoryIndex = _state.value.categories.indexOfFirst { it.id == categoryId }
                if (categoryIndex != -1) {
                    _state.update { state ->
                        stateHelper.updateStateByIncrementingCategoryVaultsCount(
                            oldModel = state,
                            categoryIndex = categoryIndex,
                        )
                    }
                } else {
                    val category = categoryRepository.getShortById(id = categoryId) ?: return@runCatching
                    _state.update { state ->
                        stateHelper.updateStateByCreatingNewCategoryVault(
                            oldModel = state,
                            newCategory = category,
                        )
                    }
                }
            }.onFailure {
                if (it is CancellationException) throw it
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
                _state.update { stateHelper.updateStateByVaultDeleted(it, vaultToDelete) }
                dismissDeleteDialog()
            }.onFailure { throwable ->
                if (throwable is CancellationException) throw throwable
                _state.update { stateHelper.updateStateByDeletingVaultError(it) }
            }
        }
    }

    private fun dismissDeleteDialog() {
        if (_state.value.isDeletingVault) return
        _state.update { stateHelper.updateStateByDismissingDeleteDialog(it) }
    }

    private fun openDeleteDialog(vault: VaultShort) {
        if (_state.value.deleteDialogOpenedForVault != null || _state.value.isDeletingVault) return
        _state.update { stateHelper.updateStateByOpeningDeleteDialog(it, vault) }
    }

    private fun loadNextVaultsPage() {
        if (_state.value.vaultsPageLoadingState == LceState.LOADING) return
        _state.update { stateHelper.updateStateByVaultPageLoading(it) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                vaultRepository.getAll(FilterType.RECENT, null, _state.value.nextPageKey)
            }.onSuccess { (vaults, nextPageKey, endReached) ->
                _state.update { state ->
                    stateHelper.updateStateVaultsPage(
                        oldModel = state,
                        vaults = vaults,
                        nextPageKey = nextPageKey,
                        isEndReached = endReached,
                    )
                }
            }.onFailure {
                if (it is CancellationException) throw it
                _state.update { state -> stateHelper.updateStateByVaultPageError(state) }
            }
        }
    }

    private fun loadCategories() {
        if (_state.value.categoriesLoadingState == LceState.LOADING) return
        _state.update { stateHelper.updateStateByCategoriesLoading(it) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                categoryRepository.getAllShort().filter { it.vaultsAmount > 0 }
                    .sortedWith(
                        compareByDescending<CategoryShort> { category -> category.vaultsAmount }
                            .thenBy { category -> category.name }
                    )
            }.onSuccess { categories ->
                val uiCategories = categories.map { it.toUi() }
                _state.update { stateHelper.updateStateCategories(it, uiCategories) }
            }.onFailure {
                if (it is CancellationException) throw it
                _state.update { state -> stateHelper.updateStateByCategoriesError(state) }
            }
        }
    }

}