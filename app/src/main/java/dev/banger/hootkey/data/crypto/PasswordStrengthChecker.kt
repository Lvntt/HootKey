package dev.banger.hootkey.data.crypto

import dev.banger.hootkey.domain.entity.password.PasswordStrength
import dev.banger.hootkey.domain.entity.password.PasswordSymbols
import javax.inject.Inject

class PasswordStrengthChecker @Inject constructor() {

    fun checkPasswordStrength(password: String): PasswordStrength {
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