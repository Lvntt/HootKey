package dev.banger.hootkey.domain.usecase

class ValidateEmailUseCase(
    private val emailFormatIsCorrect: (String) -> Boolean
) {

    operator fun invoke(email: String): Boolean {
        return email.isNotBlank() && emailFormatIsCorrect(email)
    }

}