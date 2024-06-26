package dev.banger.hootkey.presentation.ui.common.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.banger.hootkey.presentation.ui.theme.DarkGray
import dev.banger.hootkey.presentation.ui.theme.LinearProgressIndicatorHeight
import dev.banger.hootkey.presentation.ui.theme.LinearProgressIndicatorRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.entity.UiPasswordStrength

@Composable
fun PasswordStrengthIndicator(
    strength: UiPasswordStrength,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkGray, LinearProgressIndicatorRegular)
            .height(LinearProgressIndicatorHeight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(strength.progress)
                .background(strength.brush, LinearProgressIndicatorRegular)
                .height(LinearProgressIndicatorHeight)
        )
    }
}

@Preview
@Composable
private fun PasswordStrengthIndicatorPreview() {
    Box {
        PasswordStrengthIndicator(
            strength = UiPasswordStrength.MEDIUM,
            modifier = Modifier.padding(PaddingMedium)
        )
    }
}