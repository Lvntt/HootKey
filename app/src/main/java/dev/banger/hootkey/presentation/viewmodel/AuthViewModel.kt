package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.usecase.CheckPasswordUseCase
import dev.banger.hootkey.domain.usecase.ValidatePasswordUseCase
import dev.banger.hootkey.presentation.state.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _successEventFlow = MutableSharedFlow<Unit>()
    val successEventFlow = _successEventFlow.asSharedFlow()

    fun onPasswordChanged(password: String) {
        _state.update {
            it.copy(
                password = password, passwordIsValid = validatePasswordUseCase(password)
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
                if (checkPasswordUseCase(state.value.password)) {
                    _successEventFlow.emit(Unit)
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false, errorMessageResId = R.string.invalid_password
                        )
                    }
                }
            }.fold(onSuccess = {}, onFailure = {
                _state.update {
                    it.copy(
                        isLoading = false, errorMessageResId = R.string.unknown_error
                    )
                }
            })
        }
    }

}