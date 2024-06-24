package dev.banger.hootkey.presentation.viewmodel

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.repository.SettingsRepository
import dev.banger.hootkey.domain.usecase.CheckPasswordUseCase
import dev.banger.hootkey.domain.usecase.ValidatePasswordUseCase
import dev.banger.hootkey.presentation.state.auth.AuthState
import dev.banger.hootkey.presentation.state.auth.BiometricError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val checkPasswordUseCase: CheckPasswordUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private companion object {
        const val TAG = "AuthViewModel"
    }

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _successEventFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val successEventFlow = _successEventFlow.asSharedFlow()

    private val _biometricErrorFlow = MutableSharedFlow<BiometricError>(extraBufferCapacity = 1)
    val biometricErrorFlow = _biometricErrorFlow.asSharedFlow()

    init {
        getBiometrySettings()
    }

    private fun getBiometrySettings() {
        runCatching {
            settingsRepository.isBiometryOn()
        }.fold(
            onSuccess = { isBiometryOn ->
                _state.update {
                    it.copy(isBiometryOn = isBiometryOn)
                }
            },
            onFailure = { throwable ->
                Log.e(TAG, "error getting biometry settings: \n ${throwable.stackTraceToString()}")
            }
        )
    }

    fun onPasswordChanged(password: String) {
        _state.update {
            it.copy(
                password = password, passwordIsValid = validatePasswordUseCase(password)
            )
        }
    }

    fun togglePasswordVisibility() {
        _state.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
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
                    _state.update { it.copy(isLoading = false) }
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

    fun showBiometricPrompt(
        activity: FragmentActivity, title: String, description: String, cancelText: String
    ) {
        val manager = BiometricManager.from(activity)
        val authenticators = BIOMETRIC_STRONG

        val promptInfo =
            BiometricPrompt.PromptInfo.Builder().setTitle(title).setDescription(description)
                .setAllowedAuthenticators(authenticators)

        promptInfo.setNegativeButtonText(
            cancelText
        )

        when (manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                _biometricErrorFlow.tryEmit(BiometricError.BiometricNotAvailable)
                return
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                _biometricErrorFlow.tryEmit(BiometricError.BiometricNotEnrolled)
                return
            }

            else -> Unit
        }

        val prompt = BiometricPrompt(activity, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                _successEventFlow.tryEmit(Unit)
            }
        })
        prompt.authenticate(promptInfo.build())
    }

}