package dev.banger.hootkey.presentation.ui.common.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.banger.hootkey.presentation.ui.theme.ButtonHeightRegular
import dev.banger.hootkey.presentation.ui.theme.ButtonShapeRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingNone

@Composable
fun PrimaryButtonRegular(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(PaddingNone),
    shape: RoundedCornerShape = ButtonShapeRegular
) {
    PrimaryButton(
        modifier = modifier.height(ButtonHeightRegular),
        onClick = onClick,
        text = text,
        enabled = enabled,
        contentPadding = contentPadding,
        shape = shape
    )
}

@Preview
@Composable
private fun EnabledPrimaryButtonRegularPreview() {
    PrimaryButtonRegular(
        modifier = Modifier.fillMaxWidth(),
        onClick = { },
        text = "Login"
    )
}

@Preview
@Composable
private fun DisabledPrimaryButtonRegularPreview() {
    PrimaryButtonRegular(
        modifier = Modifier.fillMaxWidth(),
        onClick = { },
        text = "Login",
        enabled = false
    )
}