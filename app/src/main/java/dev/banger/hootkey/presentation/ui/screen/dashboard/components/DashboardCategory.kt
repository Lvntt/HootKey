package dev.banger.hootkey.presentation.ui.screen.dashboard.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.theme.Gray
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.TypeR10
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint

@Composable
fun DashboardCategory(
    @DrawableRes iconResId: Int,
    title: String,
    passwordCount: Int,
    onClick: () -> Unit
) {
    DashboardCategoryCard(
        modifier = Modifier.width(104.dp),
        iconResId = iconResId,
        title = title,
        subtitle = stringResource(R.string.vault_count, passwordCount),
        onClick = onClick
    )
}

@Composable
fun DashboardCategoryCard(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(White)
            .clickable { onClick() }
            .padding(vertical = 15.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(Gray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.gradientTint(Primary),
                imageVector = ImageVector.vectorResource(iconResId),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = TypeM14,
            color = Secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = TypeR10,
            color = Secondary60,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}