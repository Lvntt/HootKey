package dev.banger.hootkey.presentation.ui.screen.launch

import androidx.compose.runtime.Composable
import dev.banger.hootkey.presentation.state.LaunchEvent
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.viewmodel.LaunchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LaunchScreen(
    onNavigateToAccountLogin: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: LaunchViewModel = koinViewModel()
) {
    ObserveAsEvents(viewModel.events) {
        when (it) {
            LaunchEvent.Authorized -> onNavigateToLogin()
            LaunchEvent.Unauthorized -> onNavigateToAccountLogin()
        }
    }
}