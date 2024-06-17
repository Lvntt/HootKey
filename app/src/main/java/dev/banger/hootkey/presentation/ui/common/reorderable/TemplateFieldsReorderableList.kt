package dev.banger.hootkey.presentation.ui.common.reorderable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.entity.UiTemplateField
import dev.banger.hootkey.presentation.ui.theme.PaddingMedium
import dev.banger.hootkey.presentation.ui.theme.PaddingRegular
import dev.banger.hootkey.presentation.ui.theme.PaddingSmall
import dev.banger.hootkey.presentation.ui.theme.PaddingTiny
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary80
import dev.banger.hootkey.presentation.ui.theme.TextFieldHeightRegular
import dev.banger.hootkey.presentation.ui.theme.TypeM14
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TemplateFieldsReorderableList(
    fields: List<UiTemplateField>,
    onMoveField: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    onEditClick: (UiTemplateField) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onMoveField(from, to)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
    ) {
        items(fields, key = { it.uuid }) { field ->
            ReorderableItem(state = reorderableLazyListState, key = field.uuid) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "elevation_anim")

                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(PaddingTiny)
                ) {
                    Text(
                        modifier = Modifier.padding(start = PaddingSmall),
                        text = stringResource(id = R.string.field),
                        style = TypeM14,
                        color = Secondary60
                    )

                    Row(
                        modifier = Modifier
                            .padding(horizontal = PaddingSmall)
                            .fillMaxWidth()
                            .draggableHandle(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PaddingRegular)
                    ) {
                        BaseReorderableTextField(
                            modifier = Modifier
                                .weight(1f)
                                .height(TextFieldHeightRegular)
                                .shadow(elevation.value),
                            value = field.name,
                            leadingContent = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_profile),
                                    contentDescription = null,
                                    tint = Secondary80
                                )
                            },
                            trailingContent = {
                                Icon(
                                    modifier = Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(
                                            bounded = false,
                                            radius = 16.dp
                                        ),
                                        onClick = {
                                            onEditClick(field)
                                        }
                                    ),
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_edit),
                                    contentDescription = null,
                                    tint = Secondary80
                                )
                            }
                        )

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_drag_indicator),
                            contentDescription = null,
                            tint = Secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(PaddingMedium))
                }
            }
        }
    }
}

@Preview
@Composable
private fun TemplateFieldsReorderableListPreview() {
    TemplateFieldsReorderableList(
        fields = listOf(),
        onMoveField = {_,_ ->},
        onEditClick = {}
    )
}