package dev.banger.hootkey.presentation.state.password_generator

import dev.banger.hootkey.presentation.entity.UiGeneratedPassword
import dev.banger.hootkey.presentation.entity.UiPasswordOptions

data class PasswordGeneratorState(
    val password: UiGeneratedPassword = UiGeneratedPassword(),
    val options: UiPasswordOptions = UiPasswordOptions(),
    val lengthSliderValue: Float = 20f
)
