package dev.banger.hootkey.presentation.ui.common.textfields

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.theme.IconSizeSmall
import dev.banger.hootkey.presentation.ui.theme.PaddingNone
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TextFieldHeightRegular
import dev.banger.hootkey.presentation.ui.theme.TextFieldShapeRegular
import dev.banger.hootkey.presentation.ui.theme.TypeR14
import dev.banger.hootkey.presentation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        modifier = modifier
            .background(
                color = White,
                shape = TextFieldShapeRegular
            )
            .padding(horizontal = PaddingRegular),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TypeR14,
        cursorBrush = SolidColor(Secondary80),
        enabled = enabled,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                ),
                contentPadding = PaddingValues(PaddingNone),
                innerTextField = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(IconSizeSmall),
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_search),
                            tint = Secondary60,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        innerTextField()
                    }
                },
                enabled = enabled,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = {
                    Row {
                        Spacer(modifier = Modifier.width(10.dp + IconSizeSmall))
                        Text(
                            text = placeholder,
                            style = TypeR14,
                            color = Secondary60
                        )
                    }
                }
            )
        }
    )
}

@Preview
@Composable
private fun SearchTextFieldPreview() {
    var value by remember { mutableStateOf("") }

    SearchTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(TextFieldHeightRegular),
        value = value,
        onValueChange = { value = it },
        placeholder = "Search your vaults"
    )
}