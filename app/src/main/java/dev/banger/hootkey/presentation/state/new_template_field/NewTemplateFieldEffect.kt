package dev.banger.hootkey.presentation.state.new_template_field

import dev.banger.hootkey.presentation.entity.UiTemplateField

sealed interface NewTemplateFieldEffect {
    data class AddField(val field: UiTemplateField) : NewTemplateFieldEffect
}