package dev.banger.hootkey.presentation.ui.screen.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.entity.password.PasswordHealthScore
import dev.banger.hootkey.presentation.ui.common.StatsWidget
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.TypeM20
import dev.banger.hootkey.presentation.ui.theme.TypeR14
import dev.banger.hootkey.presentation.ui.theme.White

@Composable
inline fun DashboardBackgroundContent(
    crossinline listStateProvider: () -> LazyListState,
    crossinline onSetNonBottomSheetContentHeight: (Float) -> Unit,
    noinline passwordHealthScoreProvider: () -> PasswordHealthScore,
    noinline onSettingsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .graphicsLayer {
                alpha = 1f - listStateProvider().firstVisibleItemScrollOffset * 0.001f
            }, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(17.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_title),
                    style = TypeM20,
                    color = White,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.dashboard_subtitle),
                    style = TypeR14,
                    color = White,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(White),
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Secondary
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.statistics_icon),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(7.dp))
            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(White),
                onClick = onSettingsClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Secondary
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.settings_icon),
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        StatsWidget(
            modifier = Modifier.graphicsLayer {
                scaleX = 1f + listStateProvider().firstVisibleItemScrollOffset * 0.0003f
                scaleY = 1f + listStateProvider().firstVisibleItemScrollOffset * 0.0003f
            },
            scoreProvider = passwordHealthScoreProvider
        )
        Spacer(modifier = Modifier
            .height(17.dp)
            .onGloballyPositioned {
                onSetNonBottomSheetContentHeight(it.positionInParent().y + it.size.height)
            })
    }
}