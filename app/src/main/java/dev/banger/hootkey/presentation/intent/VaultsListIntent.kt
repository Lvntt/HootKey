package dev.banger.hootkey.presentation.intent

import dev.banger.hootkey.presentation.entity.UiFilterType

sealed interface VaultsListIntent {
    data object LoadVaultsNextPage : VaultsListIntent
    data class ChangeSearchQuery(val query: String) : VaultsListIntent
    data class ChangeFilterType(val filterType: UiFilterType) : VaultsListIntent
}