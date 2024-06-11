package dev.banger.hootkey.presentation.state.auth

import androidx.annotation.StringRes

data class AuthState(
    val password: String = "",
    val passwordIsValid: Boolean = true,
    val isLoading: Boolean = false,
    @StringRes val errorMessageResId: Int? = null
) {
    val isAuthAllowed: Boolean
        get() = passwordIsValid && password.isNotBlank()
}