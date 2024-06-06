package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.entity.auth.exception.InvalidCredentialsException
import dev.banger.hootkey.domain.entity.auth.exception.RegistrationCollisionException
import dev.banger.hootkey.domain.usecase.AuthUseCase
import dev.banger.hootkey.domain.usecase.ValidateEmailUseCase
import dev.banger.hootkey.domain.usecase.ValidatePasswordUseCase
import dev.banger.hootkey.presentation.state.auth.AccountAuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountAuthViewModel(
    private val authUseCase: AuthUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AccountAuthState())
    val state = _state.asStateFlow()

    private val _successEventFlow = MutableSharedFlow<Unit>()
    val successEventFlow = _successEventFlow.asSharedFlow()

    fun onEmailChanged(email: String) {
        _state.update {
            it.copy(
                email = email,
                emailIsValid = validateEmailUseCase(email)
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _state.update {
            it.copy(
                password = password,
                passwordIsValid = validatePasswordUseCase(password)
            )
        }
    }

    fun resetErrorMessage() {
        _state.update {
            it.copy(errorMessageResId = null)
        }
    }

    fun authorize() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                authUseCase(state.value.email, state.value.password)
            }.fold(
                onSuccess = {
                    _successEventFlow.emit(Unit)
                    _state.update { it.copy(isLoading = false) }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageResId = when (throwable) {
                                is InvalidCredentialsException -> R.string.invalid_credentials
                                is RegistrationCollisionException -> R.string.collision_error
                                else -> R.string.unknown_error
                            }
                        )
                    }
                }
            )
        }
    }

}