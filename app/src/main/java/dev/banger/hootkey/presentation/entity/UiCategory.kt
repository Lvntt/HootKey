package dev.banger.hootkey.presentation.entity

data class UiCategory(
    val id: String,
    val icon: UiCategoryIcon,
    val name: String,
    val template: UiEditableTemplate,
    val vaultsAmount: Int,
    val isCustom: Boolean
)