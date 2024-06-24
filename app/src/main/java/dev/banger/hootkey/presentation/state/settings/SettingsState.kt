package dev.banger.hootkey.presentation.state.settings

data class SettingsState(
    val isLoading: Boolean = false,
    val isBiometryOn: Boolean = false,
    val isAutofillOn: Boolean = false,
//    val isCheckForCompromisesOn: Boolean = false,
    val isSyncOn: Boolean = false,
    val isLogoutLoading: Boolean = false
)
