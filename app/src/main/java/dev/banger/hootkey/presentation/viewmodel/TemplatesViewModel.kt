package dev.banger.hootkey.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.di.qualifiers.IoDispatcher
import dev.banger.hootkey.domain.repository.TemplateRepository
import dev.banger.hootkey.presentation.intent.TemplatesIntent
import dev.banger.hootkey.presentation.state.templates.TemplatesEffect
import dev.banger.hootkey.presentation.state.templates.TemplatesState
import dev.banger.hootkey.presentation.ui.utils.toUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class TemplatesViewModel @Inject constructor(
    private val templateRepository: TemplateRepository,
    @IoDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private companion object {
        const val TAG = "TemplatesViewModel"
    }

    private val stateFlow = MutableStateFlow(TemplatesState())
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<TemplatesEffect>(extraBufferCapacity = 1)
    val effects = effectsFlow.asSharedFlow()

    init {
        loadTemplates()
    }

    fun dispatch(intent: TemplatesIntent) {
        when (intent) {
            TemplatesIntent.LoadTemplates -> loadTemplates()
        }
    }

    private fun loadTemplates() {
        viewModelScope.launch(defaultDispatcher) {
            stateFlow.update { it.copy(isLoading = true) }
            runCatching {
                templateRepository.getAllShort()
            }.fold(
                onSuccess = { templates ->
                    val uiTemplates = templates.map { it.toUi() }
                    stateFlow.update {
                        it.copy(
                            templates = uiTemplates,
                            isLoading = false
                        )
                    }
                },
                onFailure = { throwable ->
                    Log.e(TAG, throwable.stackTraceToString())
                    stateFlow.update { it.copy(isLoading = false) }
                    effectsFlow.emit(TemplatesEffect.ShowError)
                }
            )
        }
    }

}