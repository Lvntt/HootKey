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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.input.pointer.pointerInteropFilter
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
import dev.banger.hootkey.Constants.ADDED_VAULT_CATEGORIES_KEY
import dev.banger.hootkey.Constants.DELETED_VAULT_CATEGORIES_KEY
import dev.banger.hootkey.Constants.DELETED_VAULT_IDS_KEY
import dev.banger.hootkey.Constants.EDITED_VAULT_KEY
import dev.banger.hootkey.Constants.EDITED_VAULT_NEW_CATEGORY_KEY
import dev.banger.hootkey.Constants.EDITED_VAULT_OLD_CATEGORY_KEY
import dev.banger.hootkey.Constants.UPDATED_VAULT_IDS_KEY
import dev.banger.hootkey.Constants.VAULT_CATEGORY_KEY
import dev.banger.hootkey.Constants.VAULT_KEY
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.intent.DashboardIntent
import dev.banger.hootkey.presentation.ui.common.bottomSheetBackground
import dev.banger.hootkey.presentation.ui.common.textfields.SearchTextField
import dev.banger.hootkey.presentation.ui.dialog.AppAlertDialog
import dev.banger.hootkey.presentation.ui.dialog.vault_details.VaultDetailsBottomSheet
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

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun DashboardScreen(
    savedStateHandleProvider: () -> SavedStateHandle?,
    onAddNewVault: () -> Unit,
    onCategorySelected: (Id, Name) -> Unit,
    onEditClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: DashboardViewmodel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val savedStateHandle = savedStateHandleProvider()

    //Handle changes after creating a new vault (vault + category)
    val addedVaultKeyFlow =
        savedStateHandle?.getStateFlow<String?>(VAULT_KEY, null)?.collectAsStateWithLifecycle()
    LaunchedEffect(addedVaultKeyFlow?.value) {
        val vaultKey = addedVaultKeyFlow?.value ?: return@LaunchedEffect
        val categoryKey =
            savedStateHandle.remove<String>(VAULT_CATEGORY_KEY) ?: return@LaunchedEffect
        savedStateHandle.remove<String>(VAULT_KEY)
        viewModel.dispatch(DashboardIntent.IncrementCategoryVaultsCount(categoryKey))
        viewModel.dispatch(DashboardIntent.AddNewVault(vaultKey))
    }

    //Handle changes after deleting vaults on another screen (vaults only)
    val deletedVaultIdsFlow = savedStateHandle?.getStateFlow<List<String>>(DELETED_VAULT_IDS_KEY, emptyList())
        ?.collectAsStateWithLifecycle()
    LaunchedEffect(deletedVaultIdsFlow?.value) {
        val deletedVaultIds = deletedVaultIdsFlow?.value ?: return@LaunchedEffect
        savedStateHandle.remove<List<String>>(DELETED_VAULT_IDS_KEY)
        viewModel.dispatch(DashboardIntent.RemoveDeletedVaults(deletedVaultIds))
    }

    //Handle changes after updating vaults on another screen (vaults only)
    val updatedVaultIdsFlow = savedStateHandle?.getStateFlow<List<String>>(UPDATED_VAULT_IDS_KEY, emptyList())
        ?.collectAsStateWithLifecycle()
    LaunchedEffect(updatedVaultIdsFlow?.value) {
        val updatedVaultIds = updatedVaultIdsFlow?.value ?: return@LaunchedEffect
        savedStateHandle.remove<List<String>>(UPDATED_VAULT_IDS_KEY)
        viewModel.dispatch(DashboardIntent.UpdateVaults(updatedVaultIds))
    }

    //Handle changes after deleting vaults or moving them between categories on another screen (categories)
    val deletedVaultCategoriesFlow =
        savedStateHandle?.getStateFlow<List<String>>(DELETED_VAULT_CATEGORIES_KEY, emptyList())?.collectAsStateWithLifecycle()
    val addedVaultCategoriesFlow =
        savedStateHandle?.getStateFlow<List<String>>(ADDED_VAULT_CATEGORIES_KEY, emptyList())?.collectAsStateWithLifecycle()
    LaunchedEffect(deletedVaultCategoriesFlow?.value, addedVaultCategoriesFlow?.value) {
        val deletedVaultCategories = deletedVaultCategoriesFlow?.value ?: emptyList()
        val addedVaultCategories = addedVaultCategoriesFlow?.value ?: emptyList()
        savedStateHandle?.remove<List<String>>(DELETED_VAULT_CATEGORIES_KEY)
        savedStateHandle?.remove<List<String>>(ADDED_VAULT_CATEGORIES_KEY)
        viewModel.dispatch(DashboardIntent.ChangeCategoriesVaultsCount(deletedVaultCategories, addedVaultCategories))
    }

    //Handle changes after editing a single vault (vault + categories)
    val updatedVaultKeyFlow = savedStateHandle?.getStateFlow<String?>(EDITED_VAULT_KEY, null)
        ?.collectAsStateWithLifecycle()
    LaunchedEffect(updatedVaultKeyFlow?.value) {
        val updatedVaultKey = updatedVaultKeyFlow?.value ?: return@LaunchedEffect
        savedStateHandle.remove<String>(EDITED_VAULT_OLD_CATEGORY_KEY)?.let {
            viewModel.dispatch(DashboardIntent.DecrementCategoryVaultsCount(it))
        }
        savedStateHandle.remove<String>(EDITED_VAULT_NEW_CATEGORY_KEY)?.let {
            viewModel.dispatch(DashboardIntent.IncrementCategoryVaultsCount(it))
        }
        savedStateHandle.remove<String>(EDITED_VAULT_KEY)
        viewModel.dispatch(DashboardIntent.UpdateVault(updatedVaultKey))
    }

    state.vaultDetails?.let { vault ->
        VaultDetailsBottomSheet(
            vaultId = vault.id,
            onDismissRequest = {
                viewModel.dispatch(DashboardIntent.DismissVaultDetails)
            },
            onEditClick = {
                onEditClick(vault.id)
                viewModel.dispatch(DashboardIntent.DismissVaultDetails)
            },
            onDeleteClick = {
                viewModel.dispatch(DashboardIntent.OpenDeleteDialog(vault))
                viewModel.dispatch(DashboardIntent.DismissVaultDetails)
            }
        )
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

        DashboardBackgroundContent(
            listStateProvider = { listState },
            onSetNonBottomSheetContentHeight = {
                nonBottomSheetContentHeight = it
            },
            onSettingsClick = onSettingsClick
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .bottomSheetBackground({ bottomSheetBackgroundYOffset }, cornerRadius)
                .pointerInteropFilter {
                    return@pointerInteropFilter false
                },
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
            }, onClick = {
                viewModel.dispatch(DashboardIntent.OpenVaultDetails(it))
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