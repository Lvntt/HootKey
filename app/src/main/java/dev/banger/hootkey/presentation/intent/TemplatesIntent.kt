package dev.banger.hootkey.presentation.intent

sealed interface TemplatesIntent {
    data object LoadTemplates : TemplatesIntent
}