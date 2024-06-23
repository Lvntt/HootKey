package dev.banger.hootkey.presentation.intent

sealed interface DashboardIntent {
    data object LoadCategories : DashboardIntent
    data object LoadNextVaultsPage : DashboardIntent
    data class OpenDeleteDialog(val vaultId: String) : DashboardIntent
    data object DismissDeleteDialog : DashboardIntent
    data object DeleteVault : DashboardIntent
}