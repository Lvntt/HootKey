package dev.banger.hootkey.domain.entity.category

data class EditCategoryRequest(
    val id: String,
    val icon: CategoryIcon,
    val name: String,
)
