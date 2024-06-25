package dev.banger.hootkey.presentation.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val PrimaryPink = Color(0xFFF2167B)
val PrimaryOrange = Color(0xFFF86668)
val PrimaryShadow = Color(0xFFF7556D)

val PrimaryPinkDisabled = Color(0xFFDA6D9D)
val PrimaryOrangeDisabled = Color(0xFFE28B8C)

val Primary20 = Color(0xFFFFECE8)
val Primary20Disabled = Color(0xFFFAEEED)

val Primary = Brush.linearGradient(listOf(PrimaryPink, PrimaryOrange))
val PrimaryDisabled = Brush.linearGradient(listOf(PrimaryPinkDisabled, PrimaryOrangeDisabled))

val Secondary = Color(0xFF171F46)
val Secondary80 = Color(0xFF47495B)
val Secondary70 = Color(0xFF5D637E)
val Secondary60 = Color(0xFF757784)

val White = Color(0xFFFFFFFF)
val PeachGray = Color(0xFFF1EAF1)
val Gray = Color(0xFFF0F4F7)
val LightGray = Color(0xFFCCCFD4)
val DarkGray = Color(0xFFD9D9D9)

val MainDark = Color(0xFF011821)

val StrengthCompromisedBrush = Brush.linearGradient(
    listOf(Color(0xFFBD0E11), Color(0xFFFF2D2D))
)
val StrengthVeryWeakBrush = Brush.linearGradient(
    listOf(Color(0xFFBD0E11), Color(0xFFFF2D2D))
)
val StrengthWeakBrush = Brush.linearGradient(
    listOf(Color(0xFFCC4014), Color(0xFFFF792D))
)
val StrengthMediumBrush = Brush.linearGradient(
    listOf(Color(0xFFF2B416), Color(0xFFF8E966))
)
val StrengthStrongBrush = Brush.linearGradient(
    listOf(Color(0xFF2CC929), Color(0xFF7ADA4D))
)
val StrengthVeryStrongBrush = Brush.linearGradient(
    listOf(Color(0xFF399A29), Color(0xFF65D123))
)

val DefaultBackgroundBrush = Brush.verticalGradient(
    listOf(PeachGray, Gray)
)

val BottomSheetDragHandle = Color(0xFF79747E)