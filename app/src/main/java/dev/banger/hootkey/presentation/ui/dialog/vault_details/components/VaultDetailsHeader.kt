package dev.banger.hootkey.presentation.ui.dialog.vault_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.TypeB24
import dev.banger.hootkey.presentation.ui.theme.TypeM16
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint

@Composable
fun VaultDetailsHeader(
    name: String,
    category: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = onFavoriteClick,
                ) {
                    Icon(
                        modifier = Modifier.gradientTint(Primary),
                        imageVector = ImageVector.vectorResource(if (isFavorite) R.drawable.favourite_checked else R.drawable.favourite_unchecked),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = name,
                    style = TypeB24,
                    color = Secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.size(1.5.dp))
            Text(
                text = category,
                style = TypeM16,
                color = Secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(7.dp))
        IconButton(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Secondary),
            onClick = onEditClick,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = White
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.edit_icon),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(7.dp))
        IconButton(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Primary),
            onClick = onDeleteClick,
            colors = IconButtonDefaults.iconButtonColors(contentColor = White)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.trash_icon),
                contentDescription = null
            )
        }
    }
}