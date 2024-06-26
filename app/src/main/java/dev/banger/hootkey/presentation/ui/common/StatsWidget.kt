package dev.banger.hootkey.presentation.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.entity.password.PasswordHealthScore
import dev.banger.hootkey.presentation.ui.common.progress.StatsProgressIndicator
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.StatsBackground
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.TypeSB58
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsWidget(
    modifier: Modifier = Modifier,
    scoreProvider: () -> PasswordHealthScore
) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false
    ) {
        val progress by remember {
            derivedStateOf {
                when (val score = scoreProvider()) {
                    is PasswordHealthScore.Score -> score.value
                    PasswordHealthScore.Calculating -> null
                    PasswordHealthScore.Unknown -> 0f
                }
            }
        }
        val scoreText by remember {
            derivedStateOf {
                when (val score = scoreProvider()) {
                    is PasswordHealthScore.Score -> "${(score.value * 100).toInt()}%"
                    PasswordHealthScore.Calculating -> "..."
                    PasswordHealthScore.Unknown -> "N/A"
                }
            }
        }
        Box(
            modifier = modifier.size(239.dp),
            contentAlignment = Alignment.Center
        ) {
            StatsProgressIndicator(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = 0.99f
                    }
                    .drawWithContent {
                        val colors = listOf(
                            Color.Black,
                            Color.Transparent
                        )
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(colors, endY = size.height * 0.9f),
                            blendMode = BlendMode.DstIn
                        )
                    },
                progressProvider = { progress }
            )
            Column(
                modifier = Modifier
                    .padding(all = 23.dp)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(brush = StatsBackground),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.health_score),
                    style = TypeM14,
                    color = White,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AnimatedContent(
                    targetState = scoreText,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }, label = "scoreSwitchAnimation"
                ) { scoreText ->
                    Text(
                        text = scoreText,
                        style = TypeSB58,
                        color = White,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .size(42.dp)
                    .background(
                        color = White,
                        shape = CircleShape
                    )
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.gradientTint(Primary),
                    imageVector = ImageVector.vectorResource(R.drawable.heartbeat_icon),
                    contentDescription = null
                )
            }
        }
    }
}