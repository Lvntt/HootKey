package dev.banger.hootkey.presentation.entity

data class UiGeneratedPassword(
    val password: String = "",
    val strength: UiPasswordStrength = UiPasswordStrength.VERY_WEAK
)
