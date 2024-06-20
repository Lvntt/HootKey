package dev.banger.hootkey.presentation.state.categories

sealed interface CategoriesEffect {
    data object ShowError : CategoriesEffect
}