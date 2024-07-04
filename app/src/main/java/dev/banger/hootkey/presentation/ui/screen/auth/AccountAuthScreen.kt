package dev.banger.hootkey.presentation.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.TextAlertDialog
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButton
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButtonTiny
import dev.banger.hootkey.presentation.ui.common.textfields.RegularTextField
import dev.banger.hootkey.presentation.ui.theme.Gray
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Primary20
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.TypeB14
import dev.banger.hootkey.presentation.ui.theme.TypeB32
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.TypeM24
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.AccountAuthViewModel
import dev.banger.hootkey.presentation.viewmodel.LoginViewModel
import dev.banger.hootkey.presentation.viewmodel.RegisterViewModel

private const val AUTH_TAG = "AUTH_TAG"

@Composable
fun RegisterScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onNavigateFromBottomHint: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel(factory = viewModelFactory)
) {
    AccountAuthScreen(
        isLogin = false,
        onNavigateFromBottomHint = onNavigateFromBottomHint,
        onSuccess = onSuccess,
        viewModel = viewModel
    )
}

@Composable
fun LoginScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onNavigateFromBottomHint: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = viewModelFactory)
) {
    AccountAuthScreen(
        isLogin = true,
        onNavigateFromBottomHint = onNavigateFromBottomHint,
        onSuccess = onSuccess,
        viewModel = viewModel
    )
}

@Composable
private fun AccountAuthScreen(
    isLogin: Boolean,
    onNavigateFromBottomHint: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AccountAuthViewModel
) {
    val focusManager = LocalFocusManager.current
    val state by viewModel.state.collectAsStateWithLifecycle(lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current)
    val density = LocalDensity.current
    val windowsInsetsIme = WindowInsets.ime
    val windowInsetsNavBars = WindowInsets.navigationBars
    val windowInsetsStatusBars = WindowInsets.statusBars
    val bottomImePadding by remember { derivedStateOf { windowsInsetsIme.getBottom(density) } }
    val bottomNavBarsPadding by remember { derivedStateOf { windowInsetsNavBars.getBottom(density) } }

    ObserveAsEvents(viewModel.successEventFlow) {
        onSuccess()
    }

    val bottomHint = buildAnnotatedString {
        withStyle(
            style = TypeM14.toSpanStyle().copy(color = Primary20)
        ) {
            append(stringResource(id = if (isLogin) R.string.dont_have_account else R.string.already_have_account))
            append(" ")
        }

        pushStringAnnotation(AUTH_TAG, AUTH_TAG)
        withStyle(
            style = TypeB14.toSpanStyle().copy(color = Primary20)
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
            .paint(painterResource(R.drawable.auth_bg), contentScale = ContentScale.FillBounds)
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .noRippleClickable { focusManager.clearFocus() }
            .graphicsLayer {
                translationY =
                    if (bottomImePadding >= bottomNavBarsPadding) -bottomImePadding.toFloat() + bottomNavBarsPadding.toFloat()
                    else 0f
            }
    ) {
        Spacer(
            modifier = Modifier.height(
                with(LocalDensity.current) {
                    windowInsetsStatusBars.getTop(this).toDp()
                }
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.CenterHorizontally),
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
        RegularTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.email,
            onValueChange = viewModel::onEmailChanged,
            isError = !state.emailIsValid,
            hint = stringResource(R.string.email_hint),
            hintColor = Gray,
            strokeBrushInactive = SolidColor(Secondary),
            errorText = if (!state.emailIsValid) stringResource(R.string.invalid_email) else null
        )
        Spacer(modifier = Modifier.height(8.dp))
        RegularTextField(modifier = Modifier.fillMaxWidth(),
            value = state.password,
            onValueChange = viewModel::onPasswordChanged,
            isError = !state.passwordIsValid,
            hint = stringResource(R.string.password_hint),
            hintColor = Gray,
            strokeBrushInactive = SolidColor(Secondary),
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            errorText = if (!state.passwordIsValid) stringResource(R.string.password_length_error) else null,
            trailingContent = {
                PrimaryButtonTiny(
                    onClick = { viewModel.togglePasswordVisibility() },
                    text = stringResource(if (state.isPasswordVisible) R.string.hide else R.string.view)
                )
            })
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth(),
            onClick = viewModel::authorize,
            enabled = state.isAuthAllowed,
            text = stringResource(if (isLogin) R.string.login else R.string.register),
            isLoading = state.isLoading
        )
        Spacer(modifier = Modifier.height(12.dp))
        ClickableText(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(PaddingRegular),
            text = bottomHint,
            onClick = { offset ->
                bottomHint.getStringAnnotations(tag = AUTH_TAG, start = offset, end = offset)
                    .firstOrNull()?.let { if (!state.isLoading) onNavigateFromBottomHint() }
            })
    }
}