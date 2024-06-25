package dev.banger.hootkey.presentation.intent

sealed interface VaultDetailsIntent {
    data object LoadVault : VaultDetailsIntent
    data object FavoriteVault : VaultDetailsIntent
    data class ChangeFieldVisibility(val fieldIndex: Int) : VaultDetailsIntent
}