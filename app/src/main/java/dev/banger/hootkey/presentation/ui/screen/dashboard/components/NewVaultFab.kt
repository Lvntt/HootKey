package dev.banger.hootkey.presentation.ui.screen.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.White

@Composable
fun NewVaultFab(onAddNewVault: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 27.dp), contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            modifier = Modifier
                .size(52.dp)
                .shadow(6.dp, shape = CircleShape, spotColor = Color(0xFFF7556D))
                .clip(CircleShape)
                .background(Primary),
            containerColor = Color.Unspecified,
            contentColor = Color.Unspecified,
            shape = CircleShape,
            onClick = onAddNewVault,
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.plus_icon),
                contentDescription = null,
                tint = White
            )
        }
    }
}