package dev.banger.hootkey.presentation.ui.screen.new_template

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.intent.NewTemplateIntent
import dev.banger.hootkey.presentation.state.new_template.NewTemplateEffect
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.reorderable.TemplateFieldsReorderableList
import dev.banger.hootkey.presentation.ui.common.textfields.RegularTextField
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.dialog.NewTemplateFieldDialog
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.theme.MainDark
import dev.banger.hootkey.presentation.ui.theme.PaddingLarge
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingSmall
import dev.banger.hootkey.presentation.ui.theme.RoundedCornerShapeRegular
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.NewTemplateViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewTemplateScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewTemplateViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.effects) {
        when (it) {
            NewTemplateEffect.HandleSuccess -> onNavigateBack()
            NewTemplateEffect.ShowError -> TODO()
        }
    }

    val focusManager = LocalFocusManager.current

    if (state.isNewFieldDialogShown) {
        NewTemplateFieldDialog(
            onDismissRequest = {
                viewModel.dispatch(NewTemplateIntent.DismissDialog)
            },
            onContinue = {
                viewModel.dispatch(NewTemplateIntent.AddField(it))
                viewModel.dispatch(NewTemplateIntent.DismissDialog)
            }
        )
    }

    if (state.isEditFieldDialogShown) {
        // TODO
    }

    Scaffold(
        modifier = modifier.noRippleClickable {
            focusManager.clearFocus()
        },
        topBar = {
            HootKeyTopBar(
                onNavigateBack = onNavigateBack,
                title = stringResource(id = R.string.create_new_template)
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DefaultBackgroundBrush)
                .padding(contentPadding)
//                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp,
                        bottom = PaddingLarge
                    )
                    .clip(RoundedCornerShapeRegular)
                    .fillMaxWidth()
                    .background(White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .padding(PaddingMedium),
                    verticalArrangement = Arrangement.spacedBy(PaddingMedium)
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = PaddingSmall),
                        text = stringResource(id = R.string.template),
                        style = TypeB16,
                        color = MainDark
                    )

                    RegularTextField(
                        value = state.name,
                        onValueChange = {
                            viewModel.dispatch(NewTemplateIntent.NameChanged(it))
                        },
                        hint = stringResource(id = R.string.name)
                    )

                    TemplateFieldsReorderableList(
                        fields = state.fields,
                        onEditClick = {
                            viewModel.dispatch(NewTemplateIntent.ShowEditFieldDialog(it))
                        },
                        onMoveField = { from, to ->
                            viewModel.dispatch(NewTemplateIntent.MoveField(from.index, to.index))
                        },
                        onCreateFieldClick = {
                            viewModel.dispatch(NewTemplateIntent.ShowCreateFieldDialog)
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun NewTemplateScreenPreview() {
    NewTemplateScreen(onNavigateBack = {})
}