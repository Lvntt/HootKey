package dev.banger.hootkey.presentation.ui.common.textfields

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.common.buttons.AlternativeButtonTiny
import dev.banger.hootkey.presentation.ui.theme.BorderWidthSmall
import dev.banger.hootkey.presentation.ui.theme.LightGray
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingTiny
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TextFieldHeightRegular
import dev.banger.hootkey.presentation.ui.theme.TextFieldShapeVerySmall
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.White

@Composable
fun BaseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    decorationBoxModifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onFocusChange: (Boolean) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(key1 = isFocused) {
        onFocusChange(isFocused)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(PaddingTiny)
    ) {
        Text(
            text = hint,
            style = TypeM14,
            color = Secondary60
        )
        // TODO visual transformation
        BasicTextField(
            modifier = decorationBoxModifier
                .background(
                    color = White,
                    shape = TextFieldShapeVerySmall
                )
                .border(
                    width = BorderWidthSmall,
                    brush = if (isFocused) Primary else SolidColor(LightGray),
                    shape = TextFieldShapeVerySmall
                )
                .padding(horizontal = PaddingRegular),
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = TypeM14,
            cursorBrush = SolidColor(Secondary80),
            interactionSource = interactionSource,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        leadingContent?.let {
                            it()
                            Spacer(modifier = Modifier.width(PaddingRegular))
                        }

                        innerTextField()
                    }

                    trailingContent?.let {
                        Spacer(modifier = Modifier.width(PaddingRegular))
                        it()
                    }
                }
            }
        )

        if (isError) {
            errorText?.let {
                Text(
                    text = it,
                    style = TypeM14.copy(
                        brush = Primary
                    ),
                    color = Secondary60
                )
            }
        }
    }
}

@Preview
@Composable
private fun EnabledRegularTextFieldPreview() {
    var value by remember { mutableStateOf("") }

    var isHidden by remember { mutableStateOf(true) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    BaseTextField(
        leadingContent = {
            Icon(
                modifier = if (isTextFieldFocused) {
                    Modifier
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(Primary, blendMode = BlendMode.SrcAtop)
                            }
                        }
                } else {
                    Modifier
                },
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_lock),
                contentDescription = null,
                tint = if (isTextFieldFocused) {
                    Secondary80
                } else {
                    LocalContentColor.current
                }
            )
        },
        trailingContent = {
            AlternativeButtonTiny(
                onClick = { isHidden = !isHidden },
                text = if (isHidden) "View" else "Hide"
            )
        },
        onFocusChange = { isFocused ->
            isTextFieldFocused = isFocused
        },
        hint = "Password",
        isError = true,
        errorText = "Field cannot be empty",
        decorationBoxModifier = Modifier
            .fillMaxWidth()
            .height(TextFieldHeightRegular),
        value = value,
        onValueChange = { value = it }
    )
}