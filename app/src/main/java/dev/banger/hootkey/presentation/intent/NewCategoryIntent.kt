package dev.banger.hootkey.presentation.intent

import dev.banger.hootkey.presentation.entity.UiCategoryIcon
import dev.banger.hootkey.presentation.entity.UiTemplateShort

sealed interface NewCategoryIntent {
    data class IconChanged(val icon: UiCategoryIcon) : NewCategoryIntent
    data class NameChanged(val name: String) : NewCategoryIntent
    data class NameFocusChanged(val isFocused: Boolean) : NewCategoryIntent
    data object SelectTemplate : NewCategoryIntent
    data object SelectIcon : NewCategoryIntent
    data class TemplateChanged(val template: UiTemplateShort) : NewCategoryIntent
    data object CreateCategory : NewCategoryIntent
}