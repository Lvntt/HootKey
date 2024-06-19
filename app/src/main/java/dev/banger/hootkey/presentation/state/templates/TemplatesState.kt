package dev.banger.hootkey.presentation.state.templates

import dev.banger.hootkey.presentation.entity.UiTemplateShort

data class TemplatesState(
    val templates: List<UiTemplateShort> = emptyList(),
    val isLoading: Boolean = false
)
