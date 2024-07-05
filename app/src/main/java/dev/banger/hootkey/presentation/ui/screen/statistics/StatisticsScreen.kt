package dev.banger.hootkey.presentation.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.entity.password.PasswordHealthScore
import dev.banger.hootkey.presentation.ui.common.StatsWidget
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.screen.statistics.components.StatisticsCountCard
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingXLarge
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TypeM16
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.StatisticsViewModel

@Composable
fun StatisticsScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = viewModel(factory = viewModelFactory)
) {
    val healthScore by viewModel.passwordHealthScore.collectAsStateWithLifecycle(lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current)

    Scaffold(
        topBar = {
            HootKeyTopBar(
                onNavigateBack = onNavigateBack,
                title = stringResource(id = R.string.stats_title),
                titleColor = White
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .paint(painterResource(R.drawable.auth_bg), contentScale = ContentScale.FillBounds)
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(PaddingMedium))
            StatsWidget(scoreProvider = { healthScore })
            Spacer(modifier = Modifier.height(PaddingXLarge))
            Row {
                StatisticsCountCard(
                    modifier = Modifier.weight(1f),
                    typeLabel = stringResource(id = R.string.total),
                    textProvider = {
                        when (val score = healthScore) {
                            is PasswordHealthScore.Score -> score.totalPasswordCount.toString()
                            PasswordHealthScore.Calculating -> "..."
                            is PasswordHealthScore.Unknown -> "N/A"
                        }
                    }
                )
                Spacer(modifier = Modifier.width(PaddingRegular))
                StatisticsCountCard(
                    modifier = Modifier.weight(1f),
                    typeLabel = stringResource(id = R.string.medium),
                    textProvider = {
                        when (val score = healthScore) {
                            is PasswordHealthScore.Score -> score.mediumPasswordCount.toString()
                            PasswordHealthScore.Calculating -> "..."
                            is PasswordHealthScore.Unknown -> "N/A"
                        }
                    }
                )
                Spacer(modifier = Modifier.width(PaddingRegular))
                StatisticsCountCard(
                    modifier = Modifier.weight(1f),
                    typeLabel = stringResource(id = R.string.weak),
                    textProvider = {
                        when (val score = healthScore) {
                            is PasswordHealthScore.Score -> score.weakPasswordCount.toString()
                            PasswordHealthScore.Calculating -> "..."
                            is PasswordHealthScore.Unknown -> "N/A"
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(PaddingMedium))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(White)
                    .padding(12.dp)
                    .noRippleClickable { viewModel.updatePasswordHealthScore() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.sync_icon),
                    contentDescription = null,
                    tint = Secondary80
                )
                Spacer(modifier = Modifier.width(PaddingRegular))
                Text(
                    text = stringResource(id = R.string.calculate_again),
                    color = Secondary,
                    style = TypeM16,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}