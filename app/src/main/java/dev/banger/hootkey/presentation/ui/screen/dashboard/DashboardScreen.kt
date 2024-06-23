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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.Constants.VAULT_CATEGORY_KEY
import dev.banger.hootkey.Constants.VAULT_KEY
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.intent.DashboardIntent
import dev.banger.hootkey.presentation.ui.common.bottomSheetBackground
import dev.banger.hootkey.presentation.ui.common.textfields.SearchTextField
import dev.banger.hootkey.presentation.ui.dialog.AppAlertDialog
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.BOTTOM_SPACER
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.FIRST_VAULT_HINT
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.RECENTLY_USED_HEADER
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.SEARCH_FIELD
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.TOP_SPACER
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
    savedStateHandleProvider: () -> SavedStateHandle?,
    onAddNewVault: () -> Unit,
    onCategorySelected: (Id, Name) -> Unit,
    onEditClick: (String) -> Unit,
    viewModel: DashboardViewmodel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val savedStateHandle = savedStateHandleProvider()
    val vaultKeyFlow =
        savedStateHandle?.getStateFlow<String?>(VAULT_KEY, null)?.collectAsStateWithLifecycle()
    LaunchedEffect(vaultKeyFlow?.value) {
        val vaultKey = vaultKeyFlow?.value ?: return@LaunchedEffect
        val categoryKey =
            savedStateHandle.remove<String>(VAULT_CATEGORY_KEY) ?: return@LaunchedEffect
        savedStateHandle.remove<String>(VAULT_KEY)
        viewModel.dispatch(DashboardIntent.IncrementCategoryVaultsCount(categoryKey))
        viewModel.dispatch(DashboardIntent.AddNewVault(vaultKey))
    }

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

        state.deleteDialogOpenedForVault?.let {
            AppAlertDialog(
                onDismissRequest = { viewModel.dispatch(DashboardIntent.DismissDeleteDialog) },
                onPositiveAction = { viewModel.dispatch(DashboardIntent.DeleteVault) },
                title = stringResource(R.string.are_you_sure),
                message = stringResource(R.string.delete_vault_message),
                isLoading = state.isDeletingVault,
                positiveButtonText = stringResource(R.string.delete),
            )
        }

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
                .bottomSheetBackground({ bottomSheetBackgroundYOffset }, cornerRadius),
            contentPadding = WindowInsets.systemBars.asPaddingValues(),
        ) {
            item(contentType = TOP_SPACER) {
                Spacer(modifier = Modifier.height(with(LocalDensity.current) { nonBottomSheetContentHeight.toDp() }))
            }
            item(contentType = SEARCH_FIELD) {
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

            if (state.vaults.isNotEmpty() || state.vaultsPageLoadingState != LceState.CONTENT) item(
                contentType = RECENTLY_USED_HEADER
            ) {
                RecentlyUsedHeader(onCategorySelected)
            }
            if (state.vaults.isEmpty() && state.vaultsPageLoadingState == LceState.CONTENT) item(
                contentType = FIRST_VAULT_HINT
            ) {
                FirstVaultHintItem(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                    onClick = { onAddNewVault() })
            }

            vaultsContent(stateProvider = { state }, onLoadNextPageRequested = {
                viewModel.dispatch(DashboardIntent.LoadNextVaultsPage)
            }, onDeleteVaultRequested = {
                viewModel.dispatch(DashboardIntent.OpenDeleteDialog(it))
            }, onEditClick = onEditClick, clipboardManager = clipboardManager)

            item(contentType = BOTTOM_SPACER) {
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