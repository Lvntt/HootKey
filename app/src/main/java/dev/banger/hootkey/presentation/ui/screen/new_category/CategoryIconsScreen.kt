package dev.banger.hootkey.presentation.ui.screen.new_category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiCategoryIcon
import dev.banger.hootkey.presentation.ui.common.topbar.HootKeyTopBar
import dev.banger.hootkey.presentation.ui.theme.DefaultBackgroundBrush
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint
import dev.banger.hootkey.presentation.ui.utils.noRippleClickable

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryIconsScreen(
    onNavigateBack: () -> Unit,
    onChooseIcon: (UiCategoryIcon) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            HootKeyTopBar(
                onNavigateBack = onNavigateBack,
                title = stringResource(id = R.string.choose_icon)
            )
        }
    ) { contentPadding ->
        FlowRow(
            modifier = modifier
                .fillMaxSize()
                .background(DefaultBackgroundBrush)
                .padding(contentPadding)
                .padding(start = 20.dp, end = 20.dp, top = PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(PaddingMedium),
            horizontalArrangement = Arrangement.Center
        ) {
            UiCategoryIcon.entries.forEach { icon ->
                Row {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(White)
                            .noRippleClickable { onChooseIcon(icon) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.gradientTint(Primary),
                            imageVector = ImageVector.vectorResource(id = icon.icon),
                            contentDescription = null
                        )
                    }

                    Spacer(modifier = Modifier.width(PaddingMedium))
                }
            }
        }
    }
}