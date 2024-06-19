package dev.banger.hootkey.presentation.ui.screen.new_template

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.intent.NewTemplateIntent
import dev.banger.hootkey.presentation.state.new_template.NewTemplateEffect
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButton
import dev.banger.hootkey.presentation.ui.common.buttons.TextFieldButton
import dev.banger.hootkey.presentation.ui.common.reorderable.BaseReorderableTextField
import dev.banger.hootkey.presentation.ui.common.reorderable.TemplateFieldsReorderableList
import dev.banger.hootkey.presentation.ui.common.textfields.RegularTextField
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.dialog.EditTemplateFieldDialog
import dev.banger.hootkey.presentation.ui.dialog.NewTemplateFieldDialog
import dev.banger.hootkey.presentation.ui.theme.ButtonHeightRegular
import dev.banger.hootkey.presentation.ui.theme.DarkGray
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.theme.MainDark
import dev.banger.hootkey.presentation.ui.theme.PaddingLarge
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingSmall
import dev.banger.hootkey.presentation.ui.theme.PaddingTiny
import dev.banger.hootkey.presentation.ui.theme.RoundedCornerShapeRegular
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TextFieldHeightRegular
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.NewTemplateViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewTemplateScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewTemplateViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarText = stringResource(id = R.string.new_template_error)
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(
        lazyListState = lazyListState
    ) { from, to ->
        viewModel.dispatch(NewTemplateIntent.MoveField(from.index, to.index))
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.effects) {
        when (it) {
            NewTemplateEffect.HandleSuccess -> onNavigateBack()
            NewTemplateEffect.ShowError -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = snackbarText
                    )
                }
            }
        }
    }

    if (state.isNewFieldDialogShown) {
        NewTemplateFieldDialog(
            onDismissRequest = {
                viewModel.dispatch(NewTemplateIntent.DismissDialog)
            },
            onContinue = {
                viewModel.dispatch(NewTemplateIntent.AddField(it))
                viewModel.dispatch(NewTemplateIntent.DismissDialog)
            }
        )
    }

    if (state.isEditFieldDialogShown) {
        state.fieldToEdit?.let { field ->
            val editFieldKey = "editField${field.name}${field.type}"

            EditTemplateFieldDialog(
                fieldKey = editFieldKey,
                field = field,
                onDismissRequest = {
                    viewModel.dispatch(NewTemplateIntent.DismissDialog)
                },
                onContinue = {
                    viewModel.dispatch(NewTemplateIntent.EditField(it))
                    viewModel.dispatch(NewTemplateIntent.DismissDialog)
                },
                onDeleteField = {
                    viewModel.dispatch(NewTemplateIntent.DeleteField(field))
                    viewModel.dispatch(NewTemplateIntent.DismissDialog)
                }
            )
        }
    }

    Scaffold(
        modifier = modifier.noRippleClickable {
            focusManager.clearFocus()
        },
        topBar = {
            HootKeyTopBar(
                onNavigateBack = onNavigateBack,
                title = stringResource(id = R.string.create_new_template)
            )
        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.systemBarsPadding(),
                hostState = snackbarHostState
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DefaultBackgroundBrush)
                .padding(contentPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        start = 20.dp,
                        end = 20.dp
                    )
                    .background(White),
                state = lazyListState
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(White)
                            .padding(PaddingMedium),
                        text = stringResource(id = R.string.template),
                        style = TypeB16,
                        color = MainDark
                    )
                }

                item {
                    RegularTextField(
                        modifier = Modifier
                            .background(White)
                            .padding(
                                start = PaddingMedium,
                                end = PaddingMedium,
                                bottom = PaddingMedium
                            ),
                        value = state.name,
                        onValueChange = {
                            viewModel.dispatch(NewTemplateIntent.NameChanged(it))
                        },
                        hint = stringResource(id = R.string.name)
                    )
                }

                items(state.fields, key = { it.uuid }) { field ->
                    var itemHeight by remember { mutableIntStateOf(0) }
                    var itemPosY by remember { mutableFloatStateOf(0f) }

                    ReorderableItem(
                        modifier = Modifier
                            .background(White),
                        state = reorderableLazyListState,
                        key = field.uuid
                    ) { isDragging ->
                        val elevation =
                            animateDpAsState(
                                if (isDragging) 16.dp else 0.dp,
                                label = "elevation_anim"
                            )

                        Column(
                            modifier = Modifier.padding(horizontal = PaddingMedium),
//                            modifier = modifier.graphicsLayer {
//                                translationY = if (itemPosY < 0f) -itemPosY
//                                else if (itemPosY > fieldsHeight - itemHeight) -(itemPosY - (fieldsHeight - itemHeight))
//                                else 0f
//                            },
                            verticalArrangement = Arrangement.spacedBy(PaddingTiny)
                        ) {
                            Text(
                                text = stringResource(id = R.string.field),
                                style = TypeM14,
                                color = Secondary60
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .draggableHandle(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(PaddingRegular)
                            ) {
                                BaseReorderableTextField(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(TextFieldHeightRegular)
                                        .shadow(elevation.value),
                                    value = field.name,
                                    leadingContent = if (field.type.icon != null) {
                                        {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = field.type.icon),
                                                contentDescription = null,
                                                tint = Secondary80
                                            )
                                        }
                                    } else null,
                                    trailingContent = {
                                        Icon(
                                            modifier = Modifier.clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = rememberRipple(
                                                    bounded = false,
                                                    radius = 16.dp
                                                ),
                                                onClick = {
                                                    viewModel.dispatch(
                                                        NewTemplateIntent.ShowEditFieldDialog(
                                                            field
                                                        )
                                                    )
                                                }
                                            ),
                                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_edit),
                                            contentDescription = null,
                                            tint = Secondary80
                                        )
                                    }
                                )

                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_drag_indicator),
                                    contentDescription = null,
                                    tint = Secondary
                                )
                            }
                            Spacer(modifier = Modifier.height(PaddingMedium))
                        }
                    }
                }

                // TODO spacers for vertical spacing

                item {
                    TextFieldButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                            .background(White)
                            .padding(
                                start = PaddingMedium,
                                end = PaddingMedium,
                                bottom = PaddingMedium
                            ),
//                        .onGloballyPositioned {
//                            lastButtonHeight = it.size.height
//                        },
                        value = stringResource(id = R.string.create_new_field),
                        onClick = {
                            viewModel.dispatch(NewTemplateIntent.ShowCreateFieldDialog)
                            focusManager.clearFocus()
                        },
                        leadingContent = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_circle),
                                contentDescription = null,
                                tint = Secondary80
                            )
                        },
                        hint = stringResource(id = R.string.new_field)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(PaddingLarge))

                    PrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF0F1F5))
                            .height(ButtonHeightRegular),
                        onClick = {
                            viewModel.dispatch(NewTemplateIntent.CreateTemplate)
                        },
                        text = stringResource(id = R.string.create_template),
                        enabled = state.isCreationAllowed
                    )

                    Spacer(modifier = Modifier.height(PaddingLarge))
                }
