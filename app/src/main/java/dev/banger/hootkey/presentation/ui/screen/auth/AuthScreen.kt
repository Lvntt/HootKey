package dev.banger.hootkey.presentation.ui.screen.auth

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.state.auth.BiometricError
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.TextAlertDialog
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButton
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButtonTiny
import dev.banger.hootkey.presentation.ui.common.textfields.RegularTextField
import dev.banger.hootkey.presentation.ui.theme.Gray
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.TypeB32
import dev.banger.hootkey.presentation.ui.theme.TypeM24
import dev.banger.hootkey.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(onSuccess: () -> Unit, viewModel: AuthViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val biometricLauncher: () -> Unit = {
        context.findActivity()?.let { activity ->
            if (state.isBiometryOn) {
                viewModel.showBiometricPrompt(
                    activity,
                    activity.getString(R.string.biometric_title),
                    activity.getString(R.string.biometric_description),
                    activity.getString(R.string.biometric_cancel_text)
                )
            }
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
            .paint(painterResource(R.drawable.auth_bg), contentScale = ContentScale.FillBounds)
            .padding(horizontal = 20.dp)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier.size(250.dp).align(Alignment.CenterHorizontally),
            imageVector = ImageVector.vectorResource(R.drawable.auth_logo),
            contentDescription = null
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.welcome_to),
            color = Color.White,
            style = TypeM24,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.app_name),
            style = TypeB32.copy(brush = Primary),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        RegularTextField(modifier = Modifier.fillMaxWidth(),
            value = state.password,
            onValueChange = viewModel::onPasswordChanged,
            isError = !state.passwordIsValid,
            hint = stringResource(R.string.master_password_hint),
            hintColor = Gray,
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            errorText = if (!state.passwordIsValid) stringResource(R.string.password_length_error) else null,
            trailingContent = {
                PrimaryButtonTiny(
                    onClick = { viewModel.togglePasswordVisibility() },
                    text = stringResource(if (state.isPasswordVisible) R.string.hide else R.string.view)
                )
            })
        Spacer(modifier = Modifier.weight(2f))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButton(
                modifier = Modifier
                    .height(52.dp)
                    .weight(1f),
                onClick = viewModel::authorize,
                enabled = state.isAuthAllowed,
                text = stringResource(R.string.login),
                isLoading = state.isLoading
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (state.isBiometryOn) {
                Button(
                    modifier = Modifier
                        .size(52.dp),
                    onClick = biometricLauncher,
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gray,
                        contentColor = Secondary
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.fingerprint_icon),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

fun Context.findActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}