package dev.banger.hootkey.presentation.ui.common.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.presentation.ui.theme.StatsProgress
import dev.banger.hootkey.presentation.ui.theme.StatsProgressTrack
import kotlinx.coroutines.delay

@Composable
fun StatsProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 239.dp,
    trackWidth: Dp = 12.dp,
    progressProvider: () -> Float?,
    color: Color = StatsProgress,
    trackColor: Color = StatsProgressTrack
) {
    val animatedProgress by animateFloatAsState(progressProvider()?.coerceIn(0f, 1f) ?: 0.5f, label = "statsProgress", animationSpec = tween(durationMillis = 2000))

    var targetAngle by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(progressProvider()) {
        if (progressProvider() == null) {
            while (true) {
                targetAngle += 360
                delay(2000)
            }
        }
    }
    val animatedStartAngle by animateFloatAsState(targetAngle, animationSpec = tween(durationMillis = 2000), label = "startAngleAnimation")

    Canvas(modifier = modifier.size(size)) {
        val trackWidthPx = trackWidth.toPx()
        val arcSize = size.toPx() - trackWidthPx
        withTransform({
            rotate(degrees = 90f + animatedStartAngle, pivot = center)
        }) {
            drawArc(
                color = color,
                startAngle = 5f,
                sweepAngle = animatedProgress * 360 - 10f,
                topLeft = Offset(trackWidthPx / 2, trackWidthPx /2),
                size = Size(arcSize, arcSize),
                style = Stroke(width = trackWidthPx, cap = StrokeCap.Round),
                useCenter = false
            )
        }
        withTransform({
            rotate(degrees = 90f + animatedStartAngle + animatedProgress * 360, pivot = center)
        }) {
            drawArc(
                color = trackColor,
                startAngle = 5f,
                sweepAngle = (1f - animatedProgress) * 360 - 10f,
                topLeft = Offset(trackWidthPx / 2, trackWidthPx /2),
                size = Size(arcSize, arcSize),
                style = Stroke(width = trackWidthPx, cap = StrokeCap.Round),
                useCenter = false
            )
        }
    }
}