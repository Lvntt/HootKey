package dev.banger.hootkey.presentation.ui.screen.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.intent.DashboardIntent
import dev.banger.hootkey.presentation.ui.common.bottomSheetBackground
import dev.banger.hootkey.presentation.ui.common.textfields.SearchTextField
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.DashboardBackgroundContent
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.FirstVaultHintItem
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.NewVaultFab
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.RecentlyUsedHeader
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.categoriesContent
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.vaultsContent
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.DashboardViewmodel
import org.koin.androidx.compose.koinViewModel

typealias Id = String?
typealias Name = String?

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddNewVault: () -> Unit,
    onCategorySelected: (Id, Name) -> Unit,
    viewModel: DashboardViewmodel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
        LocalMinimumInteractiveComponentEnforcement provides false
    ) {
        val configuration = LocalConfiguration.current
        val clipboardManager = LocalClipboardManager.current
        val defaultOffset = -with(LocalDensity.current) { 68.dp.toPx() }
        val cornerRadius = with(LocalDensity.current) { CornerRadius(40.dp.toPx(), 40.dp.toPx()) }
        var bottomSheetBackgroundYOffset by remember { mutableFloatStateOf(defaultOffset) }
        var bottomSheetStartPosY by remember { mutableFloatStateOf(-Float.MAX_VALUE) }
        var bottomSheetEndPosY by remember { mutableFloatStateOf(0f) }
        var nonBottomSheetContentHeight by remember { mutableFloatStateOf(0f) }
        val listState = rememberLazyListState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(painterResource(R.drawable.auth_bg), contentScale = ContentScale.FillBounds)
        )

        DashboardBackgroundContent(listStateProvider = { listState },
            onSetNonBottomSheetContentHeight = {
                nonBottomSheetContentHeight = it
            })

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .bottomSheetBackground({ bottomSheetBackgroundYOffset }, cornerRadius)
            ,
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

            categoriesContent(stateProvider = { state },
                onCategorySelected = onCategorySelected,
                onLoadCategoriesRequested = { viewModel.dispatch(DashboardIntent.LoadCategories) })

            if (state.vaults.isNotEmpty() || state.vaultsPageLoadingState != LceState.CONTENT) item {
                RecentlyUsedHeader(onCategorySelected)
            }
            if (state.vaults.isEmpty() && state.vaultsPageLoadingState == LceState.CONTENT) item {
                FirstVaultHintItem(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                    onClick = { onAddNewVault() })
            }

            vaultsContent(stateProvider = { state }, onLoadNextPageRequested = {
                viewModel.dispatch(DashboardIntent.LoadNextVaultsPage)
            }, clipboardManager = clipboardManager)

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
        NewVaultFab(onAddNewVault)
    }
}