package dev.banger.hootkey.presentation.intent

sealed interface CategoriesIntent {
    data object LoadCategories : CategoriesIntent
}