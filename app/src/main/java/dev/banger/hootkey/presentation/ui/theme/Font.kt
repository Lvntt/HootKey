package dev.banger.hootkey.presentation.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import dev.banger.hootkey.R

val Ubuntu = FontFamily(
    Font(R.font.ubuntu_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.ubuntu_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.ubuntu_regular, FontWeight.Normal, FontStyle.Normal),
)

@OptIn(ExperimentalTextApi::class)
val Outfit = FontFamily(
    Font(
        resId = R.font.outfit_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontWeight.Bold, FontStyle.Normal)
    )
)