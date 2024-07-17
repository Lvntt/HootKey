package dev.banger.feature_auth_impl.domain.usecase

interface AuthUseCase {

    suspend operator fun invoke(email: String, password: String)

}