package dev.banger.hootkey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.banger.hootkey.di.qualifiers.IoDispatcher
import dev.banger.hootkey.domain.entity.password.PasswordHealthScore
import dev.banger.hootkey.domain.repository.PasswordRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository,
    @IoDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    val passwordHealthScore = passwordRepository.passwordHealthScore

    fun updatePasswordHealthScore() {
        if (passwordHealthScore.value is PasswordHealthScore.Calculating) return
        viewModelScope.launch(defaultDispatcher) {
            passwordRepository.calculatePasswordHealthScore()
        }
    }

}