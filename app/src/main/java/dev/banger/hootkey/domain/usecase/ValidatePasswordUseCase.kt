package dev.banger.hootkey.domain.usecase

class ValidatePasswordUseCase {

    operator fun invoke(password: String): Boolean {
        return password.length >= 6
    }

}