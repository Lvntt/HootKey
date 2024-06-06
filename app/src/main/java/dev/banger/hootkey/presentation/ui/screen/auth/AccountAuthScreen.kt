package dev.banger.hootkey.presentation.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.Constants
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.TextAlertDialog
import dev.banger.hootkey.presentation.viewmodel.AccountAuthViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.qualifier.named

private const val AUTH_TAG = "AUTH_TAG"

@Composable
fun AccountAuthScreen(
    isLogin: Boolean, onNavigateFromBottomHint: () -> Unit, onSuccess: () -> Unit
) {
    val viewModel: AccountAuthViewModel =
        koinViewModel(named(if (isLogin) Constants.LOGIN else Constants.REGISTER))
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.successEventFlow) {
        onSuccess()
    }

    val bottomHint = buildAnnotatedString {
        append(stringResource(id = if (isLogin) R.string.dont_have_account else R.string.already_have_account))
        append(" ")

        pushStringAnnotation(AUTH_TAG, AUTH_TAG)
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold
            )
        ) {
            append(stringResource(id = if (isLogin) R.string.register else R.string.login))
        }
        pop()
    }

    state.errorMessageResId?.let { errorMessageResId ->
        TextAlertDialog(
            title = stringResource(R.string.error),
            message = stringResource(errorMessageResId),
            acceptButtonText = stringResource(R.string.ok),
            onAccept = viewModel::resetErrorMessage,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = state.email,
            onValueChange = viewModel::onEmailChanged,
            isError = !state.emailIsValid,
            enabled = !state.isLoading,
            label = { Text(stringResource(R.string.email_hint)) })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password,
            onValueChange = viewModel::onPasswordChanged,
            isError = !state.passwordIsValid,
            enabled = !state.isLoading,
            label = { Text(stringResource(R.string.password_hint)) },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            onClick = viewModel::authorize,
            enabled = state.isAuthAllowed
        ) {
            if (state.isLoading) CircularProgressIndicator()
            Text(stringResource(if (isLogin) R.string.login else R.string.register))
        }
        ClickableText(modifier = Modifier.align(Alignment.CenterHorizontally),
            text = bottomHint,
            onClick = { offset ->
                bottomHint.getStringAnnotations(tag = AUTH_TAG, start = offset, end = offset)
                    .firstOrNull()?.let { if (!state.isLoading) onNavigateFromBottomHint() }
            })
    }
}