package dev.banger.hootkey.presentation.ui.screen.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.common.textfields.SearchTextField
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.DashboardCategory
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.VaultShortItem
import dev.banger.hootkey.presentation.ui.theme.Gray
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.TypeM12

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen() {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        val configuration = LocalConfiguration.current
        val defaultOffset = -with(LocalDensity.current) { 68.dp.toPx() }
        var backgroundYOffset by remember { mutableFloatStateOf(defaultOffset) }
        var bottomSheetStartPosY by remember { mutableFloatStateOf(-Float.MAX_VALUE) }
        var bottomSheetEndPosY by remember { mutableFloatStateOf(0f) }
        val state = rememberLazyListState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(painterResource(R.drawable.auth_bg), contentScale = ContentScale.FillBounds)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(17.dp))
            Image(
                modifier = Modifier
                    .size(239.dp)
                    .graphicsLayer {
                        scaleX = 1f + state.firstVisibleItemScrollOffset * 0.0003f
                        scaleY = 1f + state.firstVisibleItemScrollOffset * 0.0003f
                        alpha = 1f - state.firstVisibleItemScrollOffset * 0.001f
                    },
                painter = painterResource(R.drawable.health_score_placeholder),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(17.dp))
        }
        LazyColumn(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val cornerRadius = CornerRadius(40.dp.toPx(), 40.dp.toPx())
                    val path = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(
                                    offset = Offset(0f, backgroundYOffset),
                                    size = Size(size.width, size.height - backgroundYOffset)
                                ), topLeft = cornerRadius, topRight = cornerRadius
                            )
                        )
                    }
                    drawPath(path = path, color = Gray)
                },
            contentPadding = WindowInsets.systemBars.asPaddingValues(),
        ) {
            item {
                Spacer(modifier = Modifier.height(273.dp))
            }
            item {
                Spacer(modifier = Modifier
                    .height(20.dp)
                    .onGloballyPositioned {
                        bottomSheetStartPosY = it.positionInParent().y
                    })
                SearchTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(start = 20.dp, end = 20.dp),
                    value = "",
                    onValueChange = {},
                    placeholder = stringResource(R.string.search_vaults),
                    enabled = false
                )
                Spacer(modifier = Modifier
                    .height(20.dp)
                    .onGloballyPositioned {
                        backgroundYOffset = it.positionInParent().y + defaultOffset
                    })
            }
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(count = 5) {
                        DashboardCategory(iconResId = R.drawable.finance_icon,
                            title = "Finance $it",
                            passwordCount = 5 * it,
                            onClick = {})
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .weight(1f),
                        text = stringResource(R.string.recently_used),
                        style = TypeB16,
                        color = Secondary,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .weight(1f),
                        text = stringResource(R.string.see_more),
                        style = TypeM12,
                        color = Secondary60,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(count = 5) {
                VaultShortItem(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                    iconModel = { "https://www.google.com/s2/favicons?domain=netflix.com&sz=256" },
                    name = "Netflix",
                    login = "user@example.com",
                    onClick = {},
                    onCopyClick = {},
                    onEditClick = {},
                    onDeleteClick = {})
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Spacer(modifier = Modifier
                    .onGloballyPositioned {
                        bottomSheetEndPosY = it.positionInParent().y
                    }
                    .height(
                        maxOf(
                            configuration.screenHeightDp.dp - with(LocalDensity.current) { (bottomSheetEndPosY - bottomSheetStartPosY).toDp() },
                            0.dp
                        )
                    )
                )
            }
        }
    }
}