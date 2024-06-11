package dev.banger.hootkey.domain.usecase

interface AuthUseCase {

    suspend operator fun invoke(email: String, password: String)

}