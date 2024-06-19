package dev.banger.hootkey.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.presentation.entity.UiCategoryIcon
import dev.banger.hootkey.presentation.entity.UiTemplateShort
import dev.banger.hootkey.presentation.intent.NewCategoryIntent
import dev.banger.hootkey.presentation.state.new_category.NewCategoryEffect
import dev.banger.hootkey.presentation.state.new_category.NewCategoryState
import dev.banger.hootkey.presentation.ui.utils.toCreateCategoryRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewCategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private companion object {
        const val TAG = "NewCategoryViewModel"
    }

    private val stateFlow = MutableStateFlow(NewCategoryState())
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<NewCategoryEffect>(extraBufferCapacity = 1)
    val effects = effectsFlow.asSharedFlow()

    fun dispatch(intent: NewCategoryIntent) {
        when (intent) {
            is NewCategoryIntent.IconChanged -> onIconChanged(intent.icon)
            is NewCategoryIntent.NameChanged -> onNameChanged(intent.name)
            is NewCategoryIntent.NameFocusChanged -> onNameFocusChanged(intent.isFocused)
            is NewCategoryIntent.TemplateChanged -> onTemplateChanged(intent.template)
            NewCategoryIntent.SelectTemplate -> onSelectTemplate()
            NewCategoryIntent.SelectIcon -> onSelectIcon()
            NewCategoryIntent.CreateCategory -> createCategory()
        }
    }

    private fun onIconChanged(icon: UiCategoryIcon) {
        stateFlow.update { it.copy(icon = icon) }
    }

    private fun onNameChanged(name: String) {
        stateFlow.update { it.copy(name = name) }
    }

    private fun onNameFocusChanged(isFocused: Boolean) {
        stateFlow.update { it.copy(isNameTextFieldFocused = isFocused) }
    }

    private fun onTemplateChanged(template: UiTemplateShort) {
        stateFlow.update { it.copy(template = template) }
    }

    private fun onSelectTemplate() {
        effectsFlow.tryEmit(NewCategoryEffect.GoToTemplates)
    }

    private fun onSelectIcon() {
        effectsFlow.tryEmit(NewCategoryEffect.GoToIcons)
    }

    private fun createCategory() {
        viewModelScope.launch(defaultDispatcher) {
            stateFlow.update { it.copy(isLoading = true) }
            runCatching {
                categoryRepository.create(
                    state.value.toCreateCategoryRequest()
                )
            }.fold(
                onSuccess = {
                    stateFlow.update { it.copy(isLoading = false) }
                    effectsFlow.tryEmit(NewCategoryEffect.HandleSuccess)
                },
                onFailure = { throwable ->
                    Log.e(TAG, throwable.stackTraceToString())
                    stateFlow.update { it.copy(isLoading = false) }
                    effectsFlow.tryEmit(NewCategoryEffect.ShowError)
                }
            )
        }
    }

}