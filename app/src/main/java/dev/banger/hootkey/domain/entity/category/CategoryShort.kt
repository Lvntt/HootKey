package dev.banger.hootkey.domain.entity.category

data class CategoryShort(
    val id: String,
    val icon: CategoryIcon,
    val name: String,
    val templateId: String,
    val vaultsAmount: Int,
    val isCustom: Boolean
)
