package dev.banger.hootkey.presentation.state.new_category

import dev.banger.hootkey.presentation.entity.UiCategoryIcon
import dev.banger.hootkey.presentation.entity.UiTemplateShort

data class NewCategoryState(
    val icon: UiCategoryIcon = UiCategoryIcon.EMAIL,
    val name: String = "",
    val isNameTextFieldFocused: Boolean = false,
    val template: UiTemplateShort? = null,
    val isLoading: Boolean = false
) {
    val isCreationAllowed = name.isNotBlank() && template != null && !isLoading
}