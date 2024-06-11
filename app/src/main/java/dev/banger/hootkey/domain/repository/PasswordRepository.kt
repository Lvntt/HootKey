package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.password.GeneratedPassword
import dev.banger.hootkey.domain.entity.password.PasswordOptions
import dev.banger.hootkey.domain.entity.password.PasswordStrength

interface PasswordRepository {

    fun generatePassword(options: PasswordOptions): GeneratedPassword

    fun checkPasswordStrength(password: String): PasswordStrength

}