package dev.banger.feature_auth_impl.domain.usecase

import dev.banger.feature_auth_impl.domain.repository.AuthRepository

class CheckUserLoggedInUseCase(private val authRepository: AuthRepository) {

    operator fun invoke(): Boolean {
        return authRepository.checkUserLoggedIn()
    }
}