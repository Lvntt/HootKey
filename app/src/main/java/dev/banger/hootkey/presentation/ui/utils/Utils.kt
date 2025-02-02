package dev.banger.hootkey.presentation.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Modifier.gradientTint(brush: Brush) = this
    .graphicsLayer(alpha = 0.99f)
    .drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(brush, blendMode = BlendMode.SrcAtop)
        }
    }

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

fun formatDate(millis: Long): String {
    val locale = Locale.getDefault()
    val date = Date(millis)
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", locale)
    return dateFormat.format(date)
}