package dev.banger.feature_auth_impl.domain.usecase

class ValidatePasswordUseCase {

    operator fun invoke(password: String): Boolean {
        return password.length >= 6
    }

}