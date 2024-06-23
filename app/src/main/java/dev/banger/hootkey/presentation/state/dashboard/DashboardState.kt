package dev.banger.hootkey.presentation.state.dashboard

import androidx.compose.runtime.Immutable
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.entity.UiCategoryShort

@Immutable
data class DashboardState(
    val categoriesLoadingState: LceState = LceState.CONTENT,
    val categories: List<UiCategoryShort> = emptyList(),
    val vaultsPageLoadingState: LceState = LceState.CONTENT,
    val vaults: List<VaultShort> = emptyList(),
    val nextPageKey: String? = null,
    val isEndReached: Boolean = false,
    val deleteDialogOpenedForVaultId: String? = null,
    val isDeletingVault: Boolean = false
)