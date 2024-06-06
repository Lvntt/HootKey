package dev.banger.hootkey.presentation.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.TextAlertDialog
import dev.banger.hootkey.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(onSuccess: () -> Unit, viewModel: AuthViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.successEventFlow) {
        onSuccess()
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
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password,
            onValueChange = viewModel::onPasswordChanged,
            isError = !state.passwordIsValid,
            enabled = !state.isLoading,
            label = { Text(stringResource(R.string.master_password_hint)) },
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
            Text(stringResource(R.string.login))
        }
    }
}