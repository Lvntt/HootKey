package dev.banger.hootkey.presentation.ui.screen.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.entity.password.PasswordHealthScore
import dev.banger.hootkey.presentation.ui.common.StatsWidget
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.screen.statistics.components.StatisticsCountCard
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingXLarge
import dev.banger.hootkey.presentation.ui.theme.White

@Composable
fun StatisticsScreen(onNavigateBack: () -> Unit) {
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(PaddingMedium))
            StatsWidget(scoreProvider = { PasswordHealthScore.Calculating })
            Spacer(modifier = Modifier.height(PaddingXLarge))
            Row {
                StatisticsCountCard(
                    modifier = Modifier.weight(1f),
                    typeLabel = stringResource(id = R.string.total),
                    textProvider = { "0" }
                )
                Spacer(modifier = Modifier.width(PaddingRegular))
                StatisticsCountCard(
                    modifier = Modifier.weight(1f),
                    typeLabel = stringResource(id = R.string.medium),
                    textProvider = { "0" }
                )
                Spacer(modifier = Modifier.width(PaddingRegular))
                StatisticsCountCard(
                    modifier = Modifier.weight(1f),
                    typeLabel = stringResource(id = R.string.weak),
                    textProvider = { "0" }
                )
            }
        }
    }
}