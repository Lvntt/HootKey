package dev.banger.hootkey.presentation.ui.dialog

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiGeneratedPassword
import dev.banger.hootkey.presentation.intent.PasswordGeneratorIntent
import dev.banger.hootkey.presentation.state.password_generator.PasswordGeneratorEffect
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
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
import dev.banger.hootkey.presentation.viewmodel.PasswordGeneratorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PasswordGeneratorDialog(
    viewModel: PasswordGeneratorViewModel = koinViewModel(),
    onDismissRequest: () -> Unit,
    onContinue: (UiGeneratedPassword) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    ObserveAsEvents(viewModel.effects) {
        when (it) {
            PasswordGeneratorEffect.ShowEmptyCharPoolError -> Toast.makeText(
                context,
                R.string.empty_char_pool_error,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

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
                    value = state.password.password,
                    onRegenerate = {
                        viewModel.dispatch(PasswordGeneratorIntent.RegeneratePassword)
                    }
                )

                Spacer(modifier = Modifier.height(PaddingSmall))

                PasswordStrengthIndicator(strength = state.password.strength)

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
                        value = state.lengthSliderValue,
                        onValueChange = {
                            viewModel.dispatch(PasswordGeneratorIntent.ChangeLengthSliderValue(it))
                        },
                        onValueChangeFinished = {
                            viewModel.dispatch(PasswordGeneratorIntent.ChangeLength(it))
                        }
                    )
                    Text(
                        modifier = Modifier.width(20.dp),
                        text = "${state.lengthSliderValue.toInt()}",
                        style = TypeM14,
                        color = Secondary80
                    )
                }

                Spacer(modifier = Modifier.height(PaddingSmall))

                Row {
                    Column(verticalArrangement = Arrangement.spacedBy(PaddingSmall)) {
                        RegularCheckbox(
                            checked = state.options.hasNumbers,
                            onCheckedChange = {
                                viewModel.dispatch(PasswordGeneratorIntent.ChangeHasNumbers(it))
                            },
                            text = stringResource(id = R.string.numbers)
                        )
                        RegularCheckbox(
                            checked = state.options.hasSymbols,
                            onCheckedChange = {
                                viewModel.dispatch(PasswordGeneratorIntent.ChangeHasSymbols(it))
                            },
                            text = stringResource(id = R.string.symbols)
                        )
                    }

                    Spacer(modifier = Modifier.width(PaddingXLarge))

                    Column(verticalArrangement = Arrangement.spacedBy(PaddingSmall)) {
                        RegularCheckbox(
                            checked = state.options.hasUppercase,
                            onCheckedChange = {
                                viewModel.dispatch(PasswordGeneratorIntent.ChangeHasUppercase(it))
                            },
                            text = stringResource(id = R.string.uppercase)
                        )
                        RegularCheckbox(
                            checked = state.options.hasLowercase,
                            onCheckedChange = {
                                viewModel.dispatch(PasswordGeneratorIntent.ChangeHasLowercase(it))
                            },
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
                                onContinue(state.password)
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
