package dev.banger.hootkey.presentation.ui.screen.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.common.textfields.SearchTextField
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.DashboardCategory
import dev.banger.hootkey.presentation.ui.common.VaultShortItem
import dev.banger.hootkey.presentation.ui.theme.Gray
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.TypeM12
import dev.banger.hootkey.presentation.ui.theme.TypeM20
import dev.banger.hootkey.presentation.ui.theme.TypeR14
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable

typealias Id = String?
typealias Name = String?

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddNewVault: () -> Unit,
    onCategorySelected: (Id, Name) -> Unit,
) {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
        LocalMinimumInteractiveComponentEnforcement provides false
    ) {
        val configuration = LocalConfiguration.current
        val defaultOffset = -with(LocalDensity.current) { 68.dp.toPx() }
        var bottomSheetBackgroundYOffset by remember { mutableFloatStateOf(defaultOffset) }
        var bottomSheetStartPosY by remember { mutableFloatStateOf(-Float.MAX_VALUE) }
        var bottomSheetEndPosY by remember { mutableFloatStateOf(0f) }
        var nonBottomSheetContentHeight by remember { mutableFloatStateOf(0f) }
        val state = rememberLazyListState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(painterResource(R.drawable.auth_bg), contentScale = ContentScale.FillBounds)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .graphicsLayer {
                    alpha = 1f - state.firstVisibleItemScrollOffset * 0.001f
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
                    onClick = {},
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
            Image(
                modifier = Modifier
                    .size(239.dp)
                    .graphicsLayer {
                        scaleX = 1f + state.firstVisibleItemScrollOffset * 0.0003f
                        scaleY = 1f + state.firstVisibleItemScrollOffset * 0.0003f
                    },
                painter = painterResource(R.drawable.health_score_placeholder),
                contentDescription = null
            )
            Spacer(modifier = Modifier
                .height(17.dp)
                .onGloballyPositioned {
                    nonBottomSheetContentHeight = it.positionInParent().y + it.size.height
                })
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
                                    offset = Offset(0f, bottomSheetBackgroundYOffset), size = Size(
                                        size.width, size.height - bottomSheetBackgroundYOffset
                                    )
                                ), topLeft = cornerRadius, topRight = cornerRadius
                            )
                        )
                    }
                    drawPath(path = path, color = Gray)
                },
            contentPadding = WindowInsets.systemBars.asPaddingValues(),
        ) {
            item {
                Spacer(modifier = Modifier.height(with(LocalDensity.current) { nonBottomSheetContentHeight.toDp() }))
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
                        .padding(start = 20.dp, end = 20.dp)
                        .noRippleClickable { onCategorySelected(null, null) },
                    value = "",
                    onValueChange = {},
                    placeholder = stringResource(R.string.search_vaults),
                    enabled = false,
                )
                Spacer(modifier = Modifier
                    .height(20.dp)
                    .onGloballyPositioned {
                        bottomSheetBackgroundYOffset = it.positionInParent().y + defaultOffset
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
                            onClick = {
                                onCategorySelected("id", "Finance $it")
                            })
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 27.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(6.dp, shape = CircleShape, spotColor = Color(0xFFF7556D))
                    .clip(CircleShape)
                    .background(Primary),
                containerColor = Color.Unspecified,
                contentColor = Color.Unspecified,
                shape = CircleShape,
                onClick = onAddNewVault,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.plus_icon),
                    contentDescription = null,
                    tint = White
                )
            }
        }
    }
}