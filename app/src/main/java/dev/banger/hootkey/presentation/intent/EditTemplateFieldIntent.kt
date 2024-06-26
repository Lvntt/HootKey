package dev.banger.hootkey.presentation.intent

import dev.banger.hootkey.presentation.entity.UiFieldType

sealed interface EditTemplateFieldIntent {
    data class NameChanged(val name: String) : EditTemplateFieldIntent
    data class TypeChanged(val type: UiFieldType) : EditTemplateFieldIntent
    data object OpenDropdownMenu : EditTemplateFieldIntent
    data object DismissDropdownMenu : EditTemplateFieldIntent
    data object EditField : EditTemplateFieldIntent
}