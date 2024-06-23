package dev.banger.hootkey.presentation.ui.screen.vaults_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.entity.UiFilterType
import dev.banger.hootkey.presentation.intent.VaultsListIntent
import dev.banger.hootkey.presentation.ui.common.ListLoadingContent
import dev.banger.hootkey.presentation.ui.common.VaultErrorItem
import dev.banger.hootkey.presentation.ui.common.VaultShortItem
import dev.banger.hootkey.presentation.ui.common.textfields.SearchTextField
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.screen.vaults_list.VaultsListContentTypes.ERROR_VAULTS
import dev.banger.hootkey.presentation.ui.screen.vaults_list.VaultsListContentTypes.FILTER_CHIPS
import dev.banger.hootkey.presentation.ui.screen.vaults_list.VaultsListContentTypes.LOADING_CONTENT
import dev.banger.hootkey.presentation.ui.screen.vaults_list.VaultsListContentTypes.SEARCH_FIELD
import dev.banger.hootkey.presentation.ui.screen.vaults_list.VaultsListContentTypes.VAULT
import dev.banger.hootkey.presentation.ui.screen.vaults_list.components.FilterChip
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.VaultsListViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultsListScreen(
    categoryName: String? = null,
    categoryId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: VaultsListViewModel = koinViewModel(parameters = { parametersOf(categoryId ?: "") })
) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current

    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false
    ) {
        val focusManager = LocalFocusManager.current
        Scaffold(modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                focusManager.clearFocus()
            }, topBar = {
            HootKeyTopBar(
                onNavigateBack = onNavigateBack,
                title = categoryName ?: stringResource(id = R.string.my_vaults)
            )
        }) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DefaultBackgroundBrush)
                    .padding(padding)
                    .padding(top = 16.dp)
            ) {
                item(contentType = SEARCH_FIELD) {
                    SearchTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(start = 20.dp, end = 20.dp),
                        value = query,
                        onValueChange = { viewModel.dispatch(VaultsListIntent.ChangeSearchQuery(it)) },
                        placeholder = stringResource(R.string.search_vaults),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item(contentType = FILTER_CHIPS) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(UiFilterType.entries) { filterType ->
                            FilterChip(
                                modifier = Modifier.noRippleClickable {
                                    viewModel.dispatch(VaultsListIntent.ChangeFilterType(filterType))
                                },
                                text = stringResource(filterType.labelResId),
                                isSelected = filterType == state.uiFilterType
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                itemsIndexed(items = state.vaults,
                    key = { _, item -> item.id },
                    contentType = { _, _ -> VAULT }) { index, vault ->
                    if (index >= state.vaults.size - 1 && !state.endReached && state.vaultsPageLoadingState == LceState.CONTENT) {
                        viewModel.dispatch(VaultsListIntent.LoadVaultsNextPage)
                    }
                    VaultShortItem(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                        iconModel = { "https://www.google.com/s2/favicons?domain=${vault.link}&sz=256" },
                        name = vault.name,
                        login = vault.login ?: "",
                        onClick = {},
                        onCopyClick = {
                            val clipData = if (!vault.password.isNullOrBlank()) vault.password
                            else if (!vault.login.isNullOrBlank()) vault.login
                            else vault.name
                            clipboardManager.setText(AnnotatedString(clipData))
                        },
                        onEditClick = {},
                        onDeleteClick = {})
                    Spacer(modifier = Modifier.height(12.dp))
                }
                when (state.vaultsPageLoadingState) {
                    LceState.LOADING -> item(contentType = LOADING_CONTENT) {
                        ListLoadingContent()
                    }

                    LceState.CONTENT -> Unit

                    LceState.ERROR -> item(contentType = ERROR_VAULTS) {
                        VaultErrorItem(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                            onClick = { viewModel.dispatch(VaultsListIntent.LoadVaultsNextPage) })
                    }
                }
            }
        }
    }
}