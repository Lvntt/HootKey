package dev.banger.hootkey.domain.usecase

import dev.banger.hootkey.domain.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String) {
        return authRepository.register(email, password)
    }
}