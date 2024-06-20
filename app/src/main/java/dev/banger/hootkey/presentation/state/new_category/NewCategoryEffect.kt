package dev.banger.hootkey.presentation.state.new_category

sealed interface NewCategoryEffect {
    data object GoToTemplates : NewCategoryEffect
    data object GoToIcons : NewCategoryEffect
    data object HandleSuccess : NewCategoryEffect
    data object ShowError : NewCategoryEffect
}