package dev.banger.hootkey.domain.usecase

import dev.banger.hootkey.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) : AuthUseCase {

    override suspend operator fun invoke(email: String, password: String) {
        return authRepository.login(email, password)
    }
}