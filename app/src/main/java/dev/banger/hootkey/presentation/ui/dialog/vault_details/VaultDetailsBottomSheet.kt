package dev.banger.hootkey.presentation.ui.dialog.vault_details

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.presentation.intent.VaultDetailsIntent
import dev.banger.hootkey.presentation.state.vault_details.VaultDetailsState
import dev.banger.hootkey.presentation.ui.common.ListLoadingContent
import dev.banger.hootkey.presentation.ui.common.VaultErrorItem
import dev.banger.hootkey.presentation.ui.dialog.vault_details.VaultDetailsContentTypes.HEADER
import dev.banger.hootkey.presentation.ui.dialog.vault_details.VaultDetailsContentTypes.TOP_SPACER
import dev.banger.hootkey.presentation.ui.dialog.vault_details.VaultDetailsContentTypes.VAULT_ERROR
import dev.banger.hootkey.presentation.ui.dialog.vault_details.VaultDetailsContentTypes.VAULT_FIELD
import dev.banger.hootkey.presentation.ui.dialog.vault_details.VaultDetailsContentTypes.VAULT_LOADING
import dev.banger.hootkey.presentation.ui.dialog.vault_details.components.VaultDetailsField
import dev.banger.hootkey.presentation.ui.dialog.vault_details.components.VaultDetailsHeader
import dev.banger.hootkey.presentation.ui.theme.BottomSheetDragHandle
import dev.banger.hootkey.presentation.ui.theme.Gray
import dev.banger.hootkey.presentation.viewmodel.VaultDetailsViewModel
import javax.inject.Provider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultDetailsBottomSheet(
    vaultId: String,
    viewModelFactory: VaultDetailsViewModel.Factory,
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    viewModel: VaultDetailsViewModel = viewModelFactory.create(vaultId)
) {
    val state by viewModel.state.collectAsStateWithLifecycle(lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current)
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false
    ) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    width = 32.dp,
                    height = 4.dp,
                    shape = RoundedCornerShape(100.dp),
                    color = BottomSheetDragHandle
                )
            },
            containerColor = Gray,
            windowInsets = WindowInsets(0.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = WindowInsets.systemBars.asPaddingValues()
                        .calculateBottomPadding() + 40.dp
                )
            ) {
                when (state) {
                    is VaultDetailsState.Content -> (state as? VaultDetailsState.Content)?.let { vault ->
                        item(contentType = HEADER) {
                            VaultDetailsHeader(
                                name = vault.name,
                                category = vault.categoryName,
                                isFavorite = vault.isFavorite,
                                onFavoriteClick = {
                                    viewModel.dispatch(VaultDetailsIntent.FavoriteVault)
                                },
                                onEditClick = onEditClick,
                                onDeleteClick = onDeleteClick
                            )
                        }
                        item(contentType = TOP_SPACER) {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        itemsIndexed(vault.fields,
                            contentType = { _, _ -> VAULT_FIELD }) { index, field ->
                            VaultDetailsField(isLastItem = index == vault.fields.lastIndex,
                                isFirstItem = index == 0,
                                field = field,
                                onCopyContent = {
                                    clipboardManager.setText(AnnotatedString(field.value))
                                },
                                onOpenLink = {
                                    runCatching {
                                        val url =
                                            if (!field.value.startsWith("http://") && !field.value.startsWith(
                                                    "https://"
                                                )
                                            ) {
                                                "https://${field.value}"
                                            } else {
                                                field.value
                                            }
                                        uriHandler.openUri(url)
                                    }
                                },
                                onToggleVisibility = {
                                    viewModel.dispatch(
                                        VaultDetailsIntent.ChangeFieldVisibility(index)
                                    )
                                })
                        }
                    }

                    VaultDetailsState.Error -> item(contentType = VAULT_ERROR) {
                        VaultErrorItem(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            viewModel.dispatch(VaultDetailsIntent.LoadVault)
                        }
                    }

                    VaultDetailsState.Loading -> item(contentType = VAULT_LOADING) {
                        ListLoadingContent()
                    }
                }
            }
        }
    }
}