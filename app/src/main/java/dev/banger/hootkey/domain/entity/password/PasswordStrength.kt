package dev.banger.hootkey.domain.entity.password

enum class PasswordStrength {
    COMPROMISED,
    VERY_WEAK,
    WEAK,
    MEDIUM,
    STRONG,
    VERY_STRONG,
}