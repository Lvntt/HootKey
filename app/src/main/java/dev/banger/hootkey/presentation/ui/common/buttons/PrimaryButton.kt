package dev.banger.hootkey.presentation.ui.common.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.banger.hootkey.presentation.ui.common.ButtonProgressLoader
import dev.banger.hootkey.presentation.ui.theme.ButtonShapeRegular
import dev.banger.hootkey.presentation.ui.theme.DarkGray
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
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(PaddingSmall),
    shape: RoundedCornerShape = ButtonShapeRegular,
    loadingContent: @Composable () -> Unit = { ButtonProgressLoader() }
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
        if (isLoading) {
            loadingContent()
        } else {
            Text(
                modifier = Modifier.padding(contentPadding),
                text = text,
                style = TypeB14,
                color = contentColor
            )
        }
    }
}