package dev.banger.hootkey.presentation.ui.common.textfields

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import dev.banger.hootkey.R
import dev.banger.hootkey.domain.entity.password.SpecialSymbols
import dev.banger.hootkey.presentation.ui.theme.BorderWidthSmall
import dev.banger.hootkey.presentation.ui.theme.LightGray
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TextFieldHeightRegular
import dev.banger.hootkey.presentation.ui.theme.TextFieldShapeVerySmall
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.White

@Composable
fun PasswordGeneratorTextField(
    value: String,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spannablePassword = getSpannablePassword(password = value)

    BasicTextField(
        modifier = modifier
            .background(
                color = White,
                shape = TextFieldShapeVerySmall
            )
            .border(
                width = BorderWidthSmall,
                color = LightGray,
                shape = TextFieldShapeVerySmall
            )
            .padding(start = PaddingRegular),
        value = TextFieldValue(annotatedString = spannablePassword),
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        textStyle = TypeM14,
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    innerTextField()
                }

                IconButton(
                    onClick = onRegenerate
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_refresh),
                        contentDescription = stringResource(id = R.string.regenerate),
                        tint = Secondary80
                    )
                }
            }
        }
    )
}

@Composable
private fun getSpannablePassword(password: String): AnnotatedString {
    return buildAnnotatedString {
        password.forEach {
            when {
                it in SpecialSymbols.values -> appendSpecialSymbol(it)
                it.isDigit() -> appendDigit(it)
                else -> appendLetter(it)
            }
        }
    }
}

private fun AnnotatedString.Builder.appendSpecialSymbol(char: Char) {
    withStyle(
        SpanStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, brush = Primary)
    ) {
        append(char)
    }
}

private fun AnnotatedString.Builder.appendDigit(char: Char) {
    withStyle(
        SpanStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Secondary80)
    ) {
        append(char)
    }
}

private fun AnnotatedString.Builder.appendLetter(char: Char) {
    withStyle(
        SpanStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Secondary60)
    ) {
        append(char)
    }
}

@Preview
@Composable
private fun PasswordGeneratorTextFieldPreview() {
    val password by remember { mutableStateOf("-kY\$pDNT70V5hp0%9tWW") }

    PasswordGeneratorTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(TextFieldHeightRegular),
        value = password,
        onRegenerate = {},
    )
}