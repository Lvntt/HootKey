package dev.banger.hootkey.domain.usecase

import dev.banger.hootkey.domain.repository.AuthRepository

class CheckUserLoggedInUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(): Boolean {
        return authRepository.checkUserLoggedIn()
    }
}