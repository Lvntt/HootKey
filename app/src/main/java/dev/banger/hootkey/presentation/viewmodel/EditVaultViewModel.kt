package dev.banger.hootkey.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.domain.repository.VaultRepository
import dev.banger.hootkey.presentation.intent.EditVaultIntent
import dev.banger.hootkey.presentation.state.edit_vault.EditVaultEffect
import dev.banger.hootkey.presentation.state.edit_vault.EditVaultState
import dev.banger.hootkey.presentation.ui.utils.toEditVaultRequest
import dev.banger.hootkey.presentation.ui.utils.toUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditVaultViewModel(
    private val vaultId: String,
    private val categoryRepository: CategoryRepository,
    private val vaultRepository: VaultRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private companion object {
        const val TAG = "EditVaultViewModel"
    }

    private val stateFlow = MutableStateFlow(EditVaultState(vaultId = vaultId))
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<EditVaultEffect>(extraBufferCapacity = 1)
    val effects = effectsFlow.asSharedFlow()

    init {
        loadVault()
    }

    fun dispatch(intent: EditVaultIntent) {
        when (intent) {
            is EditVaultIntent.NameChanged -> onNameChanged(intent.name)
            EditVaultIntent.SelectCategory -> onSelectCategory()
            is EditVaultIntent.CategorySelected -> onCategorySelected(intent.categoryId)
            is EditVaultIntent.FieldValueChanged -> onFieldValueChanged(intent.index, intent.value)
            is EditVaultIntent.FieldFocusChanged -> onFieldFocusChanged(intent.index, intent.isFocused)
            is EditVaultIntent.FieldVisibilityChanged -> onFieldVisibilityChanged(intent.index, intent.isHidden)
            is EditVaultIntent.OpenPasswordGenerator -> onOpenPasswordGenerator(intent.index)
            EditVaultIntent.DismissPasswordGenerator -> onDismissPasswordGenerator()
            is EditVaultIntent.OpenDatePicker -> onOpenDatePicker(intent.index)
            EditVaultIntent.DismissDatePicker -> onDismissDatePicker()
            is EditVaultIntent.DatePicked -> onDatePicked(intent.index, intent.millis)
            EditVaultIntent.EditVault -> editVault()
        }
    }

    private fun loadVault() {
        stateFlow.update { it.copy(isVaultLoading = true) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                vaultRepository.getById(vaultId)
            }.fold(
                onSuccess = { vault ->
                    stateFlow.update {
                        it.copy(
                            isVaultLoading = false,
                            category = vault.category.toUi(fieldValues = vault.fieldValues),
                            name = vault.name,
                            oldCategoryId = vault.category.id
                        )
                    }
                },
                onFailure = { throwable ->
                    Log.e(TAG, throwable.stackTraceToString())
                    stateFlow.update { it.copy(isVaultLoading = false) }
                    effectsFlow.emit(EditVaultEffect.ShowVaultLoadingError)
                }
            )
        }
    }

    private fun onNameChanged(name: String) {
        stateFlow.update { it.copy(name = name) }
    }

    private fun onSelectCategory() {
        effectsFlow.tryEmit(EditVaultEffect.GoToCategories)
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
                    effectsFlow.emit(EditVaultEffect.ShowCategoryLoadingError)
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

    private fun onOpenDatePicker(index: Int) {
        stateFlow.update { it.copy(pickingDateForIndex = index) }
    }

    private fun onDismissDatePicker() {
        stateFlow.update { it.copy(pickingDateForIndex = null) }
    }

    private fun onDatePicked(index: Int, millis: Long) {
        val formattedDate = formatDate(millis)
        stateFlow.update { state ->
            val updatedFields = state.category?.template?.fields?.mapIndexed { i, field ->
                if (i == index)
                    field.copy(
                        valueMillis = millis,
                        value = formattedDate
                    )
                else
                    field
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

    private fun editVault() {
        stateFlow.update { it.copy(isEditLoading = true) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                vaultRepository.edit(stateFlow.value.toEditVaultRequest())
            }.fold(
                onSuccess = { vault ->
                    stateFlow.update { it.copy(isEditLoading = false) }
                    effectsFlow.tryEmit(EditVaultEffect.HandleSuccess(vault.id, vault.category.id))
                },
                onFailure = { throwable ->
                    Log.e(TAG, throwable.stackTraceToString())
                    stateFlow.update { it.copy(isEditLoading = false) }
                    effectsFlow.emit(EditVaultEffect.ShowVaultEditError)
                }
            )
        }
    }

    private fun formatDate(millis: Long): String {
        val locale = Locale.getDefault()
        val date = Date(millis)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", locale)
        return dateFormat.format(date)
    }

}