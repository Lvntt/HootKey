package dev.banger.hootkey.presentation.entity

data class UiPasswordOptions(
    val length: Int = 20,
    val hasNumbers: Boolean = true,
    val hasSymbols: Boolean = true,
    val hasUppercase: Boolean = true,
    val hasLowercase: Boolean = true,
)
