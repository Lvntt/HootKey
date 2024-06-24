package dev.banger.hootkey.presentation.state.settings

import android.content.Intent

sealed interface SettingsEffect {
    data class ShowAutofillSettings(val intent: Intent) : SettingsEffect
    data object Logout : SettingsEffect
    data object ShowError : SettingsEffect
}