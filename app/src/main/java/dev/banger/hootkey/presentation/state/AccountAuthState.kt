package dev.banger.hootkey.presentation.state

import androidx.annotation.StringRes

data class AccountAuthState(
    val email: String = "",
    val password: String = "",
    val emailIsValid: Boolean = true,
    val passwordIsValid: Boolean = true,
    val isLoading: Boolean = false,
    @StringRes val errorMessageResId: Int? = null
) {
    val isAuthAllowed: Boolean
        get() = emailIsValid && passwordIsValid && email.isNotBlank() && password.isNotBlank()
}