package dev.banger.hootkey.presentation.entity

data class UiCategoryShort(
    val id: String,
    val icon: UiCategoryIcon,
    val name: String,
    val templateId: String,
    val vaultsAmount: Int,
    val isCustom: Boolean
)