//            Box(
//                modifier = Modifier
//                    .padding(
//                        top = 20.dp,
//                        bottom = PaddingLarge
//                    )
//                    .clip(RoundedCornerShapeRegular)
//                    .fillMaxWidth()
//                    .background(White)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(White)
//                        .padding(PaddingMedium),
//                    verticalArrangement = Arrangement.spacedBy(PaddingMedium)
//                ) {
//                    Text(
//                        modifier = Modifier.padding(bottom = PaddingSmall),
//                        text = stringResource(id = R.string.template),
//                        style = TypeB16,
//                        color = MainDark
//                    )
//
//                    RegularTextField(
//                        value = state.name,
//                        onValueChange = {
//                            viewModel.dispatch(NewTemplateIntent.NameChanged(it))
//                        },
//                        hint = stringResource(id = R.string.name)
//                    )
//
//                    TemplateFieldsReorderableList(
//                        fields = state.fields,
//                        onEditClick = {
//                            viewModel.dispatch(NewTemplateIntent.ShowEditFieldDialog(it))
//                        },
//                        onMoveField = { from, to ->
//                            viewModel.dispatch(NewTemplateIntent.MoveField(from.index, to.index))
//                        },
//                        onCreateFieldClick = {
//                            viewModel.dispatch(NewTemplateIntent.ShowCreateFieldDialog)
//                            focusManager.clearFocus()
//                        }
//                    )
//                }
//            }
//
//            PrimaryButton(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(ButtonHeightRegular),
//                onClick = {
//                    viewModel.dispatch(NewTemplateIntent.CreateTemplate)
//                },
//                text = stringResource(id = R.string.create_template),
//                enabled = state.isCreationAllowed
//            )
            }
        }
    }
}

@Preview
@Composable
private fun NewTemplateScreenPreview() {
    NewTemplateScreen(onNavigateBack = {})
}