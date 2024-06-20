package dev.banger.hootkey.presentation.state.categories

import dev.banger.hootkey.presentation.entity.UiCategoryShort

data class CategoriesState(
    val categories: List<UiCategoryShort> = emptyList(),
    val isLoading: Boolean = false
)
