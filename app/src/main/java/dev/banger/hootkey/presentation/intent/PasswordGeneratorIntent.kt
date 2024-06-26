package dev.banger.hootkey.presentation.intent

sealed interface PasswordGeneratorIntent {
    data object RegeneratePassword : PasswordGeneratorIntent
    data class ChangeLength(val length: Int) : PasswordGeneratorIntent
    data class ChangeLengthSliderValue(val length: Float) : PasswordGeneratorIntent
    data class ChangeHasNumbers(val hasNumbers: Boolean) : PasswordGeneratorIntent
    data class ChangeHasSymbols(val hasSymbols: Boolean) : PasswordGeneratorIntent
    data class ChangeHasUppercase(val hasUppercase: Boolean) : PasswordGeneratorIntent
    data class ChangeHasLowercase(val hasLowercase: Boolean) : PasswordGeneratorIntent
}