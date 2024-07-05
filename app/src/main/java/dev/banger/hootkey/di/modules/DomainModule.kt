package dev.banger.hootkey.di.modules

import android.util.Patterns
import dagger.Module
import dagger.Provides
import dev.banger.hootkey.domain.repository.AuthRepository
import dev.banger.hootkey.domain.usecase.CheckPasswordUseCase
import dev.banger.hootkey.domain.usecase.CheckUserLoggedInUseCase
import dev.banger.hootkey.domain.usecase.LoginUseCase
import dev.banger.hootkey.domain.usecase.RegisterUseCase
import dev.banger.hootkey.domain.usecase.ValidateEmailUseCase
import dev.banger.hootkey.domain.usecase.ValidatePasswordUseCase

@Module
class DomainModule {

    @Provides
    fun provideRegisterUseCase(
        authRepository: AuthRepository
    ): RegisterUseCase = RegisterUseCase(authRepository)

    @Provides
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase = LoginUseCase(authRepository)

    @Provides
    fun provideCheckUserLoggedInUseCase(
        authRepository: AuthRepository
    ): CheckUserLoggedInUseCase = CheckUserLoggedInUseCase(authRepository)

    @Provides
    fun provideValidateEmailUseCase(): ValidateEmailUseCase = ValidateEmailUseCase(
        emailFormatIsCorrect = {
            Patterns.EMAIL_ADDRESS.matcher(
                it
            ).matches()
        }
    )

    @Provides
    fun provideValidatePasswordUseCase(): ValidatePasswordUseCase = ValidatePasswordUseCase()

    @Provides
    fun provideCheckPasswordUseCase(
        authRepository: AuthRepository
    ): CheckPasswordUseCase = CheckPasswordUseCase(authRepository)
}