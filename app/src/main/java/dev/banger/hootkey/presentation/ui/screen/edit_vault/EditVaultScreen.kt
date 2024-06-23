package dev.banger.hootkey.presentation.ui.screen.edit_vault

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.Constants.CREATED_CATEGORY_ID_KEY
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiCategory
import dev.banger.hootkey.presentation.entity.UiEditableTemplateFieldShort
import dev.banger.hootkey.presentation.entity.UiFieldType
import dev.banger.hootkey.presentation.intent.EditVaultIntent
import dev.banger.hootkey.presentation.state.edit_vault.EditVaultEffect
import dev.banger.hootkey.presentation.ui.common.LoadingContent
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.buttons.AlternativeButton
import dev.banger.hootkey.presentation.ui.common.buttons.AlternativeButtonTiny
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButton
import dev.banger.hootkey.presentation.ui.common.textfields.RegularTextField
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.dialog.PasswordGeneratorDialog
import dev.banger.hootkey.presentation.ui.dialog.VaultDatePickerDialog
import dev.banger.hootkey.presentation.ui.theme.ButtonHeightRegular
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.theme.MainDark
import dev.banger.hootkey.presentation.ui.theme.PaddingLarge
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingSmall
import dev.banger.hootkey.presentation.ui.theme.PaddingTiny
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.RoundedCornerShapeRegular
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.EditVaultViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditVaultScreen(
    vaultId: String,
    savedStateHandleProvider: () -> SavedStateHandle?,
    onNavigateBack: () -> Unit,
    onNavigateToCategories: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditVaultViewModel = koinViewModel(
        parameters = { parametersOf(vaultId) }
    )
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val vaultEditErrorSnackbarText = stringResource(id = R.string.edit_vault_error)
    val vaultLoadingErrorSnackbarText = stringResource(id = R.string.error_loading_vault)
    val categoryErrorSnackbarText = stringResource(id = R.string.selected_category_fetch_error)
    val snackbarHostState = remember { SnackbarHostState() }
    val savedStateHandle = savedStateHandleProvider()

    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.effects) {
        when (it) {
            EditVaultEffect.GoToCategories -> onNavigateToCategories()
            EditVaultEffect.ShowCategoryLoadingError -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = categoryErrorSnackbarText
                    )
                }
            }
            EditVaultEffect.ShowVaultEditError -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = vaultEditErrorSnackbarText
                    )
                }
            }
            EditVaultEffect.ShowVaultLoadingError -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = vaultLoadingErrorSnackbarText
                    )
                }
            }
            EditVaultEffect.HandleSuccess -> onNavigateBack()
        }
    }

    val selectedCategoryId = savedStateHandle
        ?.getStateFlow<String?>(CREATED_CATEGORY_ID_KEY, null)
        ?.collectAsStateWithLifecycle()
        ?.value

    selectedCategoryId?.let {
        viewModel.dispatch(EditVaultIntent.CategorySelected(it))
        savedStateHandle.remove<String?>(CREATED_CATEGORY_ID_KEY)
    }

    state.generatingPasswordForIndex?.let { index ->
        PasswordGeneratorDialog(
            onDismissRequest = {
                viewModel.dispatch(EditVaultIntent.DismissPasswordGenerator)
            },
            onContinue = {
                // TODO strength?
                viewModel.dispatch(EditVaultIntent.FieldValueChanged(index, it.password))
                viewModel.dispatch(EditVaultIntent.DismissPasswordGenerator)
                focusManager.clearFocus()
            }
        )
    }

    state.pickingDateForIndex?.let { index ->
        VaultDatePickerDialog(
            onDismissRequest = {
                viewModel.dispatch(EditVaultIntent.DismissDatePicker)
            },
            onContinue = {
                viewModel.dispatch(EditVaultIntent.DatePicked(index, it))
                viewModel.dispatch(EditVaultIntent.DismissDatePicker)
                focusManager.clearFocus()
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
        if (state.isCategoryLoading || state.isVaultLoading) {
            LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DefaultBackgroundBrush)
            )
        } else {
            EditVaultContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DefaultBackgroundBrush)
                    .padding(contentPadding)
                    .padding(horizontal = 20.dp),
                category = state.category,
                onSelectCategoryClick = {
                    viewModel.dispatch(EditVaultIntent.SelectCategory)
                    focusManager.clearFocus()
                },
                fields = state.category?.template?.fields ?: emptyList(),
                // TODO remove
                viewModel = viewModel,
                isEditLoading = state.isEditLoading,
                isEditAllowed = state.isEditAllowed,
                name = state.name
            )
        }
    }
}

