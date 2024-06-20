package dev.banger.hootkey.presentation.ui.screen.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.Constants.CREATED_CATEGORY_ID_KEY
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiCategoryShort
import dev.banger.hootkey.presentation.intent.CategoriesIntent
import dev.banger.hootkey.presentation.state.categories.CategoriesEffect
import dev.banger.hootkey.presentation.ui.common.LoadingContent
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingSmall
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.RoundedCornerShapeRegular
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.CategoriesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoriesScreen(
    savedStateHandleProvider: () -> SavedStateHandle?,
    onNavigateBack: () -> Unit,
    onCreateCategoryClick: () -> Unit,
    onChooseCategory: (UiCategoryShort) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoriesViewModel = koinViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarText = stringResource(id = R.string.fetching_categories_error)
    val snackbarHostState = remember { SnackbarHostState() }
    val savedStateHandle = savedStateHandleProvider()

    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.effects) {
        when (it) {
            CategoriesEffect.ShowError -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = snackbarText
                    )
                }
            }
        }
    }

    val newCategoryId = savedStateHandle
        ?.getStateFlow<String?>(CREATED_CATEGORY_ID_KEY, null)
        ?.collectAsStateWithLifecycle()
        ?.value

    newCategoryId?.let {
        viewModel.dispatch(CategoriesIntent.LoadCategories)
        savedStateHandle.remove<String?>(CREATED_CATEGORY_ID_KEY)
    }

    Scaffold(
        topBar = {
            HootKeyTopBar(
                onNavigateBack = onNavigateBack,
                title = stringResource(id = R.string.select_category)
            )
        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.systemBarsPadding(),
                hostState = snackbarHostState
            )
        }
    ) { contentPadding ->
        if (state.isLoading) {
            LoadingContent(
                modifier = Modifier
                    .background(DefaultBackgroundBrush)
            )
        } else {
            CategoriesContent(
                modifier = modifier
                    .fillMaxSize()
                    .background(DefaultBackgroundBrush)
                    .padding(contentPadding)
                    .padding(start = 20.dp, end = 20.dp, top = PaddingMedium),
                categories = state.categories,
                onChooseCategory = onChooseCategory,
                onCreateCategoryClick = onCreateCategoryClick
            )
        }
    }
}

@Composable
private fun CategoriesContent(
    categories: List<UiCategoryShort>,
    onCreateCategoryClick: () -> Unit,
    onChooseCategory: (UiCategoryShort) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShapeRegular)
                    .background(White)
                    .noRippleClickable { onCreateCategoryClick() }
            ) {
                Row(
                    modifier = Modifier.padding(PaddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(PaddingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_add),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(id = R.string.create_new_category),
                        style = TypeM14,
                        color = Secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(PaddingMedium))
        }

        items(categories) { category ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShapeRegular)
                    .background(White)
                    .noRippleClickable { onChooseCategory(category) },
            ) {
                Row(
                    modifier = Modifier.padding(PaddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .gradientTint(Primary),
                        imageVector = ImageVector.vectorResource(id = category.icon.icon),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(PaddingRegular))

                    Text(
                        text = category.name,
                        style = TypeM14,
                        color = Secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(PaddingMedium))
        }
    }
}