package dev.banger.hootkey.data.repository

import dev.banger.hootkey.domain.entity.password.GeneratedPassword
import dev.banger.hootkey.domain.entity.password.PasswordOptions
import dev.banger.hootkey.domain.entity.password.PasswordStrength
import dev.banger.hootkey.domain.entity.password.PasswordSymbols
import dev.banger.hootkey.domain.entity.password.exception.EmptyCharPoolException
import dev.banger.hootkey.domain.repository.PasswordRepository

class PasswordRepositoryImpl : PasswordRepository {

    override fun generatePassword(options: PasswordOptions): GeneratedPassword {
        var charPool = charArrayOf()
        if (options.hasNumbers) charPool += PasswordSymbols.numbers
        if (options.hasSymbols) charPool += PasswordSymbols.special
        if (options.hasUppercase) charPool += PasswordSymbols.uppercase
        if (options.hasLowercase) charPool += PasswordSymbols.lowercase

        if (charPool.isEmpty()) throw EmptyCharPoolException()

        val password = (1..options.length)
            .map { charPool.random() }
            .joinToString("")

        val strength = checkPasswordStrength(password)

        return GeneratedPassword(password, strength)
    }

    override fun checkPasswordStrength(password: String): PasswordStrength {
        val length = password.length
        val hasNumbers = password.any { it.isDigit() }
        val hasSymbols = password.any { it in PasswordSymbols.special }
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }

        val criteriaMet = listOf(hasNumbers, hasSymbols, hasUppercase, hasLowercase).count { it }

        return when {
            length >= 12 && criteriaMet >= 3 -> PasswordStrength.VERY_STRONG
            length >= 10 && criteriaMet >= 3 -> PasswordStrength.STRONG
            length >= 8 && criteriaMet >= 2 -> PasswordStrength.MEDIUM
            length >= 6 && criteriaMet >= 2 -> PasswordStrength.WEAK
            else -> PasswordStrength.VERY_WEAK
        }
    }

}