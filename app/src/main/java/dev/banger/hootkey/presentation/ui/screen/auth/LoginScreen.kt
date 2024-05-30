package dev.banger.hootkey.presentation.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R

private const val AUTH_TAG = "AUTH_TAG"

@Composable
fun LoginScreen(
    onNavigateToRegistration: () -> Unit
) {
    val bottomHint = buildAnnotatedString {
        append(stringResource(id = R.string.dont_have_account))
        append(" ")

        pushStringAnnotation(AUTH_TAG, AUTH_TAG)
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        ) {
            append(stringResource(id = R.string.register))
        }
        pop()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            label = { Text(stringResource(R.string.email_hint)) })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            label = { Text(stringResource(R.string.password_hint)) },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(modifier = Modifier.fillMaxWidth(), onClick = {}) {
            Text(stringResource(R.string.login))
        }
        ClickableText(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = bottomHint,
            onClick = { offset ->
                bottomHint.getStringAnnotations(tag = AUTH_TAG, start = offset, end = offset)
                    .firstOrNull()?.let { onNavigateToRegistration() }
            })
    }
}