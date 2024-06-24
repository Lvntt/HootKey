package dev.banger.hootkey.presentation.state.settings

sealed interface SettingsEffect {
    data object RedirectToAuth : SettingsEffect
    data object ShowError : SettingsEffect
}