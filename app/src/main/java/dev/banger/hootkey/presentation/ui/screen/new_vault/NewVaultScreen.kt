package dev.banger.hootkey.presentation.ui.screen.new_vault

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.play.integrity.internal.i
import dev.banger.hootkey.Constants.CREATED_CATEGORY_ID_KEY
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiCategory
import dev.banger.hootkey.presentation.entity.UiEditableTemplateFieldShort
import dev.banger.hootkey.presentation.entity.UiFieldType
import dev.banger.hootkey.presentation.intent.NewVaultIntent
import dev.banger.hootkey.presentation.state.new_vault.NewVaultEffect
import dev.banger.hootkey.presentation.ui.common.LoadingContent
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.buttons.AlternativeButton
import dev.banger.hootkey.presentation.ui.common.buttons.AlternativeButtonTiny
import dev.banger.hootkey.presentation.ui.common.textfields.RegularTextField
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.dialog.PasswordGeneratorDialog
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.theme.MainDark
import dev.banger.hootkey.presentation.ui.theme.PaddingLarge
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingSmall
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.RoundedCornerShapeRegular
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.NewVaultViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewVaultScreen(
    savedStateHandleProvider: () -> SavedStateHandle?,
    onNavigateBack: () -> Unit,
    onNavigateToCategories: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewVaultViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val vaultErrorSnackbarText = stringResource(id = R.string.new_vault_error)
    val categoryErrorSnackbarText = stringResource(id = R.string.selected_category_fetch_error)
    val snackbarHostState = remember { SnackbarHostState() }
    val savedStateHandle = savedStateHandleProvider()

    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.effects) {
        when (it) {
            NewVaultEffect.GoToCategories -> onNavigateToCategories()
            NewVaultEffect.ShowCategoryLoadingError -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = categoryErrorSnackbarText
                    )
                }
            }
            NewVaultEffect.ShowVaultCreationError -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = vaultErrorSnackbarText
                    )
                }
            }
        }
    }

    val selectedCategoryId = savedStateHandle
        ?.getStateFlow<String?>(CREATED_CATEGORY_ID_KEY, null)
        ?.collectAsStateWithLifecycle()
        ?.value

    selectedCategoryId?.let {
        viewModel.dispatch(NewVaultIntent.CategorySelected(it))
        savedStateHandle.remove<String?>(CREATED_CATEGORY_ID_KEY)
    }

    state.generatingPasswordForIndex?.let { index ->
        PasswordGeneratorDialog(
            onDismissRequest = {
                viewModel.dispatch(NewVaultIntent.DismissPasswordGenerator)
            },
            onContinue = {
                // TODO strength?
                viewModel.dispatch(NewVaultIntent.FieldValueChanged(index, it.password))
                viewModel.dispatch(NewVaultIntent.DismissPasswordGenerator)
            }
        )
    }

    Scaffold(
        modifier = modifier.noRippleClickable {
            focusManager.clearFocus()
        },
        topBar = {
            HootKeyTopBar(
                onNavigateBack = onNavigateBack,
                title = stringResource(id = R.string.create_new_vault)
            )
        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.systemBarsPadding(),
                hostState = snackbarHostState
            )
        }
    ) { contentPadding ->
        if (state.isCreationLoading || state.isCategoryLoading) {
            LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DefaultBackgroundBrush)
            )
        } else {
            NewVaultContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DefaultBackgroundBrush)
                    .padding(contentPadding)
                    .padding(horizontal = 20.dp),
                category = state.category,
                onSelectCategoryClick = {
                    viewModel.dispatch(NewVaultIntent.SelectCategory)
                    focusManager.clearFocus()
                },
                fields = state.category?.template?.fields ?: emptyList(),
                // TODO remove
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun NewVaultContent(
    modifier: Modifier = Modifier,
    category: UiCategory?,
    fields: List<UiEditableTemplateFieldShort>,
    onSelectCategoryClick: () -> Unit,
    // TODO remove
    viewModel: NewVaultViewModel,
) {
    LazyColumn(
        modifier = modifier
    ) {
        category?.let { category ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = PaddingMedium),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.gradientTint(Primary),
                            imageVector = ImageVector.vectorResource(id = category.icon.icon),
                            contentDescription = null
                        )
                    }
                }
            }
        }

        item {
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
                        text = stringResource(id = R.string.credentials),
                        style = TypeB16,
                        color = MainDark
                    )

                    RegularTextField(
                        modifier = Modifier.noRippleClickable {
                            onSelectCategoryClick()
                        },
                        value = category?.name ?: "",
                        onValueChange = {},
                        enabled = false,
                        hint = stringResource(id = R.string.select_category),
                        trailingContent = {
                            Icon(
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(
                                        bounded = false,
                                        radius = 16.dp
                                    ),
                                    onClick = onSelectCategoryClick
                                ),
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = Secondary
                            )
                        }
                    )
                }
            }
        }

        itemsIndexed(fields) { index, field ->
            when (field.type) {
                UiFieldType.LOGIN -> Unit
                UiFieldType.PASSWORD -> {
                    Column {
                        RegularTextField(
                            leadingContent = {
                                Icon(
                                    modifier = if (field.isFocused) {
                                        Modifier.gradientTint(Primary)
                                    } else {
                                        Modifier
                                    },
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_lock),
                                    contentDescription = null,
                                    tint = if (field.isFocused) {
                                        Secondary80
                                    } else {
                                        LocalContentColor.current
                                    }
                                )
                            },
                            trailingContent = {
                                AlternativeButtonTiny(
                                    onClick = {
                                        viewModel.dispatch(NewVaultIntent.FieldVisibilityChanged(index, !field.isHidden))
                                    },
                                    text = if (field.isHidden) stringResource(id = R.string.view) else stringResource(id = R.string.hide)
                                )
                            },
                            onFocusChange = { isFocused ->
                                viewModel.dispatch(NewVaultIntent.FieldFocusChanged(index, isFocused))
                            },
                            hint = field.name,
                            value = field.value,
                            onValueChange = {
                                viewModel.dispatch(NewVaultIntent.FieldValueChanged(index, it))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (field.isHidden) PasswordVisualTransformation() else VisualTransformation.None
                        )

                        Spacer(modifier = Modifier.height(PaddingRegular))

                        AlternativeButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.dispatch(NewVaultIntent.OpenPasswordGenerator(index))
                            },
                            text = stringResource(id = R.string.generate_new_password)
                        )
                    }
                }
                UiFieldType.SECRET -> TODO()
                UiFieldType.TEXT -> TODO()
                UiFieldType.LINK -> TODO()
                UiFieldType.DATE -> TODO()
            }
        }
    }
}

@Preview
@Composable
private fun NewVaultScreenPreview() {
    NewVaultScreen({ null }, {}, {})
}