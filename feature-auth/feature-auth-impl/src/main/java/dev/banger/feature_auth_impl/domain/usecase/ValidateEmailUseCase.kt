package dev.banger.feature_auth_impl.domain.usecase

class ValidateEmailUseCase(
    private val emailFormatIsCorrect: (String) -> Boolean
) {

    operator fun invoke(email: String): Boolean {
        return email.isNotBlank() && emailFormatIsCorrect(email)
    }

}