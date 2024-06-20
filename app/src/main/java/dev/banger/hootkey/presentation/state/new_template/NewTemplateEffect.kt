package dev.banger.hootkey.presentation.state.new_template

sealed interface NewTemplateEffect {
    data class HandleSuccess(val templateId: String) : NewTemplateEffect
    data object ShowError : NewTemplateEffect
}