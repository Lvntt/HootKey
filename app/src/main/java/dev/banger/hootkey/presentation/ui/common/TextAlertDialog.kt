package dev.banger.hootkey.presentation.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TextAlertDialog(
    title: String, message: String, acceptButtonText: String, onAccept: () -> Unit
) {
    AlertDialog(onDismissRequest = onAccept, title = {
        Text(text = title)
    }, text = {
        Text(text = message)
    }, confirmButton = {
        Button(
            onClick = onAccept
        ) {
            Text(text = acceptButtonText)
        }
    })
}