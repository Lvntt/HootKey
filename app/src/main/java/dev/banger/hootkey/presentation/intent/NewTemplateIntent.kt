package dev.banger.hootkey.presentation.intent

import dev.banger.hootkey.presentation.entity.UiTemplateField

sealed interface NewTemplateIntent {
    data class NameChanged(val name: String) : NewTemplateIntent
    data object ShowCreateFieldDialog : NewTemplateIntent
    data class ShowEditFieldDialog(val field: UiTemplateField) : NewTemplateIntent
    data object DismissDialog : NewTemplateIntent
    data class AddField(val field: UiTemplateField) : NewTemplateIntent
    data class EditField(val field: UiTemplateField) : NewTemplateIntent
    data class MoveField(val fromIndex: Int, val toIndex: Int) : NewTemplateIntent
    // TODO DeleteField?
    data object CreateTemplate : NewTemplateIntent
}