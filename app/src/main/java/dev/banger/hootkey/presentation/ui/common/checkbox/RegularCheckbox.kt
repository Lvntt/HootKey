package dev.banger.hootkey.presentation.ui.common.checkbox

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import dev.banger.hootkey.presentation.ui.theme.White
import dev.banger.hootkey.presentation.ui.utils.gradientTint

@Composable
fun RegularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null
) {
    val iconResId = if (checked) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked
    val iconBrush = if (checked) Primary else SolidColor(Secondary80)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onCheckedChange(!checked)
            }
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.gradientTint(iconBrush),
                    imageVector = ImageVector.vectorResource(id = iconResId),
                    contentDescription = null,
                )
                if (checked) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_tick),
                        contentDescription = null,
                        tint = White
                    )
                }
            }
        }

        text?.let {
            Text(
                text = it,
                style = TypeM14,
                color = Secondary80
            )
        }
    }

}

@Preview
@Composable
private fun RegularCheckboxPreview() {
    var checked by remember { mutableStateOf(false) }

    RegularCheckbox(
        checked = checked,
        onCheckedChange = { checked = it },
        text = "Uppercase"
    )
}