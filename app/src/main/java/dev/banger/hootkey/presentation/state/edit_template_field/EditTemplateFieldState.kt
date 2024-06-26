package dev.banger.hootkey.presentation.state.edit_template_field

import dev.banger.hootkey.presentation.entity.UiTemplateField

data class EditTemplateFieldState(
    val field: UiTemplateField = UiTemplateField(),
    val isDropdownMenuShown: Boolean = false
) {
    val isEditAllowed = field.name.isNotBlank()
}
