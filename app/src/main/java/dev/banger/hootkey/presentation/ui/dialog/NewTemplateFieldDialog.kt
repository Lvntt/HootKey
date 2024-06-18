package dev.banger.hootkey.presentation.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiFieldType
import dev.banger.hootkey.presentation.entity.UiTemplateField
import dev.banger.hootkey.presentation.intent.NewTemplateFieldIntent
import dev.banger.hootkey.presentation.state.new_template_field.NewTemplateFieldEffect
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.textfields.RegularTextField
import dev.banger.hootkey.presentation.ui.theme.DialogShapeRegular
import dev.banger.hootkey.presentation.ui.theme.MainDark
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.NewTemplateFieldViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewTemplateFieldDialog(
    onDismissRequest: () -> Unit,
    onContinue: (UiTemplateField) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewTemplateFieldViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.effects) {
        when (it) {
            is NewTemplateFieldEffect.AddField -> onContinue(it.field)
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val focusManager = LocalFocusManager.current

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .noRippleClickable { focusManager.clearFocus() },
            colors = CardDefaults.cardColors(containerColor = White),
            shape = DialogShapeRegular
        ) {
            Column(
                modifier = Modifier
                    .padding(PaddingMedium)
            ) {
                Text(
                    text = stringResource(id = R.string.create_new_field),
                    style = TypeB16,
                    color = MainDark
                )

                Spacer(modifier = Modifier.height(PaddingMedium))

                RegularTextField(
                    value = state.field.name,
                    onValueChange = {
                        viewModel.dispatch(NewTemplateFieldIntent.NameChanged(it))
                    },
                    hint = stringResource(id = R.string.enter_field_name),
                )

                Spacer(modifier = Modifier.height(PaddingMedium))

                RegularTextField(
                    modifier = Modifier.noRippleClickable {
                        viewModel.dispatch(NewTemplateFieldIntent.OpenDropdownMenu)
                        focusManager.clearFocus()
                    },
                    value = stringResource(id = state.field.type.text),
                    onValueChange = {},
                    enabled = false,
                    hint = stringResource(id = R.string.choose_field_type),
                    trailingContent = {
                        Icon(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = false,
                                    radius = 16.dp
                                ),
                                onClick = {
                                    viewModel.dispatch(NewTemplateFieldIntent.OpenDropdownMenu)
                                }
                            ),
                            imageVector = if (state.isDropdownMenuShown) {
                                ImageVector.vectorResource(id = R.drawable.ic_chevron_up)
                            } else {
                                ImageVector.vectorResource(id = R.drawable.ic_chevron_down)
                            },
                            contentDescription = null,
                            tint = Secondary
                        )

                        DropdownMenu(
                            modifier = Modifier.background(White),
                            expanded = state.isDropdownMenuShown,
                            onDismissRequest = {
                                viewModel.dispatch(NewTemplateFieldIntent.DismissDropdownMenu)
                            }
                        ) {
                            UiFieldType.entries.forEach {
                                val name = stringResource(id = it.text)

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = name,
                                            style = TypeM14,
                                            color = Secondary80
                                        )
                                    },
                                    onClick = {
                                        viewModel.dispatch(NewTemplateFieldIntent.TypeChanged(it))
                                    }
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(PaddingMedium))

                Row(
                    modifier = modifier
                        .noRippleClickable {
                            onDismissRequest()
                        }
                        .align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        style = TypeM14,
                        color = Secondary60
                    )

                    Spacer(modifier = Modifier.width(PaddingMedium))

                    Row(
                        modifier = Modifier
                            .noRippleClickable {
                                viewModel.dispatch(NewTemplateFieldIntent.AddField)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.continue_text),
                            style = TypeM14,
                            color = Secondary80
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_forward),
                            tint = Secondary80,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}