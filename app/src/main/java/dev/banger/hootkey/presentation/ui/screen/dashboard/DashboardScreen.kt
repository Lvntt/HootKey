package dev.banger.hootkey.presentation.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.common.textfields.SearchTextField
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.DashboardCategory
import dev.banger.hootkey.presentation.ui.screen.dashboard.components.VaultShortItem
import dev.banger.hootkey.presentation.ui.theme.Gray
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.TypeB16
import dev.banger.hootkey.presentation.ui.theme.TypeM12

@Composable
fun DashboardScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray)
            .systemBarsPadding()
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SearchTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(start = 20.dp, end = 20.dp),
                value = "",
                onValueChange = {},
                placeholder = stringResource(R.string.search_vaults),
                enabled = false
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(count = 5) {
                    DashboardCategory(iconResId = R.drawable.finance_icon,
                        title = "Finance $it",
                        passwordCount = 5 * it,
                        onClick = {})
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .weight(1f),
                    text = stringResource(R.string.recently_used),
                    style = TypeB16,
                    color = Secondary,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .weight(1f),
                    text = stringResource(R.string.see_more),
                    style = TypeM12,
                    color = Secondary60,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(count = 5) {
            VaultShortItem(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
                iconModel = { "https://www.google.com/s2/favicons?domain=netflix.com&sz=256" },
                name = "Netflix",
                login = "user@example.com",
                onClick = {},
                onCopyClick = {},
                onEditClick = {},
                onDeleteClick = {})
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}