package dev.banger.hootkey.presentation.state.new_category

sealed interface NewCategoryEffect {
    data object GoToTemplates : NewCategoryEffect
    data object GoToIcons : NewCategoryEffect
    data class HandleSuccess(val categoryId: String) : NewCategoryEffect
    data object ShowError : NewCategoryEffect
}