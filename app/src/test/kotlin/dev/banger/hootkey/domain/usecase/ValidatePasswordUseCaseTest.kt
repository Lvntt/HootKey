package dev.banger.hootkey.domain.usecase

import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class ValidatePasswordUseCaseTest {

    @Test
    fun `invoke with blank password returns false`() {
        val validatePasswordUseCase = ValidatePasswordUseCase()
        val password = ""

        val actual = validatePasswordUseCase(password)

        assertFalse(actual)
    }

    @Test
    fun `invoke with password length 5 returns false`() {
        val validatePasswordUseCase = ValidatePasswordUseCase()
        val password = "12345"

        val actual = validatePasswordUseCase(password)

        assertFalse(actual)
    }

    @Test
    fun `invoke with password length 6 returns true`() {
        val validatePasswordUseCase = ValidatePasswordUseCase()
        val password = "123456"

        val actual = validatePasswordUseCase(password)

        assertTrue(actual)
    }

}