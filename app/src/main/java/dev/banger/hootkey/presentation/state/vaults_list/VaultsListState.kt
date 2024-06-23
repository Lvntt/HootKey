package dev.banger.hootkey.presentation.state.vaults_list

import androidx.compose.runtime.Immutable
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.entity.UiFilterType

@Immutable
data class VaultsListState(
    val vaultsPageLoadingState: LceState = LceState.CONTENT,
    val vaults: List<VaultShort> = emptyList(),
    val nextPageKey: String? = null,
    val endReached: Boolean = false,
    val searchQuery: String? = null,
    val uiFilterType: UiFilterType = UiFilterType.ALL,
    val deleteDialogOpenedForVault: VaultShort? = null,
    val isDeletingVault: Boolean = false,
    val deletedVaultIds: List<String> = emptyList(),
    val deletedVaultCategories: List<String> = emptyList()
)