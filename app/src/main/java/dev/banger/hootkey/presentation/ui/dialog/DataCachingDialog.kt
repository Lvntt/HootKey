package dev.banger.hootkey.presentation.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.theme.DialogShapeRegular
import dev.banger.hootkey.presentation.ui.theme.MainDark
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable

@Composable
fun DataCachingDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
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
                    text = stringResource(id = R.string.wait),
                    style = TypeB16,
                    color = MainDark
                )

                Spacer(modifier = Modifier.height(PaddingMedium))

                Text(
                    text = stringResource(id = R.string.data_caching_text),
                    style = TypeM14,
                    color = Secondary60
                )

                Spacer(modifier = Modifier.height(PaddingMedium))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .gradientTint(Primary)
                    )
                }

                Spacer(modifier = Modifier.height(PaddingMedium))

                Text(
                    modifier = modifier
                        .noRippleClickable {
                            onDismissRequest()
                        }
                        .align(Alignment.End),
                    text = stringResource(id = R.string.got_it),
                    style = TypeM14,
                    color = Secondary80
                )
            }
        }
    }
}

@Preview
@Composable
private fun DataCachingDialogPreview() {
    DataCachingDialog({})
}