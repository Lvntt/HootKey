package dev.banger.hootkey.presentation.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiGeneratedPassword
import dev.banger.hootkey.presentation.entity.UiPasswordStrength
import dev.banger.hootkey.presentation.ui.common.checkbox.RegularCheckbox
import dev.banger.hootkey.presentation.ui.common.progress.PasswordStrengthIndicator
import dev.banger.hootkey.presentation.ui.common.sliders.PasswordLengthSlider
import dev.banger.hootkey.presentation.ui.common.textfields.PasswordGeneratorTextField
import dev.banger.hootkey.presentation.ui.theme.DialogShapeRegular
import dev.banger.hootkey.presentation.ui.theme.MainDark
import dev.banger.hootkey.presentation.ui.theme.PaddingLarge
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingSmall
import dev.banger.hootkey.presentation.ui.theme.PaddingXLarge
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable

@Composable
fun GeneratePasswordDialog(
    password: UiGeneratedPassword,
    passwordLengthSliderValue: Float,
    containsNumbers: Boolean,
    onContainsNumbersChange: (Boolean) -> Unit,
    containsSymbols: Boolean,
    onContainsSymbolsChange: (Boolean) -> Unit,
    containsUppercase: Boolean,
    onContainsUppercaseChange: (Boolean) -> Unit,
    containsLowercase: Boolean,
    onContainsLowercaseChange: (Boolean) -> Unit,
    onPasswordLengthChange: (Float) -> Unit,
    onRegenerate: () -> Unit,
    onDismissRequest: () -> Unit,
    onContinue: (UiGeneratedPassword) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            shape = DialogShapeRegular
        ) {
            Column(
                modifier = Modifier
                    .padding(PaddingMedium)
            ) {
                Text(
                    text = stringResource(id = R.string.generate_password),
                    style = TypeB16,
                    color = MainDark
                )

                Spacer(modifier = Modifier.height(PaddingMedium))

                PasswordGeneratorTextField(
                    value = password.password,
                    onRegenerate = onRegenerate
                )

                Spacer(modifier = Modifier.height(PaddingSmall))

                PasswordStrengthIndicator(strength = password.strength)

                Spacer(modifier = Modifier.height(PaddingMedium))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PaddingSmall)
                ) {
                    Text(
                        text = stringResource(id = R.string.length),
                        style = TypeM14,
                        color = Secondary80
                    )
                    PasswordLengthSlider(
                        modifier = Modifier.weight(1f),
                        value = passwordLengthSliderValue,
                        onValueChange = onPasswordLengthChange
                    )
                    Text(
                        modifier = Modifier.width(20.dp),
                        text = "${passwordLengthSliderValue.toInt()}",
                        style = TypeM14,
                        color = Secondary80
                    )
                }

                Spacer(modifier = Modifier.height(PaddingSmall))

                Row {
                    Column(verticalArrangement = Arrangement.spacedBy(PaddingSmall)) {
                        RegularCheckbox(
                            checked = containsNumbers,
                            onCheckedChange = onContainsNumbersChange,
                            text = stringResource(id = R.string.numbers)
                        )
                        RegularCheckbox(
                            checked = containsUppercase,
                            onCheckedChange = onContainsUppercaseChange,
                            text = stringResource(id = R.string.uppercase)
                        )
                    }

                    Spacer(modifier = Modifier.width(PaddingXLarge))

                    Column(verticalArrangement = Arrangement.spacedBy(PaddingSmall)) {
                        RegularCheckbox(
                            checked = containsSymbols,
                            onCheckedChange = onContainsSymbolsChange,
                            text = stringResource(id = R.string.symbols)
                        )
                        RegularCheckbox(
                            checked = containsLowercase,
                            onCheckedChange = onContainsLowercaseChange,
                            text = stringResource(id = R.string.lowercase)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(PaddingLarge))

                Row(
                    modifier = Modifier
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
                                onContinue(password)
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

@Preview
@Composable
private fun GeneratePasswordDialogPreview() {
    var isShown by remember { mutableStateOf(true) }
    val password by remember {
        mutableStateOf(
            UiGeneratedPassword(
                password = "-kY\$pDNT70V5hp0%9tWW",
                strength = UiPasswordStrength.VERY_STRONG
            )
        )
    }
    var length by remember { mutableFloatStateOf(20f) }
    var containsNumbers by remember { mutableStateOf(true) }
    var containsSymbols by remember { mutableStateOf(true) }
    var containsUppercase by remember { mutableStateOf(true) }
    var containsLowercase by remember { mutableStateOf(true) }

    if (isShown) {
        GeneratePasswordDialog(
            onDismissRequest = {
                isShown = !isShown
            },
            password = password,
            onRegenerate = {},
            passwordLengthSliderValue = length,
            onPasswordLengthChange = { length = it },
            containsNumbers = containsNumbers,
            onContainsNumbersChange = { containsNumbers = it },
            containsSymbols = containsSymbols,
            onContainsSymbolsChange = { containsSymbols = it },
            containsUppercase = containsUppercase,
            onContainsUppercaseChange = { containsUppercase = it },
            containsLowercase = containsLowercase,
            onContainsLowercaseChange = { containsLowercase = it },
            onContinue = {

            }
        )
    }
}