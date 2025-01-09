package dev.banger.hootkey.presentation.helpers

import dev.banger.hootkey.domain.entity.category.CategoryShort
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.entity.UiCategoryShort
import dev.banger.hootkey.presentation.state.dashboard.DashboardState
import dev.banger.hootkey.presentation.ui.utils.toUi
import javax.inject.Inject

class DashboardStateHelper @Inject constructor() {

    fun getInitialState(): DashboardState = DashboardState()

    fun updateStateByCategoriesLoading(
        oldModel: DashboardState,
    ): DashboardState {
        return oldModel.copy(categoriesLoadingState = LceState.LOADING)
    }

    fun updateStateByCategoriesError(
        oldModel: DashboardState,
    ): DashboardState {
        return oldModel.copy(categoriesLoadingState = LceState.ERROR)
    }

    fun updateStateCategories(
        oldModel: DashboardState,
        categories: List<UiCategoryShort>,
    ): DashboardState {
        return oldModel.copy(
            categories = categories,
            categoriesLoadingState = LceState.CONTENT,
        )
    }

    fun updateStateByVaultPageLoading(
        oldModel: DashboardState,
    ): DashboardState {
        return oldModel.copy(
            vaultsPageLoadingState = LceState.LOADING
        )
    }

    fun updateStateByVaultPageError(
        oldModel: DashboardState,
    ): DashboardState {
        return oldModel.copy(
            vaultsPageLoadingState = LceState.ERROR
        )
    }

    fun updateStateVaultsPage(
        oldModel: DashboardState,
        vaults: List<VaultShort>,
        nextPageKey: String,
        isEndReached: Boolean,
    ): DashboardState {
        return oldModel.copy(
            vaults = oldModel.vaults + vaults,
            nextPageKey = nextPageKey,
            isEndReached = isEndReached,
            vaultsPageLoadingState = LceState.CONTENT
        )
    }

    fun updateStateByDismissingVaultDetails(
        oldModel: DashboardState,
    ): DashboardState {
        return oldModel.copy(vaultDetails = null)
    }

    fun updateStateByOpeningVaultDetails(
        oldModel: DashboardState,
        vault: VaultShort,
    ): DashboardState {
        return oldModel.copy(vaultDetails = vault)
    }

    fun updateStateByChangingCategoriesVaultsCount(
        oldModel: DashboardState,
        deletedCategoryIds: List<String>,
        addedCategoryIds: List<String>,
    ): DashboardState {
        return oldModel.copy(
            categories = oldModel.categories.mapNotNull { category ->
                val newCount = category.vaultsAmount -
                        deletedCategoryIds.count { categoryId -> categoryId == category.id } +
                        addedCategoryIds.count { categoryId -> categoryId == category.id }
                if (newCount <= 0) null
                else category.copy(vaultsAmount = newCount)
            }.sortedWith(
                compareByDescending<UiCategoryShort> { category -> category.vaultsAmount }
                    .thenBy { category -> category.name }
            )
        )
    }

    fun updateStateByAddingNewCategories(
        oldModel: DashboardState,
        newCategoriesShort: List<UiCategoryShort?>,
    ): DashboardState {
        return oldModel.copy(
            categories = (
                    oldModel.categories + newCategoriesShort.mapNotNull { uiCategoryShort -> uiCategoryShort }
                    ).sortedWith(
                    compareByDescending<UiCategoryShort> { category -> category.vaultsAmount }
                        .thenBy { category -> category.name }
                )
        )
    }

    fun updateStateByRemovingDeletedVaults(
        oldModel: DashboardState,
        vaultIds: List<String>,
    ): DashboardState {
        return oldModel.copy(
            vaults = oldModel.vaults.filter { vault ->
                vault.id !in vaultIds
            }
        )
    }

    fun updateStateByUpdatingVaults(
        oldModel: DashboardState,
        vaults: List<VaultShort>,
    ): DashboardState {
        return oldModel.copy(
            vaults = oldModel.vaults.map { vault ->
                vaults.firstOrNull { it.id == vault.id } ?: vault
            }
        )
    }

    fun updateStateByAddingNewVault(
        oldModel: DashboardState,
        vault: VaultShort,
    ): DashboardState {
        return oldModel.copy(
            vaults = listOf(vault) + oldModel.vaults
        )
    }

    fun updateStateByUpdatingVault(
        oldModel: DashboardState,
        vault: VaultShort,
    ): DashboardState {
        return oldModel.copy(
            vaults = oldModel.vaults.map { vaultShort ->
                if (vaultShort.id == vault.id) vault else vaultShort
            }
        )
    }

    fun updateStateByDecrementingCategoryVaultsCount(
        oldModel: DashboardState,
        categoryIndex: Int,
    ): DashboardState {
        val oldCategory = oldModel.categories[categoryIndex]
        val updatedCategory = oldCategory.copy(vaultsAmount = oldCategory.vaultsAmount - 1)
        return oldModel.copy(
            categories = oldModel.categories.toMutableList()
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

    fun updateStateByIncrementingCategoryVaultsCount(
        oldModel: DashboardState,
        categoryIndex: Int,
    ): DashboardState {
        val oldCategory = oldModel.categories[categoryIndex]
        val updatedCategory = oldCategory.copy(vaultsAmount = oldCategory.vaultsAmount + 1)
        return oldModel.copy(
            categories = oldModel.categories.toMutableList()
                .apply { set(categoryIndex, updatedCategory) }
                .sortedWith(
                    compareByDescending<UiCategoryShort> { category -> category.vaultsAmount }
                        .thenBy { category -> category.name }
                )
        )
    }

    fun updateStateByCreatingNewCategoryVault(
        oldModel: DashboardState,
        newCategory: CategoryShort,
    ): DashboardState {
        return oldModel.copy(
            categories = (oldModel.categories + newCategory.toUi()).sortedWith(
                compareByDescending<UiCategoryShort> { category -> category.vaultsAmount }
                    .thenBy { category -> category.name }
            )
        )
    }

    fun updateStateByDeletingVault(
        oldModel: DashboardState,
    ): DashboardState {
        return oldModel.copy(isDeletingVault = true)
    }

    fun updateStateByDeletingVaultError(
        oldModel: DashboardState,
    ): DashboardState {
        return oldModel.copy(isDeletingVault = true)
    }

    fun updateStateByVaultDeleted(
        oldModel: DashboardState,
        vaultToDelete: VaultShort,
    ): DashboardState {
        return oldModel.copy(
            isDeletingVault = false,
            vaults = oldModel.vaults.filter { vault -> vault.id != vaultToDelete.id },
            categories = oldModel.categories
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

    fun updateStateByDismissingDeleteDialog(
        oldModel: DashboardState,
    ): DashboardState {
        return oldModel.copy(deleteDialogOpenedForVault = null)
    }

    fun updateStateByOpeningDeleteDialog(
        oldModel: DashboardState,
        vault: VaultShort
    ): DashboardState {
        return oldModel.copy(deleteDialogOpenedForVault = vault)
    }
}