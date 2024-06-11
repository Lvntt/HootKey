package dev.banger.hootkey.domain.entity.category

data class CreateCategoryRequest(
    val icon: CategoryIcon,
    val name: String,
    val templateId: String,
)
