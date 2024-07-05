package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dev.banger.hootkey.domain.entity.password.exception.EmptyCharPoolException
import dev.banger.hootkey.domain.repository.PasswordRepository
import dev.banger.hootkey.presentation.intent.PasswordGeneratorIntent
import dev.banger.hootkey.presentation.state.password_generator.PasswordGeneratorEffect
import dev.banger.hootkey.presentation.state.password_generator.PasswordGeneratorState
import dev.banger.hootkey.presentation.ui.utils.toDomain
import dev.banger.hootkey.presentation.ui.utils.toUi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PasswordGeneratorViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val stateFlow = MutableStateFlow(PasswordGeneratorState())
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<PasswordGeneratorEffect>(extraBufferCapacity = 1)
    val effects = effectsFlow.asSharedFlow()

    init {
        regeneratePassword()
    }

    fun dispatch(intent: PasswordGeneratorIntent) {
        when (intent) {
            PasswordGeneratorIntent.RegeneratePassword -> regeneratePassword()
            is PasswordGeneratorIntent.ChangeLength -> {
                changeLength(intent.length)
                regeneratePassword()
            }
            is PasswordGeneratorIntent.ChangeHasLowercase -> {
                changeHasLowercase(intent.hasLowercase)
                regeneratePassword()
            }
            is PasswordGeneratorIntent.ChangeHasNumbers -> {
                changeHasNumbers(intent.hasNumbers)
                regeneratePassword()
            }
            is PasswordGeneratorIntent.ChangeHasSymbols -> {
                changeHasSymbols(intent.hasSymbols)
                regeneratePassword()
            }
            is PasswordGeneratorIntent.ChangeHasUppercase -> {
                changeHasUppercase(intent.hasUppercase)
                regeneratePassword()
            }
            is PasswordGeneratorIntent.ChangeLengthSliderValue -> changeSliderLengthValue(intent.length)
        }
    }

    private fun regeneratePassword() {
        try {
            val password = passwordRepository
                .generatePassword(state.value.options.toDomain())
                .toUi()
            stateFlow.update { it.copy(password = password) }
        } catch (e: EmptyCharPoolException) {
            effectsFlow.tryEmit(PasswordGeneratorEffect.ShowEmptyCharPoolError)
        }
    }

    private fun changeLength(length: Int) {
        stateFlow.update {
            it.copy(
                options = it.options.copy(
                    length = length
                )
            )
        }
    }

    private fun changeSliderLengthValue(length: Float) {
        stateFlow.update {
            it.copy(lengthSliderValue = length)
        }
    }

    private fun changeHasNumbers(hasNumbers: Boolean) {
        stateFlow.update {
            it.copy(
                options = it.options.copy(
                    hasNumbers = hasNumbers
                )
            )
        }
    }

    private fun changeHasSymbols(hasSymbols: Boolean) {
        stateFlow.update {
            it.copy(
                options = it.options.copy(
                    hasSymbols = hasSymbols
                )
            )
        }
    }

    private fun changeHasUppercase(hasUppercase: Boolean) {
        stateFlow.update {
            it.copy(
                options = it.options.copy(
                    hasUppercase = hasUppercase
                )
            )
        }
    }

    private fun changeHasLowercase(hasLowercase: Boolean) {
        stateFlow.update {
            it.copy(
                options = it.options.copy(
                    hasLowercase = hasLowercase
                )
            )
        }
    }

}