package dev.banger.hootkey.presentation.intent

sealed interface EditVaultIntent {
    data object SelectCategory : EditVaultIntent
    data class NameChanged(val name: String) : EditVaultIntent
    data class CategorySelected(val categoryId: String) : EditVaultIntent
    data class FieldValueChanged(val index: Int, val value: String) : EditVaultIntent
    data class FieldVisibilityChanged(val index: Int, val isHidden: Boolean) : EditVaultIntent
    data class FieldFocusChanged(val index: Int, val isFocused: Boolean) : EditVaultIntent
    data class OpenPasswordGenerator(val index: Int) : EditVaultIntent
    data object DismissPasswordGenerator : EditVaultIntent
    data class OpenDatePicker(val index: Int) : EditVaultIntent
    data object DismissDatePicker : EditVaultIntent
    data class DatePicked(val index: Int, val millis: Long) : EditVaultIntent
    data object EditVault : EditVaultIntent
}