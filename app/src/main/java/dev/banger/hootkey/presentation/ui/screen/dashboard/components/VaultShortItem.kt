package dev.banger.hootkey.presentation.ui.screen.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.theme.Gray
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary70
import dev.banger.hootkey.presentation.ui.theme.TypeM16
import dev.banger.hootkey.presentation.ui.theme.TypeR12
import dev.banger.hootkey.presentation.ui.theme.White

@Composable
fun VaultShortItem(
    modifier: Modifier = Modifier, iconModel: () -> Any?, name: String, login: String, onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .clickable { onClick() }
            .padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(65.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Gray), contentAlignment = Alignment.Center
        ) {
            GlideImage(
                modifier = Modifier.padding(all = 14.dp).clip(RoundedCornerShape(4.dp)),
                imageOptions = ImageOptions(contentScale = ContentScale.Fit),
                imageModel = iconModel
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                style = TypeM16,
                color = Secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = login,
                style = TypeR12,
                color = Secondary70,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
        }
        IconButton(
            modifier = Modifier.size(24.dp),
            onClick = {},
            colors = IconButtonDefaults.iconButtonColors(contentColor = Secondary60)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_copy),
                contentDescription = null
            )
        }
    }
}