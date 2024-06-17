package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dev.banger.hootkey.presentation.entity.UiFieldType
import dev.banger.hootkey.presentation.intent.NewTemplateFieldIntent
import dev.banger.hootkey.presentation.state.new_template_field.NewTemplateFieldEffect
import dev.banger.hootkey.presentation.state.new_template_field.NewTemplateFieldState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NewTemplateFieldViewModel : ViewModel() {

    private val stateFlow = MutableStateFlow(NewTemplateFieldState())
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<NewTemplateFieldEffect>(extraBufferCapacity = 1)
    val effects = effectsFlow.asSharedFlow()

    fun dispatch(intent: NewTemplateFieldIntent) {
        when (intent) {
            is NewTemplateFieldIntent.NameChanged -> onNameChanged(intent.name)
            is NewTemplateFieldIntent.TypeChanged -> onTypeChanged(intent.type)
            NewTemplateFieldIntent.OpenDropdownMenu -> openDropdownMenu()
            NewTemplateFieldIntent.DismissDropdownMenu -> dismissDropdownMenu()
            NewTemplateFieldIntent.AddField -> onAddField()
        }
    }

    private fun onNameChanged(name: String) {
        stateFlow.update {
            it.copy(
                field = it.field.copy(
                    name = name
                )
            )
        }
    }

    private fun onTypeChanged(type: UiFieldType) {
        stateFlow.update {
            it.copy(
                field = it.field.copy(
                    type = type
                ),
                isDropdownMenuShown = false
            )
        }
    }

    private fun openDropdownMenu() {
        stateFlow.update { it.copy(isDropdownMenuShown = true) }
    }

    private fun dismissDropdownMenu() {
        stateFlow.update { it.copy(isDropdownMenuShown = false) }
    }

    private fun onAddField() {
        val field = state.value.field
        val processedField = field.copy(
            name = field.name.trim()
        )
        effectsFlow.tryEmit(NewTemplateFieldEffect.AddField(processedField))

        stateFlow.update { NewTemplateFieldState() }
    }

}