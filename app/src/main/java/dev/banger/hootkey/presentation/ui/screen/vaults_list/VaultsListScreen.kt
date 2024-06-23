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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiFilterType
import dev.banger.hootkey.presentation.ui.common.VaultShortItem
import dev.banger.hootkey.presentation.ui.common.textfields.SearchTextField
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.screen.vaults_list.components.FilterChip
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultsListScreen(
    categoryName: String? = null, categoryId: String? = null, onNavigateBack: () -> Unit
) {
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
                item {
                    SearchTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(start = 20.dp, end = 20.dp),
                        value = "",
                        onValueChange = {},
                        placeholder = stringResource(R.string.search_vaults),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(UiFilterType.entries) { filterType ->
                            FilterChip(
                                text = stringResource(filterType.labelResId),
                                isSelected = filterType == UiFilterType.ALL
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                items(count = 5) {
                    VaultShortItem(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                        iconModel = { /*"https://www.google.com/s2/favicons?domain=netflix.com&sz=256"*/ null },
                        name = "Netflix",
                        login = "user@example.com",
                        onClick = {},
                        onCopyClick = {},
                        onEditClick = {},
                        onDeleteClick = {})
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}