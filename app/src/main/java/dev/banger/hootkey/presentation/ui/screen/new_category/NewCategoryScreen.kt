package dev.banger.hootkey.presentation.ui.screen.new_category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.Constants.CATEGORY_ICON_KEY
import dev.banger.hootkey.Constants.TEMPLATE_KEY
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiCategoryIcon
import dev.banger.hootkey.presentation.entity.UiTemplateShort
import dev.banger.hootkey.presentation.intent.NewCategoryIntent
import dev.banger.hootkey.presentation.state.new_category.NewCategoryEffect
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButton
import dev.banger.hootkey.presentation.ui.common.textfields.RegularTextField
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.theme.ButtonHeightRegular
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.theme.MainDark
import dev.banger.hootkey.presentation.ui.theme.PaddingLarge
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingSmall
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.RoundedCornerShapeRegular
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.TypeM12
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.NewCategoryViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewCategoryScreen(
    savedStateHandleProvider: () -> SavedStateHandle?,
    onNavigateBack: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToIcons: () -> Unit,
    onSuccess: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewCategoryViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val savedStateHandle = savedStateHandleProvider()
    val coroutineScope = rememberCoroutineScope()
    val snackbarText = stringResource(id = R.string.new_category_error)
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.effects) {
        when (it) {
            NewCategoryEffect.GoToTemplates -> onNavigateToTemplates()
            NewCategoryEffect.GoToIcons -> onNavigateToIcons()
            is NewCategoryEffect.HandleSuccess -> onSuccess(it.categoryId)
            NewCategoryEffect.ShowError -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = snackbarText
                    )
                }
            }
        }
    }

    val selectedIcon = savedStateHandle
        ?.getStateFlow(CATEGORY_ICON_KEY, state.icon.name)
        ?.collectAsStateWithLifecycle()
        ?.value

    selectedIcon?.let {
        val uiCategoryIcon = UiCategoryIcon.valueOf(it)
        viewModel.dispatch(NewCategoryIntent.IconChanged(uiCategoryIcon))
        savedStateHandle.remove<String>(CATEGORY_ICON_KEY)
    }

    val selectedTemplate = savedStateHandle
        ?.getStateFlow<UiTemplateShort?>(TEMPLATE_KEY, null)
        ?.collectAsStateWithLifecycle()
        ?.value

    selectedTemplate?.let {
        viewModel.dispatch(NewCategoryIntent.TemplateChanged(it))
        savedStateHandle.remove<UiTemplateShort>(TEMPLATE_KEY)
    }

    Scaffold(
        modifier = modifier.noRippleClickable {
            focusManager.clearFocus()
        },
        topBar = {
            HootKeyTopBar(
                onNavigateBack = onNavigateBack,
                title = stringResource(id = R.string.create_new_category)
            )
        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.systemBarsPadding(),
                hostState = snackbarHostState
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DefaultBackgroundBrush)
                .padding(contentPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = PaddingMedium),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(White)
                        .noRippleClickable { onNavigateToIcons() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.gradientTint(Primary),
                        imageVector = ImageVector.vectorResource(id = state.icon.icon),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.height(PaddingSmall))

                Text(
                    text = stringResource(id = R.string.choose_icon),
                    style = TypeM12,
                    color = Secondary80
                )
            }

            Box(
                modifier = Modifier
                    .padding(
                        top = PaddingMedium,
                        bottom = PaddingLarge
                    )
                    .clip(RoundedCornerShapeRegular)
                    .fillMaxWidth()
                    .background(White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .padding(PaddingMedium),
                    verticalArrangement = Arrangement.spacedBy(PaddingMedium)
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = PaddingSmall),
                        text = stringResource(id = R.string.category),
                        style = TypeB16,
                        color = MainDark
                    )

                    RegularTextField(
                        value = state.name,
                        onValueChange = {
                            viewModel.dispatch(NewCategoryIntent.NameChanged(it))
                        },
                        hint = stringResource(id = R.string.name),
                        leadingContent = {
                            Icon(
                                modifier = if (state.isNameTextFieldFocused) {
                                    Modifier.gradientTint(Primary)
                                } else {
                                    Modifier
                                },
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_text_field),
                                contentDescription = null,
                                tint = if (state.isNameTextFieldFocused) {
                                    LocalContentColor.current
                                } else {
                                    Secondary80
                                }
                            )
                        },
                        onFocusChange = {
                            viewModel.dispatch(NewCategoryIntent.NameFocusChanged(it))
                        }
                    )

                    RegularTextField(
                        modifier = Modifier.noRippleClickable {
                            viewModel.dispatch(NewCategoryIntent.SelectTemplate)
                            focusManager.clearFocus()
                        },
                        value = state.template?.name ?: "",
                        onValueChange = {},
                        enabled = false,
                        hint = stringResource(id = R.string.select_template),
                        trailingContent = {
                            Icon(
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(
                                        bounded = false,
                                        radius = 16.dp
                                    ),
                                    onClick = {
                                        viewModel.dispatch(NewCategoryIntent.SelectTemplate)
                                        focusManager.clearFocus()
                                    }
                                ),
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = Secondary
                            )
                        }
                    )
                }
            }

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeightRegular),
                onClick = {
                    viewModel.dispatch(NewCategoryIntent.CreateCategory)
                },
                isLoading = state.isLoading,
                text = stringResource(id = R.string.create_category),
                enabled = state.isCreationAllowed
            )
        }
    }
}

@Preview
@Composable
private fun NewCategoryScreenPreview() {
    NewCategoryScreen(
        { null }, {}, {}, {}, {}
    )
}