package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.entity.category.CategoryShort
import dev.banger.hootkey.domain.entity.vault.FilterType
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.entity.UiCategoryShort
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
            DashboardIntent.DeleteVault -> deleteVault()
            DashboardIntent.DismissDeleteDialog -> dismissDeleteDialog()
            is DashboardIntent.OpenDeleteDialog -> openDeleteDialog(intent.vault)
            is DashboardIntent.AddNewVault -> addNewVault(intent.vaultId)
            is DashboardIntent.IncrementCategoryVaultsCount -> incrementCategoryVaultsCount(intent.categoryId)
            is DashboardIntent.DecrementCategoriesVaultsCount -> decrementCategoriesVaultsCount(intent.categoryIds)
            is DashboardIntent.RemoveDeletedVaults -> removeDeletedVaults(intent.vaultIds)
            is DashboardIntent.DecrementCategoryVaultsCount -> decrementCategoryVaultsCount(intent.categoryId)
            is DashboardIntent.UpdateVault -> updateVault(intent.vaultId)
        }
    }

    private fun decrementCategoriesVaultsCount(categoryIds: List<String>) {
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                _state.update {
                    it.copy(
                        categories = it.categories.map { category ->
                            category.copy(
                                vaultsAmount = category.vaultsAmount - categoryIds.count { categoryId -> categoryId == category.id }
                            )
                        }.sortedWith(
                            compareByDescending<UiCategoryShort> { category -> category.vaultsAmount }
                                .thenBy { category -> category.name }
                        )
                    )
                }
            }
        }
    }

    private fun removeDeletedVaults(vaultIds: List<String>) {
        viewModelScope.launch {
            runCatching {
                _state.update {
                    it.copy(vaults = it.vaults.filter { vault -> vault.id !in vaultIds })
                }
            }
        }
    }


    private fun addNewVault(vaultId: String) {
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                vaultRepository.getShortById(vaultId)
            }.onSuccess { vault ->
                _state.update { it.copy(vaults = listOf(vault) + it.vaults) }
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
                _state.update { it.copy(vaults = it.vaults.map { vaultShort ->
                    if (vaultShort.id == vaultId) vault
                    else vaultShort
                }) }
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
                    val category = _state.value.categories[categoryIndex]
                    val updatedCategory = category.copy(vaultsAmount = category.vaultsAmount - 1)
                    _state.update {
                        it.copy(categories = it.categories.toMutableList()
                            .apply {
                                if (updatedCategory.vaultsAmount == 0) removeAt(categoryIndex)
                                else set(categoryIndex, updatedCategory)
                            }
                            .sortedWith(
                                compareByDescending<UiCategoryShort> { category -> category.vaultsAmount }
                                    .thenBy { category -> category.name }
                            )
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
                    val category = _state.value.categories[categoryIndex]
                    val updatedCategory = category.copy(vaultsAmount = category.vaultsAmount + 1)
                    _state.update {
                        it.copy(categories = it.categories.toMutableList()
                            .apply { set(categoryIndex, updatedCategory) }
                            .sortedWith(
                                compareByDescending<UiCategoryShort> { category -> category.vaultsAmount }
                                    .thenBy { category -> category.name }
                            )
                        )
                    }
                } else {
                    val category =
                        categoryRepository.getShortById(id = categoryId) ?: return@runCatching
                    _state.update {
                        it.copy(
                            categories = (it.categories + category.toUi()).sortedWith(
                                compareBy(
                                    { category -> category.vaultsAmount },
                                    { category -> category.name })
                            )
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
                _state.update {
                    it.copy(isDeletingVault = false,
                        vaults = it.vaults.filter { vault -> vault.id != vaultToDelete.id },
                        categories = it.categories
                            .mapNotNull { category ->
                                if (category.id != vaultToDelete.categoryId) category
                                else if (category.vaultsAmount == 1) null
                                else category.copy(vaultsAmount = category.vaultsAmount - 1)
                            }
                            .sortedWith(
                                compareByDescending<UiCategoryShort> { category -> category.vaultsAmount }
                                    .thenBy { category -> category.name }
                            )
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
                categoryRepository.getAllShort().filter { it.vaultsAmount > 0 }
                    .sortedWith(
                        compareByDescending<CategoryShort> { category -> category.vaultsAmount }
                            .thenBy { category -> category.name }
                    )
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