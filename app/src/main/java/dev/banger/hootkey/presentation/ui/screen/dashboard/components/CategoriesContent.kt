package dev.banger.hootkey.presentation.ui.screen.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.LceState
import dev.banger.hootkey.presentation.state.dashboard.DashboardState
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.CATEGORIES
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.ERROR_CATEGORIES
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.LOADING_CONTENT
import dev.banger.hootkey.presentation.ui.screen.dashboard.DashboardListContentTypes.MIDDLE_SPACER
import dev.banger.hootkey.presentation.ui.screen.dashboard.Id
import dev.banger.hootkey.presentation.ui.screen.dashboard.Name

inline fun LazyListScope.categoriesContent(
    crossinline stateProvider: () -> DashboardState,
    crossinline onCategorySelected: (Id, Name) -> Unit,
    noinline onLoadCategoriesRequested: () -> Unit
) {
    when (stateProvider().categoriesLoadingState) {
        LceState.LOADING -> item(contentType = LOADING_CONTENT) { DashboardLoadingContent() }

        LceState.CONTENT -> item(contentType = CATEGORIES) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stateProvider().categories, key = { it.id }) { category ->
                    DashboardCategory(iconResId = category.icon.icon,
                        title = category.name,
                        passwordCount = category.vaultsAmount,
                        onClick = {
                            onCategorySelected(category.id, category.name)
                        })
                }
            }
        }

        LceState.ERROR -> item(contentType = ERROR_CATEGORIES) {
            DashboardCategoryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                iconResId = R.drawable.error_refresh_icon,
                title = stringResource(R.string.error_loading_categories),
                subtitle = stringResource(R.string.press_to_refresh),
                onClick = onLoadCategoriesRequested
            )
        }
    }
    if (stateProvider().categoriesLoadingState != LceState.CONTENT || stateProvider().categories.isNotEmpty()) item(
        contentType = MIDDLE_SPACER
    ) {
        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}