@Composable
private fun EditVaultContent(
    modifier: Modifier = Modifier,
    category: UiCategory?,
    name: String,
    fields: List<UiEditableTemplateFieldShort>,
    onSelectCategoryClick: () -> Unit,
    isEditLoading: Boolean,
    isEditAllowed: Boolean,
    // TODO remove
    viewModel: EditVaultViewModel,
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
            val areFieldsLoaded = fields.isNotEmpty()
            Box(
                modifier = Modifier
                    .padding(top = PaddingMedium)
                    .clip(
                        if (areFieldsLoaded)
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        else
                            RoundedCornerShapeRegular
                    )
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

                    RegularTextField(
                        value = name,
                        onValueChange = {
                            viewModel.dispatch(EditVaultIntent.NameChanged(it))
                        },
                        hint = stringResource(id = R.string.name),
                        placeholder = stringResource(id = R.string.enter_the_vault_name),
                    )
                }
            }
        }

        itemsIndexed(fields) { index, field ->
            val isLastItem = index == fields.lastIndex
            Box(
                modifier = Modifier
                    .clip(
                        if (isLastItem)
                            RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                        else
                            RectangleShape
                    )
                    .fillMaxWidth()
                    .background(White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = PaddingMedium)
                ) {
                    RegularTextField(
                        leadingContent = if (field.type.icon != null) {
                            {
                                Icon(
                                    modifier = if (field.isFocused) {
                                        Modifier.gradientTint(Primary)
                                    } else {
                                        Modifier
                                    },
                                    imageVector = ImageVector.vectorResource(id = field.type.icon),
                                    contentDescription = null,
                                    tint = if (field.isFocused) {
                                        LocalContentColor.current
                                    } else {
                                        Secondary80
                                    }
                                )
                            }
                        } else null,
                        trailingContent = when (field.type) {
                            UiFieldType.PASSWORD,
                            UiFieldType.SECRET -> {
                                {
                                    AlternativeButtonTiny(
                                        onClick = {
                                            viewModel.dispatch(
                                                EditVaultIntent.FieldVisibilityChanged(
                                                    index,
                                                    !field.isHidden
                                                )
                                            )
                                        },
                                        text = if (field.isHidden) stringResource(id = R.string.view) else stringResource(
                                            id = R.string.hide
                                        )
                                    )
                                }
                            }
                            UiFieldType.DATE -> {
                                {
                                    AlternativeButtonTiny(
                                        onClick = {
                                            viewModel.dispatch(EditVaultIntent.OpenDatePicker(index))
                                        },
                                        text = stringResource(id = R.string.choose)
                                    )
                                }
                            }
                            else -> null
                        },
                        onFocusChange = { isFocused ->
                            viewModel.dispatch(
                                EditVaultIntent.FieldFocusChanged(
                                    index,
                                    isFocused
                                )
                            )
                        },
                        enabled = when (field.type) {
                            UiFieldType.DATE -> false
                            else -> true
                        },
                        hint = field.name,
                        value = field.value,
                        placeholder = stringResource(id = R.string.enter_the_field, field.name),
                        onValueChange = {
                            viewModel.dispatch(EditVaultIntent.FieldValueChanged(index, it))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = field.type.keyboardType),
                        visualTransformation = if (field.isHidden) field.type.visualTransformation else VisualTransformation.None
                    )

                    when (field.type) {
                        UiFieldType.PASSWORD -> {
                            Spacer(modifier = Modifier.height(PaddingRegular))

                            AlternativeButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    viewModel.dispatch(EditVaultIntent.OpenPasswordGenerator(index))
                                },
                                text = stringResource(id = R.string.generate_new_password)
                            )
                        }
                        else -> Unit
                    }

                    Spacer(modifier = Modifier.height(PaddingRegular))

                    if (isLastItem) {
                        Spacer(modifier = Modifier.height(PaddingTiny))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(PaddingLarge))

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeightRegular),
                onClick = {
                    viewModel.dispatch(EditVaultIntent.EditVault)
                },
                isLoading = isEditLoading,
                text = stringResource(id = R.string.save_changes),
                enabled = isEditAllowed
            )

            Spacer(modifier = Modifier.height(PaddingLarge))
        }
    }
}
