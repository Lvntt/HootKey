package dev.banger.hootkey.presentation.ui.screen.dashboard.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.domain.entity.vault.VaultShort
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.state.dashboard.DashboardState
import dev.banger.hootkey.presentation.ui.common.ListLoadingContent
import dev.banger.hootkey.presentation.ui.common.VaultErrorItem
import dev.banger.hootkey.presentation.ui.common.VaultShortItem
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.ERROR_VAULTS
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.LOADING_CONTENT
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.VAULT

inline fun LazyListScope.vaultsContent(
    crossinline stateProvider: () -> DashboardState,
    crossinline onLoadNextPageRequested: () -> Unit,
    crossinline onDeleteVaultRequested: (VaultShort) -> Unit,
    crossinline onEditClick: (String) -> Unit,
    clipboardManager: ClipboardManager
) {
    itemsIndexed(items = stateProvider().vaults,
        key = { _, item -> item.id },
        contentType = { _, _ -> VAULT }) { index, vault ->
        if (index >= stateProvider().vaults.size - 1 && !stateProvider().isEndReached && stateProvider().vaultsPageLoadingState == LceState.CONTENT) {
            onLoadNextPageRequested()
        }
        VaultShortItem(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
            iconModel = {
                "https://www.google.com/s2/favicons?domain=${vault.link}&sz=256"
                    .takeIf { !vault.link.isNullOrBlank() }
            },
            name = vault.name,
            login = vault.login ?: "",
            onClick = {},
            onCopyClick = {
                val clipData = if (!vault.password.isNullOrBlank()) vault.password
                else if (!vault.login.isNullOrBlank()) vault.login
                else vault.name
                clipboardManager.setText(AnnotatedString(clipData))
            },
            onEditClick = {
                onEditClick(vault.id)
            },
            onDeleteClick = {
                onDeleteVaultRequested(vault)
            })
        Spacer(modifier = Modifier.height(12.dp))
    }
    when (stateProvider().vaultsPageLoadingState) {
        LceState.LOADING -> item(contentType = LOADING_CONTENT) {
            ListLoadingContent()
        }

        LceState.CONTENT -> Unit

        LceState.ERROR -> item(contentType = ERROR_VAULTS) {
            VaultErrorItem(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
                onClick = { onLoadNextPageRequested() })
        }
    }
}