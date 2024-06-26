package dev.banger.hootkey.presentation.state.new_template_field

import dev.banger.hootkey.presentation.entity.UiTemplateField

data class NewTemplateFieldState(
    val field: UiTemplateField = UiTemplateField(),
    val isDropdownMenuShown: Boolean = false
) {
    val isCreationAllowed = field.name.isNotBlank()
}
