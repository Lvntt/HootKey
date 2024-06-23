package dev.banger.hootkey.presentation.intent

import dev.banger.hootkey.domain.entity.vault.VaultShort

sealed interface DashboardIntent {
    data object LoadCategories : DashboardIntent
    data object LoadNextVaultsPage : DashboardIntent
    data class OpenDeleteDialog(val vault: VaultShort) : DashboardIntent
    data object DismissDeleteDialog : DashboardIntent
    data object DeleteVault : DashboardIntent
    data class AddNewVault(val vaultId: String) : DashboardIntent
    data class IncrementCategoryVaultsCount(val categoryId: String) : DashboardIntent
}