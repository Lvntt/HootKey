package dev.banger.hootkey.presentation.ui.screen.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiTemplateShort
import dev.banger.hootkey.presentation.state.templates.TemplatesEffect
import dev.banger.hootkey.presentation.ui.common.ObserveAsEvents
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.RoundedCornerShapeRegular
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable
import dev.banger.hootkey.presentation.viewmodel.TemplatesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TemplatesScreen(
    onNavigateBack: () -> Unit,
    onChooseTemplate: (UiTemplateShort) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TemplatesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.effects) {
        when (it) {
            TemplatesEffect.ShowError -> TODO()
        }
    }

    Scaffold(
        topBar = {
            HootKeyTopBar(
                onNavigateBack = onNavigateBack,
                title = stringResource(id = R.string.select_template)
            )
        }
    ) { contentPadding ->
        if (state.isLoading) {
            TemplatesLoading()
        } else {
            TemplatesContent(
                modifier = modifier
                    .fillMaxSize()
                    .background(DefaultBackgroundBrush)
                    .padding(contentPadding)
                    .padding(start = 20.dp, end = 20.dp, top = PaddingMedium),
                templates = state.templates,
                onChooseTemplate = onChooseTemplate
            )
        }

    }
}

@Composable
private fun TemplatesLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.gradientTint(Primary)
        )
    }
}

@Composable
private fun TemplatesContent(
    templates: List<UiTemplateShort>,
    onChooseTemplate: (UiTemplateShort) -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO item "new template"
    // TODO errors (check other TODOs)
    LazyColumn(
        modifier = modifier
    ) {
        items(templates) { template ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShapeRegular)
                    .background(White)
                    .noRippleClickable { onChooseTemplate(template) }
            ) {
                Text(
                    modifier = Modifier.padding(PaddingMedium),
                    text = template.name,
                    style = TypeM14,
                    color = Secondary
                )
            }

            Spacer(modifier = Modifier.height(PaddingMedium))
        }
    }
}