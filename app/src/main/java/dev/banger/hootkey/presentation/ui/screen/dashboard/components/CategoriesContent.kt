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
import dev.banger.hootkey.presentation.ui.screen.dashboard.Id
import dev.banger.hootkey.presentation.ui.screen.dashboard.Name

inline fun LazyListScope.categoriesContent(
    crossinline stateProvider: () -> DashboardState,
    crossinline onCategorySelected: (Id, Name) -> Unit,
    noinline onLoadCategoriesRequested: () -> Unit
) {
    item {
        when (stateProvider().categoriesLoadingState) {
            LceState.LOADING -> DashboardLoadingContent()

            LceState.CONTENT -> LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stateProvider().categories) { category ->
                    DashboardCategory(iconResId = category.icon.icon,
                        title = category.name,
                        passwordCount = category.vaultsAmount,
                        onClick = {
                            onCategorySelected(category.id, category.name)
                        })
                }
            }

            LceState.ERROR -> DashboardCategoryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                iconResId = R.drawable.error_refresh_icon,
                title = stringResource(R.string.error_loading_categories),
                subtitle = stringResource(R.string.press_to_refresh),
                onClick = onLoadCategoriesRequested
            )
        }
        if (stateProvider().categoriesLoadingState != LceState.CONTENT || stateProvider().categories.isNotEmpty()) Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}