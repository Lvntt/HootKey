package dev.banger.hootkey.presentation.ui.screen.settings

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.intent.SettingsIntent
import dev.banger.hootkey.presentation.state.settings.SettingsEffect
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.buttons.PrimaryButton
import dev.banger.hootkey.presentation.ui.common.switches.HootKeySwitch
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.RoundedCornerShapeRegular
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.TypeM16
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.effects) {
        when (it) {
            SettingsEffect.RedirectToAuth -> TODO()
            SettingsEffect.ShowError -> TODO()
        }
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
                    viewModel.dispatch(SettingsIntent.AutofillChanged(it))
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
                    viewModel.dispatch(SettingsIntent.Logout)
                },
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
    SettingsScreen(onNavigateBack = { })
}
