package dev.banger.hootkey.presentation.intent

import dev.banger.hootkey.presentation.entity.UiFieldType

sealed interface NewTemplateFieldIntent {
    data class NameChanged(val name: String) : NewTemplateFieldIntent
    data class TypeChanged(val type: UiFieldType) : NewTemplateFieldIntent
    data object OpenDropdownMenu : NewTemplateFieldIntent
    data object DismissDropdownMenu : NewTemplateFieldIntent
    data object AddField : NewTemplateFieldIntent
}