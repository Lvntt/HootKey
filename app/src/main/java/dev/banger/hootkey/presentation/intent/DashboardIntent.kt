package dev.banger.hootkey.presentation.intent

sealed interface DashboardIntent {
    data object LoadCategories : DashboardIntent
    data object LoadNextVaultsPage : DashboardIntent
}