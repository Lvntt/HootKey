package dev.banger.hootkey.presentation.intent

import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.presentation.entity.UiFilterType

sealed interface VaultsListIntent {
    data object LoadVaultsNextPage : VaultsListIntent
    data class ChangeSearchQuery(val query: String) : VaultsListIntent
    data class ChangeFilterType(val filterType: UiFilterType) : VaultsListIntent
    data class OpenDeleteDialog(val vault: VaultShort) : VaultsListIntent
    data object DismissDeleteDialog : VaultsListIntent
    data object DeleteVault : VaultsListIntent
    data class UpdateVault(
        val vaultId: String,
        val newCategoryId: String?,
        val oldCategoryId: String?
    ) : VaultsListIntent
}