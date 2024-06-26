package dev.banger.hootkey.presentation.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import dev.banger.hootkey.presentation.ui.theme.Gray

fun Modifier.bottomSheetBackground(topYPosProvider: () -> Float, cornerRadius: CornerRadius) = drawBehind {
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(
                    offset = Offset(0f, topYPosProvider()), size = Size(
                        size.width, size.height - topYPosProvider()
                    )
                ), topLeft = cornerRadius, topRight = cornerRadius
            )
        )
    }
    drawPath(path = path, color = Gray)
}