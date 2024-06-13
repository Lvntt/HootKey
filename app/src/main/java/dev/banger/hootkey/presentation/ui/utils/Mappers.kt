package dev.banger.hootkey.presentation.ui.utils

import dev.banger.hootkey.domain.entity.password.GeneratedPassword
import dev.banger.hootkey.domain.entity.password.PasswordOptions
import dev.banger.hootkey.domain.entity.password.PasswordStrength
import dev.banger.hootkey.presentation.entity.UiGeneratedPassword
import dev.banger.hootkey.presentation.entity.UiPasswordOptions
import dev.banger.hootkey.presentation.entity.UiPasswordStrength

fun UiPasswordOptions.toDomain() = with(this) {
    PasswordOptions(
        length = length,
        hasNumbers = hasNumbers,
        hasSymbols = hasSymbols,
        hasUppercase = hasUppercase,
        hasLowercase = hasLowercase
    )
}

fun PasswordStrength.toUi() = when (this) {
    PasswordStrength.COMPROMISED -> UiPasswordStrength.COMPROMISED
    PasswordStrength.VERY_WEAK -> UiPasswordStrength.VERY_WEAK
    PasswordStrength.WEAK -> UiPasswordStrength.WEAK
    PasswordStrength.MEDIUM -> UiPasswordStrength.MEDIUM
    PasswordStrength.STRONG -> UiPasswordStrength.STRONG
    PasswordStrength.VERY_STRONG -> UiPasswordStrength.VERY_STRONG
}

fun GeneratedPassword.toUi() = with(this) {
    UiGeneratedPassword(
        password = password,
        strength = strength.toUi()
    )
}