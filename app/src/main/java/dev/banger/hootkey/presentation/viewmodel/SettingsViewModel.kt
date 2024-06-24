package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.repository.SettingsRepository
import dev.banger.hootkey.presentation.intent.SettingsIntent
import dev.banger.hootkey.presentation.state.settings.SettingsEffect
import dev.banger.hootkey.presentation.state.settings.SettingsState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val stateFlow = MutableStateFlow(SettingsState())
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<SettingsEffect>(extraBufferCapacity = 1)
    val effects = effectsFlow.asSharedFlow()

    init {
        loadUserSettings()
    }

    fun dispatch(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.BiometryChanged -> onBiometryChanged(intent.isOn)
            is SettingsIntent.AutofillChanged -> onAutofillChanged(intent.isOn)
            is SettingsIntent.SyncChanged -> onSyncChanged(intent.isOn)
            SettingsIntent.Logout -> TODO()
        }
    }

    private fun loadUserSettings() {
        stateFlow.update { it.copy(isLoading = true) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                settingsRepository.getUserSettings()
            }.fold(
                onSuccess = { userSettings ->
                    stateFlow.update {
                        it.copy(
                            isLoading = false,
                            isBiometryOn = userSettings.isBiometricsOn,
                            isAutofillOn = userSettings.isAutofillOn,
                            isSyncOn = userSettings.isSyncOn
                        )
                    }
                },
                onFailure = {
                    stateFlow.update { it.copy(isLoading = false) }
                    effectsFlow.tryEmit(SettingsEffect.ShowError)
                }
            )
        }
    }

    private fun onBiometryChanged(isOn: Boolean) {
        val isBiometryOnInitial = stateFlow.value.isBiometryOn
        stateFlow.update { it.copy(isBiometryOn = isOn) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                if (isOn) {
                    settingsRepository.enableBiometry()
                } else {
                    settingsRepository.disableBiometry()
                }
            }.fold(
                onSuccess = {},
                onFailure = {
                    stateFlow.update { it.copy(isBiometryOn = isBiometryOnInitial) }
                    effectsFlow.tryEmit(SettingsEffect.ShowError)
                }
            )
        }
    }

    private fun onAutofillChanged(isOn: Boolean) {
        val isAutofillOnInitial = stateFlow.value.isAutofillOn
        stateFlow.update { it.copy(isAutofillOn = isOn) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                if (isOn) {
                    TODO()
                } else {
                    TODO()
                }
            }.fold(
                onSuccess = {},
                onFailure = {
                    stateFlow.update { it.copy(isAutofillOn = isAutofillOnInitial) }
                    effectsFlow.tryEmit(SettingsEffect.ShowError)
                }
            )
        }
    }

    private fun onSyncChanged(isOn: Boolean) {
        val isSyncOnInitial = stateFlow.value.isSyncOn
        stateFlow.update { it.copy(isSyncOn = isOn) }
        viewModelScope.launch(defaultDispatcher) {
            runCatching {
                if (isOn) {
                    settingsRepository.goOnline()
                } else {
                    settingsRepository.goOffline()
                }
            }.fold(
                onSuccess = {},
                onFailure = {
                    stateFlow.update { it.copy(isSyncOn = isSyncOnInitial) }
                    effectsFlow.tryEmit(SettingsEffect.ShowError)
                }
            )
        }
    }

}