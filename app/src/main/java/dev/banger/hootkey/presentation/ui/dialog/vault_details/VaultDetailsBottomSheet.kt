package dev.banger.hootkey.presentation.ui.dialog.vault_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiField
import dev.banger.hootkey.presentation.entity.UiFieldType
import dev.banger.hootkey.presentation.ui.common.buttons.AlternativeButtonTiny
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButton
import dev.banger.hootkey.presentation.ui.common.textfields.RegularTextField
import dev.banger.hootkey.presentation.ui.theme.BottomSheetDragHandle
import dev.banger.hootkey.presentation.ui.theme.ButtonShapeSmall
import dev.banger.hootkey.presentation.ui.theme.Gray
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingTiny
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TypeB24
import dev.banger.hootkey.presentation.ui.theme.TypeM16
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable

val fields = listOf(
    UiField(
        name = "Site Address",
        type = UiFieldType.LINK,
        value = "netflix.com"
    ),
    UiField(
        name = "User Name",
        type = UiFieldType.LOGIN,
        value = "hello@designmonk.com"
    ),
    UiField(
        name = "Password",
        type = UiFieldType.PASSWORD,
        value = "password"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultDetailsBottomSheet(
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false
    ) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    width = 32.dp,
                    height = 4.dp,
                    shape = RoundedCornerShape(100.dp),
                    color = BottomSheetDragHandle
                )
            },
            containerColor = Gray,
            windowInsets = WindowInsets(0.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = WindowInsets.systemBars.asPaddingValues()
                        .calculateBottomPadding() + 40.dp
                )
            ) {
                item {
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    modifier = Modifier.size(24.dp),
                                    onClick = {
                                        //Favourite the vault
                                    },
                                ) {
                                    Icon(
                                        modifier = Modifier.gradientTint(Primary),
                                        imageVector = ImageVector.vectorResource(R.drawable.favourite_checked),
                                        contentDescription = null
                                    )
                                }
                                Spacer(modifier = Modifier.width(7.dp))
                                Text(
                                    text = "Blahaj blahaj blahaj blahaj",
                                    style = TypeB24,
                                    color = Secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.size(1.5.dp))
                            Text(
                                text = "Vault Description",
                                style = TypeM16,
                                color = Secondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        IconButton(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Secondary),
                            onClick = onEditClick,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = White
                            )
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.edit_icon),
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        IconButton(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Primary),
                            onClick = onDeleteClick,
                            colors = IconButtonDefaults.iconButtonColors(contentColor = White)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.trash_icon),
                                contentDescription = null
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
                itemsIndexed(fields) { index, field ->
                    val isLastItem = index == fields.lastIndex
                    val isFirstItem = index == 0
                    Box(
                        modifier = Modifier
                            .clip(
                                if (isLastItem)
                                    RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                                else if (isFirstItem)
                                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
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
                            if (isFirstItem) {
                                Spacer(modifier = Modifier.height(PaddingMedium))
                            }

                            RegularTextField(
                                modifier = Modifier.noRippleClickable {
                                    when (field.type) {
                                        UiFieldType.LINK -> {
                                            runCatching {
                                                val url =
                                                    if (
                                                        !field.value.startsWith("http://") &&
                                                        !field.value.startsWith("https://")
                                                    ) {
                                                        "https://${field.value}"
                                                    } else {
                                                        field.value
                                                    }
                                                uriHandler.openUri(url)
                                            }
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
                                    UiFieldType.PASSWORD,
                                    UiFieldType.SECRET -> {
                                        {
                                            AlternativeButtonTiny(
                                                onClick = {
                                                    //Change the field visibility
                                                },
                                                text = if (field.isHidden) stringResource(id = R.string.view) else stringResource(
                                                    id = R.string.hide
                                                )
                                            )
                                        }
                                    }

                                    else -> {
                                        {
                                            IconButton(
                                                modifier = Modifier.size(24.dp),
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(field.value))
                                                },
                                                colors = IconButtonDefaults.iconButtonColors(
                                                    contentColor = Secondary60
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.ic_copy),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                },
                                enabled = false,
                                hint = field.name,
                                value = field.value,
                                onValueChange = {},
                                visualTransformation =
                                if (field.isHidden) field.type.visualTransformation else VisualTransformation.None
                            )

                            when (field.type) {
                                UiFieldType.PASSWORD, UiFieldType.SECRET -> {
                                    Spacer(modifier = Modifier.height(PaddingRegular))

                                    PrimaryButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = ButtonShapeSmall,
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(field.value))
                                        },
                                        text = stringResource(id = R.string.copy, field.name)
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
            }
        }
    }
}