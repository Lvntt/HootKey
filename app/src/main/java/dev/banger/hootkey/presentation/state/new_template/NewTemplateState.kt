package dev.banger.hootkey.presentation.state.new_template

import dev.banger.hootkey.presentation.entity.UiTemplateField

data class NewTemplateState(
    val name: String = "",
    val fields: List<UiTemplateField> = emptyList(),
    val isNewFieldDialogShown: Boolean = false,
    val isEditFieldDialogShown: Boolean = false,
    val fieldToEdit: UiTemplateField? = null,
    val isLoading: Boolean = false
) {
    val isCreationAllowed = name.isNotBlank() && !isLoading
}
