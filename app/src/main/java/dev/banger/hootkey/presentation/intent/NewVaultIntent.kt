package dev.banger.hootkey.presentation.intent

sealed interface NewVaultIntent {
    data object SelectCategory : NewVaultIntent
    data class NameChanged(val name: String) : NewVaultIntent
    data class CategorySelected(val categoryId: String) : NewVaultIntent
    data class FieldValueChanged(val index: Int, val value: String) : NewVaultIntent
    data class FieldVisibilityChanged(val index: Int, val isHidden: Boolean) : NewVaultIntent
    data class FieldFocusChanged(val index: Int, val isFocused: Boolean) : NewVaultIntent
    data class OpenPasswordGenerator(val index: Int) : NewVaultIntent
    data object DismissPasswordGenerator : NewVaultIntent
    data class OpenDatePicker(val index: Int) : NewVaultIntent
    data object DismissDatePicker : NewVaultIntent
    data class DatePicked(val index: Int, val millis: Long) : NewVaultIntent
    data object CreateVault : NewVaultIntent
}