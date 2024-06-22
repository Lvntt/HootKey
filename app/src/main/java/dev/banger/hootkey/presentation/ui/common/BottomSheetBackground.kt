package dev.banger.hootkey.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.presentation.ui.theme.Gray

@Composable
fun Modifier.bottomSheetBackground(topYPos: Float) = drawBehind {
    val cornerRadius = CornerRadius(40.dp.toPx(), 40.dp.toPx())
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(
                    offset = Offset(0f, topYPos), size = Size(
                        size.width, size.height - topYPos
                    )
                ), topLeft = cornerRadius, topRight = cornerRadius
            )
        )
    }
    drawPath(path = path, color = Gray)
}