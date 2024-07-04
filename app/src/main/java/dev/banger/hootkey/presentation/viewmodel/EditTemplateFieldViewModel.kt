package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.banger.hootkey.presentation.entity.UiFieldType
import dev.banger.hootkey.presentation.entity.UiTemplateField
import dev.banger.hootkey.presentation.intent.EditTemplateFieldIntent
import dev.banger.hootkey.presentation.state.edit_template_field.EditTemplateFieldEffect
import dev.banger.hootkey.presentation.state.edit_template_field.EditTemplateFieldState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditTemplateFieldViewModel @AssistedInject constructor(
    @Assisted field: UiTemplateField
) : ViewModel() {

    private val stateFlow = MutableStateFlow(EditTemplateFieldState(field = field))
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<EditTemplateFieldEffect>(extraBufferCapacity = 1)
    val effects = effectsFlow.asSharedFlow()

    fun dispatch(intent: EditTemplateFieldIntent) {
        when (intent) {
            is EditTemplateFieldIntent.NameChanged -> onNameChanged(intent.name)
            is EditTemplateFieldIntent.TypeChanged -> onTypeChanged(intent.type)
            EditTemplateFieldIntent.OpenDropdownMenu -> openDropdownMenu()
            EditTemplateFieldIntent.DismissDropdownMenu -> dismissDropdownMenu()
            EditTemplateFieldIntent.EditField -> onEditField()
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

    private fun onEditField() {
        val field = state.value.field
        val processedField = field.copy(
            name = field.name.trim()
        )
        effectsFlow.tryEmit(EditTemplateFieldEffect.EditField(processedField))

        stateFlow.update { EditTemplateFieldState() }
    }

    @AssistedFactory
    interface Factory {
        fun create(field: UiTemplateField): EditTemplateFieldViewModel
    }

    companion object {
        fun factory(
            factory: Factory,
            field: UiTemplateField
        ) : ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return factory.create(field) as T
                }
            }
        }
    }

}