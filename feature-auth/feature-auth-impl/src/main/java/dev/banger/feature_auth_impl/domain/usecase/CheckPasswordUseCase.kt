package dev.banger.feature_auth_impl.domain.usecase

import dev.banger.feature_auth_impl.domain.repository.AuthRepository

class CheckPasswordUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(password: String): Boolean {
        return authRepository.checkPassword(password)
    }
}