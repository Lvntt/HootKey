package dev.banger.hootkey.presentation.state.new_template

sealed interface NewTemplateEffect {
    data object HandleSuccess : NewTemplateEffect
    data object ShowError : NewTemplateEffect
}