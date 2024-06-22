package dev.banger.hootkey.presentation.state.dashboard

import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.entity.UiCategoryShort

data class DashboardState(
    val categoriesLoadingState: LceState = LceState.CONTENT,
    val categories: List<UiCategoryShort> = emptyList(),
    val vaultsPageLoadingState: LceState = LceState.CONTENT,
    val vaults: List<VaultShort> = emptyList(),
    val nextPageKey: String? = null,
    val isEndReached: Boolean = false
)