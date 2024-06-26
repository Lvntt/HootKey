package dev.banger.hootkey.presentation.state.templates

sealed interface TemplatesEffect {
    data object ShowError : TemplatesEffect
}