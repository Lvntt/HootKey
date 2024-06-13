package dev.banger.hootkey.presentation.entity

import androidx.compose.ui.graphics.Brush
import dev.banger.hootkey.presentation.ui.theme.StrengthCompromisedBrush
import dev.banger.hootkey.presentation.ui.theme.StrengthMediumBrush
import dev.banger.hootkey.presentation.ui.theme.StrengthStrongBrush
import dev.banger.hootkey.presentation.ui.theme.StrengthVeryStrongBrush
import dev.banger.hootkey.presentation.ui.theme.StrengthVeryWeakBrush
import dev.banger.hootkey.presentation.ui.theme.StrengthWeakBrush

enum class UiPasswordStrength(
    val progress: Float,
    val brush: Brush,
) {
    COMPROMISED(1f, StrengthCompromisedBrush),
    VERY_WEAK(0.2f, StrengthVeryWeakBrush),
    WEAK(0.4f, StrengthWeakBrush),
    MEDIUM(0.6f, StrengthMediumBrush),
    STRONG(0.8f, StrengthStrongBrush),
    VERY_STRONG(1f, StrengthVeryStrongBrush)
}