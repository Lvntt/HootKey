package dev.banger.hootkey.presentation.ui.common.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.common.buttons.AlternativeButtonTiny
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TextFieldHeightRegular
import dev.banger.hootkey.presentation.ui.utils.gradientTint

// TODO text color secondary when text field is focused
@Composable
fun RegularTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isError: Boolean = false,
    enabled: Boolean = true,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onFocusChange: (Boolean) -> Unit = {},
    decorationBoxModifier: Modifier = Modifier
        .fillMaxWidth()
        .height(TextFieldHeightRegular),
) {
    BaseTextField(
        modifier = modifier,
        decorationBoxModifier = decorationBoxModifier,
        value = value,
        onValueChange = onValueChange,
        hint = hint,
        singleLine = singleLine,
        isError = isError,
        enabled = enabled,
        errorText = errorText,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        onFocusChange = onFocusChange
    )
}

@Preview
@Composable
private fun RegularTextFieldPreview() {
    var value by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    RegularTextField(
        value = value,
        onValueChange = {
            value = it
            isError = value.isEmpty()
        },
        hint = "Name",
        isError = isError,
        errorText = "Field cannot be empty"
    )
}

@Preview
@Composable
private fun PasswordTextFieldPreview() {
    var value by remember { mutableStateOf("") }

    var isHidden by remember { mutableStateOf(true) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    RegularTextField(
        leadingContent = {
            Icon(
                modifier = if (isTextFieldFocused) {
                    Modifier.gradientTint(Primary)
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
        value = value,
        onValueChange = { value = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isHidden) PasswordVisualTransformation() else VisualTransformation.None
    )
}