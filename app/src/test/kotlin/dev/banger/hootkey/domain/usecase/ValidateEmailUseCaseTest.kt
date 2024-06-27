package dev.banger.hootkey.domain.usecase

import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class ValidateEmailUseCaseTest {

    @Test
    fun `invoke with blank email returns false`() {
        val emailFormatIsCorrect: (String) -> Boolean = { _ -> true }
        val validateEmailUseCase = ValidateEmailUseCase(emailFormatIsCorrect)
        val email = ""

        val actual = validateEmailUseCase(email)

        assertEquals(false, actual)
    }

    @Test
    fun `invoke with correct email returns true`() {
        val emailFormatIsCorrect: (String) -> Boolean = { _ -> true }
        val validateEmailUseCase = ValidateEmailUseCase(emailFormatIsCorrect)
        val email = "test@example.com"

        val actual = validateEmailUseCase(email)

        assertEquals(true, actual)
    }

    @Test
    fun `invoke with incorrect email returns false`() {
        val emailFormatIsCorrect: (String) -> Boolean = { _ -> false }
        val validateEmailUseCase = ValidateEmailUseCase(emailFormatIsCorrect)
        val email = "incorrect-email"

        val actual = validateEmailUseCase(email)

        assertEquals(false, actual)
    }

}