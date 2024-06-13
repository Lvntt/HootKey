package dev.banger.hootkey.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.banger.hootkey.presentation.entity.UiGeneratedPassword
import dev.banger.hootkey.presentation.ui.common.buttons.AlternativeButtonSmall
import dev.banger.hootkey.presentation.ui.common.progress.PasswordStrengthIndicator
import dev.banger.hootkey.presentation.ui.dialog.PasswordGeneratorDialog
import dev.banger.hootkey.presentation.ui.theme.PaddingLarge
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingXLarge

@Composable
fun TestScreen(modifier: Modifier = Modifier) {
    var generatedPassword by remember { mutableStateOf(UiGeneratedPassword()) }
    var isDialogOpened by remember { mutableStateOf(false) }

    if (isDialogOpened) {
        PasswordGeneratorDialog(
            onDismissRequest = { isDialogOpened = false },
            onContinue = {
                generatedPassword = it
                isDialogOpened = false
            }
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = PaddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = generatedPassword.password.ifEmpty { "The password will be here" }
            )

            if (generatedPassword.password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(PaddingMedium))

                PasswordStrengthIndicator(strength = generatedPassword.strength)
            }

            Spacer(modifier = Modifier.height(PaddingXLarge))
            
            AlternativeButtonSmall(
                modifier = Modifier.fillMaxWidth(),
                onClick = { isDialogOpened = !isDialogOpened },
                text = "Generate new password"
            )
        }
    }
}