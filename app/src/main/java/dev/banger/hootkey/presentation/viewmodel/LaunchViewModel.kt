package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.repository.SettingsRepository
import dev.banger.hootkey.domain.usecase.CheckUserLoggedInUseCase
import dev.banger.hootkey.presentation.state.auth.LaunchEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<LaunchEvent>(replay = 1)
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            if (settingsRepository.isOffline()) {
                settingsRepository.goOffline()
            } else {
                settingsRepository.goOnline()
            }

            if (checkUserLoggedInUseCase()) {
                _events.emit(LaunchEvent.Authorized)
            } else {
                _events.emit(LaunchEvent.Unauthorized)
            }
        }
    }

}