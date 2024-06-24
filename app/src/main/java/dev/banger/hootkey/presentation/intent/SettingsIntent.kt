package dev.banger.hootkey.presentation.intent

sealed interface SettingsIntent {
    data class BiometryChanged(val isOn: Boolean) : SettingsIntent
    data class AutofillChanged(val isOn: Boolean) : SettingsIntent
    data class SyncChanged(val isOn: Boolean) : SettingsIntent
    data object Logout : SettingsIntent
}