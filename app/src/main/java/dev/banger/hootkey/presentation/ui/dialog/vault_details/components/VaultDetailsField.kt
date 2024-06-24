package dev.banger.hootkey.presentation.ui.dialog.vault_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiField
import dev.banger.hootkey.presentation.entity.UiFieldType
import dev.banger.hootkey.presentation.ui.common.buttons.AlternativeButtonTiny
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButton
import dev.banger.hootkey.presentation.ui.common.textfields.RegularTextField
import dev.banger.hootkey.presentation.ui.theme.ButtonShapeSmall
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingTiny
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable

@Composable
fun VaultDetailsField(
    isLastItem: Boolean,
    isFirstItem: Boolean,
    field: UiField,
    onCopyContent: () -> Unit,
    onOpenLink: () -> Unit,
    onToggleVisibility: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(
                if (isLastItem) RoundedCornerShape(
                    bottomStart = 20.dp, bottomEnd = 20.dp
                )
                else if (isFirstItem) RoundedCornerShape(
                    topStart = 20.dp, topEnd = 20.dp
                )
                else RectangleShape
            )
            .fillMaxWidth()
            .background(White)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = PaddingMedium)
        ) {
            if (isFirstItem) {
                Spacer(modifier = Modifier.height(PaddingMedium))
            }

            RegularTextField(modifier = Modifier.noRippleClickable {
                when (field.type) {
                    UiFieldType.LINK -> {
                        onOpenLink()
                    }

                    else -> Unit
                }
            },
                leadingContent = if (field.type.icon != null) {
                    {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = field.type.icon),
                            contentDescription = null,
                            tint = Secondary80
                        )
                    }
                } else null,
                trailingContent = when (field.type) {
                    UiFieldType.PASSWORD, UiFieldType.SECRET -> {
                        {
                            AlternativeButtonTiny(
                                onClick = onToggleVisibility,
                                text = if (field.isHidden) stringResource(id = R.string.view) else stringResource(
                                    id = R.string.hide
                                )
                            )
                        }
                    }

                    else -> {
                        {
                            IconButton(
                                modifier = Modifier.size(24.dp), onClick = {
                                    onCopyContent()
                                }, colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = Secondary60
                                )
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(
                                        R.drawable.ic_copy
                                    ), contentDescription = null
                                )
                            }
                        }
                    }
                },
                enabled = false,
                hint = field.name,
                value = field.value,
                onValueChange = {},
                visualTransformation = if (field.isHidden) field.type.visualTransformation else VisualTransformation.None
            )

            when (field.type) {
                UiFieldType.PASSWORD, UiFieldType.SECRET -> {
                    Spacer(modifier = Modifier.height(PaddingRegular))

                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(), shape = ButtonShapeSmall, onClick = {
                            onCopyContent()
                        }, text = stringResource(
                            id = R.string.copy, field.name
                        )
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