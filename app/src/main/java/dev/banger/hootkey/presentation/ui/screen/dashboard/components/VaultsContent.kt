package dev.banger.hootkey.presentation.ui.screen.dashboard.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.state.dashboard.DashboardState
import dev.banger.hootkey.presentation.ui.common.VaultErrorItem
import dev.banger.hootkey.presentation.ui.common.VaultShortItem

fun LazyListScope.vaultsContent(
    stateProvider: () -> DashboardState,
    onLoadNextPageRequested: () -> Unit,
    clipboardManager: ClipboardManager
) {
    val state = stateProvider()
    itemsIndexed(items = state.vaults) { index, vault ->
        if (index >= state.vaults.size - 1 && !state.isEndReached && state.vaultsPageLoadingState == LceState.CONTENT) {
            onLoadNextPageRequested()
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
    }
    when (state.vaultsPageLoadingState) {
        LceState.LOADING -> item {
            DashboardLoadingContent()
        }

        LceState.CONTENT -> Unit

        LceState.ERROR -> item {
            VaultErrorItem(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
                onClick = { onLoadNextPageRequested() })
        }
    }
}