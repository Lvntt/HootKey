package dev.banger.hootkey.presentation.ui.screen.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.AutofillServiceContract
import dev.banger.hootkey.presentation.intent.SettingsIntent
import dev.banger.hootkey.presentation.state.settings.SettingsEffect
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButton
import dev.banger.hootkey.presentation.ui.common.switches.HootKeySwitch
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.dialog.AppAlertDialog
import dev.banger.hootkey.presentation.ui.screen.auth.findActivity
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.RoundedCornerShapeRegular
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.TypeM16
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarText = stringResource(id = R.string.request_error)
    val snackbarHostState = remember { SnackbarHostState() }

    val autofillLauncher = rememberLauncherForActivityResult(
        contract = AutofillServiceContract()
    ) { result ->
        if (result) {
            viewModel.dispatch(SettingsIntent.AutofillServiceChosen(context.findActivity()!!))
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle(lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current)
    ObserveAsEvents(viewModel.effects) {
        when (it) {
            SettingsEffect.Logout -> onLogout()
            SettingsEffect.ShowError -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = snackbarText
                    )
                }
            }
            is SettingsEffect.ShowAutofillSettings -> autofillLauncher.launch(it.intent)
        }
    }

    if (state.isLogoutDialogShown) {
        AppAlertDialog(
            onDismissRequest = {
                viewModel.dispatch(SettingsIntent.DismissLogoutDialog)
            },
            onPositiveAction = {
                viewModel.dispatch(SettingsIntent.Logout)
            },
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.confirm_logout),
            isLoading = state.isLogoutLoading
        )
    }

    Scaffold(
        topBar = {
            HootKeyTopBar(
                onNavigateBack = onNavigateBack,
                title = stringResource(id = R.string.settings)
            )
        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.systemBarsPadding(), hostState = snackbarHostState
            )
        }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(DefaultBackgroundBrush)
                .padding(contentPadding)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsItem(
                text = stringResource(id = R.string.biometry_auth_settings),
                isChecked = state.isBiometryOn,
                onCheckedChange = {
                    viewModel.dispatch(SettingsIntent.BiometryChanged(it))
                }
            )
            Spacer(modifier = Modifier.height(PaddingRegular))
            SettingsItem(
                text = stringResource(id = R.string.autofill_settings),
                isChecked = state.isAutofillOn,
                onCheckedChange = {
                    viewModel.dispatch(SettingsIntent.AutofillChanged(it, context.findActivity()!!))
                }
            )
            Spacer(modifier = Modifier.height(PaddingRegular))
            SettingsItem(
                text = stringResource(id = R.string.enable_sync_settings),
                isChecked = state.isSyncOn,
                onCheckedChange = {
                    viewModel.dispatch(SettingsIntent.SyncChanged(it))
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                onClick = {
                    viewModel.dispatch(SettingsIntent.ShowLogoutDialog)
                },
                isLoading = state.isLogoutLoading,
                text = stringResource(id = R.string.logout)
            )
            Spacer(modifier = Modifier.height(PaddingMedium))
        }
    }
}

@Composable
private fun SettingsItem(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShapeRegular)
            .fillMaxWidth()
            .background(White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingRegular),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = TypeM16,
                color = Secondary
            )

            HootKeySwitch(
                isChecked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen({}, {})
}
