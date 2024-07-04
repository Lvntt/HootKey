package dev.banger.hootkey.presentation.viewmodel

import android.app.Application
import dev.banger.hootkey.domain.usecase.RegisterUseCase
import dev.banger.hootkey.domain.usecase.ValidateEmailUseCase
import dev.banger.hootkey.domain.usecase.ValidatePasswordUseCase
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    application: Application,
    registerUseCase: RegisterUseCase,
    validateEmailUseCase: ValidateEmailUseCase,
    validatePasswordUseCase: ValidatePasswordUseCase
) : AccountAuthViewModel(application, registerUseCase, validateEmailUseCase, validatePasswordUseCase)