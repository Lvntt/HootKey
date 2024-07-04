package dev.banger.hootkey.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.di.qualifiers.IoDispatcher
import dev.banger.hootkey.domain.repository.CategoryRepository
import dev.banger.hootkey.presentation.intent.CategoriesIntent
import dev.banger.hootkey.presentation.state.categories.CategoriesEffect
import dev.banger.hootkey.presentation.state.categories.CategoriesState
import dev.banger.hootkey.presentation.ui.utils.toUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    @IoDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private companion object {
        const val TAG = "CategoriesViewModel"
    }

    private val stateFlow = MutableStateFlow(CategoriesState())
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<CategoriesEffect>(extraBufferCapacity = 1)
    val effects = effectsFlow.asSharedFlow()

    init {
        loadCategories()
    }

    fun dispatch(intent: CategoriesIntent) {
        when (intent) {
            CategoriesIntent.LoadCategories -> loadCategories()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch(defaultDispatcher) {
            stateFlow.update { it.copy(isLoading = true) }
            runCatching {
                categoryRepository.getAllShort()
            }.fold(
                onSuccess = { categories ->
                    val uiCategories = categories.map { it.toUi() }
                    stateFlow.update {
                        it.copy(
                            categories = uiCategories,
                            isLoading = false
                        )
                    }
                },
                onFailure = { throwable ->
                    Log.e(TAG, throwable.stackTraceToString())
                    stateFlow.update { it.copy(isLoading = false) }
                    effectsFlow.emit(CategoriesEffect.ShowError)
                }
            )
        }
    }

}