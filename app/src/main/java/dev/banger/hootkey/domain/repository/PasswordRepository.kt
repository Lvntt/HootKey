package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.password.GeneratedPassword
import dev.banger.hootkey.domain.entity.password.PasswordHealthScore
import dev.banger.hootkey.domain.entity.password.PasswordOptions
import dev.banger.hootkey.domain.entity.password.PasswordStrength
import kotlinx.coroutines.flow.StateFlow

interface PasswordRepository {

    val passwordHealthScore: StateFlow<PasswordHealthScore>

    suspend fun calculatePasswordHealthScore()

    fun generatePassword(options: PasswordOptions): GeneratedPassword

    fun checkPasswordStrength(password: String): PasswordStrength

}