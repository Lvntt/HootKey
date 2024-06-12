package dev.banger.hootkey.presentation.ui.common.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.banger.hootkey.presentation.ui.theme.ButtonShapeSmall
import dev.banger.hootkey.presentation.ui.theme.PaddingSmall

@Composable
fun PrimaryButtonSmall(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(PaddingSmall),
    shape: RoundedCornerShape = ButtonShapeSmall
) {
    PrimaryButton(
        modifier = modifier,
        onClick = onClick,
        text = text,
        enabled = enabled,
        contentPadding = contentPadding,
        shape = shape
    )
}

@Preview
@Composable
private fun EnabledPrimaryButtonSmallPreview() {
    PrimaryButtonSmall(onClick = {}, text = "Copy password")
}

@Preview
@Composable
private fun DisabledPrimaryButtonSmallPreview() {
    PrimaryButtonSmall(onClick = {}, text = "Copy password", enabled = false)
}