package dev.banger.hootkey.presentation.ui.common.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.banger.hootkey.presentation.ui.theme.ButtonHeightRegular
import dev.banger.hootkey.presentation.ui.theme.ButtonShapeRegular
import dev.banger.hootkey.presentation.ui.theme.ButtonShapeSmall
import dev.banger.hootkey.presentation.ui.theme.DarkGray
import dev.banger.hootkey.presentation.ui.theme.PaddingNone
import dev.banger.hootkey.presentation.ui.theme.PaddingSmall
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.PrimaryDisabled
import dev.banger.hootkey.presentation.ui.theme.TypeB14
import dev.banger.hootkey.presentation.ui.theme.White

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(PaddingSmall),
    shape: RoundedCornerShape = ButtonShapeRegular
) {
    val backgroundColor = if (enabled) Primary else PrimaryDisabled
    val contentColor = if (enabled) White else DarkGray

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (enabled)
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                        onClick = onClick
                    )
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(contentPadding),
            text = text,
            style = TypeB14,
            color = contentColor
        )
    }
}