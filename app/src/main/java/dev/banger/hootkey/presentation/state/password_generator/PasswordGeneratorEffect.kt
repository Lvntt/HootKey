package dev.banger.hootkey.presentation.state.password_generator

sealed interface PasswordGeneratorEffect {
    data object ShowEmptyCharPoolError : PasswordGeneratorEffect
}