package dev.banger.hootkey.presentation.ui.screen.launch

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.state.auth.LaunchEvent
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.viewmodel.LaunchViewModel

@Composable
fun LaunchScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onNavigateToAccountLogin: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: LaunchViewModel = viewModel(factory = viewModelFactory)
) {
    ObserveAsEvents(viewModel.events) {
        when (it) {
            LaunchEvent.Authorized -> onNavigateToLogin()
            LaunchEvent.Unauthorized -> onNavigateToAccountLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(R.drawable.auth_bg),
                contentScale = ContentScale.FillBounds
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            imageVector = ImageVector.vectorResource(R.drawable.auth_logo),
            contentDescription = null
        )
    }
}