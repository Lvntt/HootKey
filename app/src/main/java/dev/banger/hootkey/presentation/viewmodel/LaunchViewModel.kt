package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.domain.usecase.CheckUserLoggedInUseCase
import dev.banger.hootkey.presentation.state.LaunchEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LaunchViewModel(
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<LaunchEvent>(replay = 1)
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            if (checkUserLoggedInUseCase()) {
                _events.emit(LaunchEvent.Authorized)
            } else {
                _events.emit(LaunchEvent.Unauthorized)
            }
        }
    }

}