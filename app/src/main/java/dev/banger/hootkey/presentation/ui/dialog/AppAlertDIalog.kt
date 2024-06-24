package dev.banger.hootkey.presentation.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.theme.DialogShapeRegular
import dev.banger.hootkey.presentation.ui.theme.MainDark
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable

@Composable
fun AppAlertDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onPositiveAction: () -> Unit,
    onNegativeAction: () -> Unit = onDismissRequest,
    title: String,
    message: String,
    positiveButtonText: String = stringResource(id = R.string.continue_text),
    negativeButtonText: String = stringResource(R.string.cancel),
    isLoading: Boolean = false
) {
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
                    .fillMaxWidth()
                    .padding(PaddingMedium)
            ) {
                Text(
                    text = title, style = TypeB16, color = MainDark
                )

                Spacer(modifier = Modifier.height(PaddingMedium))

                Text(
                    text = message, style = TypeM14, color = Secondary60
                )

                Spacer(modifier = Modifier.height(PaddingMedium))

                Row(
                    modifier = modifier
                        .noRippleClickable {
                            onNegativeAction()
                        }
                        .align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = negativeButtonText, style = TypeM14, color = Secondary60
                    )

                    Spacer(modifier = Modifier.width(PaddingMedium))

                    Row(
                        modifier = Modifier.noRippleClickable {
                            onPositiveAction()
                        }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = positiveButtonText, style = TypeM14, color = Secondary80
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        if (!isLoading) {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_forward),
                                tint = Secondary80,
                                contentDescription = null
                            )
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp), color = Secondary80
                            )
                        }
                    }
                }
            }
        }
    }
}