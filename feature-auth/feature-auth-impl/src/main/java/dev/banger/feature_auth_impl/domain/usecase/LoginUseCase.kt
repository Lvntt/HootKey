package dev.banger.feature_auth_impl.domain.usecase

import dev.banger.feature_auth_impl.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) : AuthUseCase {

    override suspend operator fun invoke(email: String, password: String) {
        return authRepository.login(email, password)
    }
}