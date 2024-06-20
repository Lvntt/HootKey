package dev.banger.hootkey.presentation.intent

sealed interface NewVaultIntent {
    data object SelectCategory : NewVaultIntent
    data class NameChanged(val name: String) : NewVaultIntent
    data class CategorySelected(val categoryId: String) : NewVaultIntent
}