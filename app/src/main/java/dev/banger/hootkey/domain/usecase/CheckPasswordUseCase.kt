package dev.banger.hootkey.domain.usecase

import dev.banger.hootkey.domain.repository.AuthRepository

class CheckPasswordUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(password: String): Boolean {
        return authRepository.checkPassword(password)
    }
}