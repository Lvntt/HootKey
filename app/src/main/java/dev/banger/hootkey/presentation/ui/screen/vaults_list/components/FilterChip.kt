package dev.banger.hootkey.presentation.ui.screen.vaults_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.TypeR14_150
import dev.banger.hootkey.presentation.ui.theme.White

@Composable
fun FilterChip(
    modifier: Modifier = Modifier, text: String, isSelected: Boolean
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(45.dp))
            .filterChipBackground(isSelected)
            .padding(horizontal = 20.dp, vertical = 7.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = if (isSelected) TypeM14 else TypeR14_150,
            color = if (isSelected) White else Secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

private fun Modifier.filterChipBackground(isSelected: Boolean) = this.then(
    if (isSelected) {
        background(Primary)
    } else {
        background(White)
    }
)