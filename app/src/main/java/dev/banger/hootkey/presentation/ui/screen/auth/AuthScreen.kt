package dev.banger.hootkey.presentation.ui.screen.auth

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.state.auth.BiometricError
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.TextAlertDialog
import dev.banger.hootkey.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(onSuccess: () -> Unit, viewModel: AuthViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val biometricLauncher: () -> Unit = {
        context.findActivity()?.let { activity ->
            viewModel.showBiometricPrompt(
                activity,
                activity.getString(R.string.biometric_title),
                activity.getString(R.string.biometric_description),
                activity.getString(R.string.biometric_cancel_text)
            )
        }
    }

    LaunchedEffect(Unit) {
        biometricLauncher()
    }

    ObserveAsEvents(viewModel.successEventFlow) {
        onSuccess()
    }

    ObserveAsEvents(viewModel.biometricErrorFlow) { error ->
        when (error) {
            BiometricError.BiometricNotAvailable -> Toast.makeText(
                context, R.string.biometric_error_not_available, Toast.LENGTH_LONG
            ).show()

            BiometricError.BiometricNotEnrolled -> Toast.makeText(
                context, R.string.biometric_error_not_enrolled, Toast.LENGTH_LONG
            ).show()
        }
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
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                onClick = viewModel::authorize,
                enabled = state.isAuthAllowed
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp), color = LocalContentColor.current
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(stringResource(R.string.login))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp),
                onClick = biometricLauncher,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.fingerprint_icon),
                    contentDescription = null
                )
            }
        }
    }
}

fun Context.findActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}