package dev.banger.hootkey.presentation.ui.common.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.presentation.ui.theme.ButtonShapeVerySmall
import dev.banger.hootkey.presentation.ui.theme.PaddingTiny

@Composable
fun PrimaryButtonTiny(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 10.dp,
        vertical = PaddingTiny
    ),
    shape: RoundedCornerShape = ButtonShapeVerySmall
) {
    PrimaryButton(
        modifier = modifier,
        onClick = onClick,
        text = text,
        enabled = enabled,
        contentPadding = contentPadding,
        shape = shape,
        isLoading = isLoading
    )
}

@Preview
@Composable
private fun EnabledPrimaryButtonSmallPreview() {
    PrimaryButtonSmall(onClick = {}, text = "View")
}

@Preview
@Composable
private fun DisabledPrimaryButtonSmallPreview() {
    PrimaryButtonSmall(onClick = {}, text = "View", enabled = false)
}