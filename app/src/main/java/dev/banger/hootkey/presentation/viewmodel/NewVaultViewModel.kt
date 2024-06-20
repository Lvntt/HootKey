package dev.banger.hootkey.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.presentation.intent.NewVaultIntent
import dev.banger.hootkey.presentation.state.new_vault.NewVaultEffect
import dev.banger.hootkey.presentation.state.new_vault.NewVaultState
import dev.banger.hootkey.presentation.ui.utils.toUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewVaultViewModel(
    private val categoryRepository: CategoryRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private companion object {
        const val TAG = "NewVaultViewModel"
    }

    private val stateFlow = MutableStateFlow(NewVaultState())
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<NewVaultEffect>(extraBufferCapacity = 1)
    val effects = effectsFlow.asSharedFlow()

    fun dispatch(intent: NewVaultIntent) {
        when (intent) {
            is NewVaultIntent.NameChanged -> onNameChanged(intent.name)
            NewVaultIntent.SelectCategory -> onSelectCategory()
            is NewVaultIntent.CategorySelected -> onCategorySelected(intent.categoryId)
            is NewVaultIntent.FieldValueChanged -> onFieldValueChanged(intent.index, intent.value)
            is NewVaultIntent.FieldFocusChanged -> onFieldFocusChanged(intent.index, intent.isFocused)
            is NewVaultIntent.FieldVisibilityChanged -> onFieldVisibilityChanged(intent.index, intent.isHidden)
            is NewVaultIntent.OpenPasswordGenerator -> onOpenPasswordGenerator(intent.index)
            NewVaultIntent.DismissPasswordGenerator -> onDismissPasswordGenerator()
        }
    }

    private fun onNameChanged(name: String) {
        stateFlow.update { it.copy(name = name) }
    }

    private fun onSelectCategory() {
        effectsFlow.tryEmit(NewVaultEffect.GoToCategories)
    }

    private fun onCategorySelected(categoryId: String) {
        stateFlow.update { it.copy(isCategoryLoading = true) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                categoryRepository.getById(categoryId)
                    ?: throw IllegalStateException("could not find category with id $categoryId")
            }.fold(
                onSuccess = { category ->
                    stateFlow.update {
                        it.copy(
                            isCategoryLoading = false,
                            category = category.toUi()
                        )
                    }
                },
                onFailure = { throwable ->
                    Log.e(TAG, throwable.stackTraceToString())
                    stateFlow.update { it.copy(isCategoryLoading = false) }
                    effectsFlow.emit(NewVaultEffect.ShowCategoryLoadingError)
                }
            )
        }
    }

    private fun onFieldValueChanged(index: Int, value: String) {
        stateFlow.update { state ->
            val updatedFields = state.category?.template?.fields?.mapIndexed { i, field ->
                if (i == index) field.copy(value = value) else field
            }
            if (updatedFields != null) {
                val updatedTemplate = state.category.template.copy(fields = updatedFields)
                val updatedCategory = state.category.copy(template = updatedTemplate)
                state.copy(category = updatedCategory)
            } else {
                state
            }
        }
    }

    private fun onFieldVisibilityChanged(index: Int, isHidden: Boolean) {
        stateFlow.update { state ->
            val updatedFields = state.category?.template?.fields?.mapIndexed { i, field ->
                if (i == index) field.copy(isHidden = isHidden) else field
            }
            if (updatedFields != null) {
                val updatedTemplate = state.category.template.copy(fields = updatedFields)
                val updatedCategory = state.category.copy(template = updatedTemplate)
                state.copy(category = updatedCategory)
            } else {
                state
            }
        }
    }

    private fun onFieldFocusChanged(index: Int, isFocused: Boolean) {
        stateFlow.update { state ->
            val updatedFields = state.category?.template?.fields?.mapIndexed { i, field ->
                if (i == index) field.copy(isFocused = isFocused) else field
            }
            if (updatedFields != null) {
                val updatedTemplate = state.category.template.copy(fields = updatedFields)
                val updatedCategory = state.category.copy(template = updatedTemplate)
                state.copy(category = updatedCategory)
            } else {
                state
            }
        }
    }

    private fun onOpenPasswordGenerator(index: Int) {
        stateFlow.update { it.copy(generatingPasswordForIndex = index) }
    }

    private fun onDismissPasswordGenerator() {
        stateFlow.update { it.copy(generatingPasswordForIndex = null) }
    }

}