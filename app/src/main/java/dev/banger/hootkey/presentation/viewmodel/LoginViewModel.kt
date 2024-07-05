package dev.banger.hootkey.presentation.viewmodel

import android.app.Application
import dev.banger.hootkey.domain.usecase.LoginUseCase
import dev.banger.hootkey.domain.usecase.ValidateEmailUseCase
import dev.banger.hootkey.domain.usecase.ValidatePasswordUseCase
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    application: Application,
    loginUseCase: LoginUseCase,
    validateEmailUseCase: ValidateEmailUseCase,
    validatePasswordUseCase: ValidatePasswordUseCase
) : AccountAuthViewModel(application, loginUseCase, validateEmailUseCase, validatePasswordUseCase)