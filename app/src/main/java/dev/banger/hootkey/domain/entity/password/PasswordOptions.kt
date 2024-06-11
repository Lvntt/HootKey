package dev.banger.hootkey.domain.entity.password

data class PasswordOptions(
    val length: Int,
    val hasNumbers: Boolean,
    val hasSymbols: Boolean,
    val hasUppercase: Boolean,
    val hasLowercase: Boolean,
)
