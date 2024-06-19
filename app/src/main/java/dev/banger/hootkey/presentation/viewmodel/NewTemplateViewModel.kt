package dev.banger.hootkey.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.repository.TemplateRepository
import dev.banger.hootkey.presentation.entity.UiTemplateField
import dev.banger.hootkey.presentation.intent.NewTemplateIntent
import dev.banger.hootkey.presentation.state.new_template.NewTemplateEffect
import dev.banger.hootkey.presentation.state.new_template.NewTemplateState
import dev.banger.hootkey.presentation.ui.utils.toCreateTemplateRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewTemplateViewModel(
    private val templateRepository: TemplateRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private companion object {
        const val TAG = "NewTemplateViewModel"
    }

    private val stateFlow = MutableStateFlow(NewTemplateState())
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<NewTemplateEffect>(extraBufferCapacity = 1)
    val effects = effectsFlow.asSharedFlow()

    fun dispatch(intent: NewTemplateIntent) {
        when (intent) {
            is NewTemplateIntent.NameChanged -> onNameChanged(intent.name)
            NewTemplateIntent.ShowCreateFieldDialog -> showCreateFieldDialog()
            is NewTemplateIntent.ShowEditFieldDialog -> showEditFieldDialog(intent.field)
            NewTemplateIntent.DismissDialog -> dismissDialog()
            is NewTemplateIntent.AddField -> addField(intent.field)
            is NewTemplateIntent.EditField -> editField(intent.field)
            is NewTemplateIntent.MoveField -> moveField(intent.fromIndex, intent.toIndex)
            is NewTemplateIntent.DeleteField -> deleteField(intent.field)
            NewTemplateIntent.CreateTemplate -> createTemplate()
        }
    }

    private fun onNameChanged(name: String) {
        stateFlow.update { it.copy(name = name) }
    }

    private fun showCreateFieldDialog() {
        stateFlow.update { it.copy(isNewFieldDialogShown = true) }
    }

    private fun showEditFieldDialog(field: UiTemplateField) {
        stateFlow.update {
            it.copy(
                isEditFieldDialogShown = true,
                fieldToEdit = field
            )
        }
    }

    private fun dismissDialog() {
        stateFlow.update {
            it.copy(
                isNewFieldDialogShown = false,
                isEditFieldDialogShown = false,
                fieldToEdit = null
            )
        }
    }

    private fun addField(field: UiTemplateField) {
        stateFlow.update {
            it.copy(fields = it.fields + field)
        }
    }

    private fun editField(field: UiTemplateField) {
        stateFlow.update {
            it.copy(
                fields = it.fields.map { oldField ->
                    if (oldField.uuid == field.uuid) {
                        field
                    } else {
                        oldField
                    }
                },
                fieldToEdit = null
            )
        }
    }

    private fun moveField(fromIndex: Int, toIndex: Int) {
            val newList = stateFlow.value.fields
                .toMutableList()
                .apply {
                    add(toIndex - 2, removeAt(fromIndex - 2))
                }
            stateFlow.value = stateFlow.value.copy(
                fields = newList
            )
    }

    private fun deleteField(field: UiTemplateField) {
        stateFlow.update {
            it.copy(fields = it.fields - field)
        }
    }

    private fun createTemplate() {
        viewModelScope.launch(defaultDispatcher) {
            stateFlow.update { it.copy(isLoading = true) }
            runCatching {
                templateRepository.create(
                    state.value.toCreateTemplateRequest()
                )
            }.fold(
                onSuccess = {
                    stateFlow.update { it.copy(isLoading = false) }
                    effectsFlow.tryEmit(NewTemplateEffect.HandleSuccess)
                },
                onFailure = { throwable ->
                    Log.e(TAG, throwable.stackTraceToString())
                    stateFlow.update { it.copy(isLoading = false) }
                    effectsFlow.tryEmit(NewTemplateEffect.ShowError)
                }
            )
        }
    }

}