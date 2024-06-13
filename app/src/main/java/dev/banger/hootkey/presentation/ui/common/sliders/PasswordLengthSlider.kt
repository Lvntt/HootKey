package dev.banger.hootkey.presentation.ui.common.sliders

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.banger.hootkey.presentation.ui.theme.DarkGray
import dev.banger.hootkey.presentation.ui.theme.LinearProgressIndicatorHeight
import dev.banger.hootkey.presentation.ui.theme.LinearProgressIndicatorRegular
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordLengthSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Slider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        valueRange = 1f..30f,
        interactionSource = interactionSource,
        onValueChangeFinished = {
            onValueChangeFinished(value.toInt())
        },
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                colors = SliderDefaults.colors(thumbColor = Secondary)
            )
        },
        track = {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(DarkGray, LinearProgressIndicatorRegular)
                    .height(LinearProgressIndicatorHeight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(it.value / 30)
                        .background(Secondary80, LinearProgressIndicatorRegular)
                        .height(LinearProgressIndicatorHeight)
                )
            }
        }
    )
}