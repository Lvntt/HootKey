package dev.banger.hootkey.presentation.state.edit_template_field

import dev.banger.hootkey.presentation.entity.UiTemplateField

sealed interface EditTemplateFieldEffect {
    data class EditField(val field: UiTemplateField) : EditTemplateFieldEffect
}