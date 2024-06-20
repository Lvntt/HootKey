package dev.banger.hootkey.presentation.ui.screen.dashboard.components

import androidx.compose.animation.core.Spring.DampingRatioNoBouncy
import androidx.compose.animation.core.Spring.StiffnessHigh
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import dev.banger.hootkey.R
import dev.banger.hootkey.presentation.ui.theme.Gray
import dev.banger.hootkey.presentation.ui.theme.Primary
import dev.banger.hootkey.presentation.ui.theme.Secondary
import dev.banger.hootkey.presentation.ui.theme.Secondary60
import dev.banger.hootkey.presentation.ui.theme.Secondary70
import dev.banger.hootkey.presentation.ui.theme.TypeM16
import dev.banger.hootkey.presentation.ui.theme.TypeR12
import dev.banger.hootkey.presentation.ui.theme.White

enum class SwipePosition { Swiped, NotSwiped }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VaultShortItem(
    modifier: Modifier = Modifier,
    iconModel: () -> Any?,
    name: String,
    login: String,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyClick: () -> Unit
) {
    val swipeOffset = with(LocalDensity.current) { 87.dp.toPx() }
    val velocityThreshold = with(LocalDensity.current) { 1000.dp.toPx() }
    val anchors = DraggableAnchors {
        SwipePosition.Swiped at -swipeOffset
        SwipePosition.NotSwiped at 0f
    }
    val state = remember {
        AnchoredDraggableState(
            initialValue = SwipePosition.NotSwiped,
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 0.8f },
            velocityThreshold = { velocityThreshold },
            animationSpec = spring(
                dampingRatio = DampingRatioNoBouncy, stiffness = StiffnessHigh
            )
        )
    }

    val progress by animateFloatAsState(
        if (state.targetValue == SwipePosition.Swiped) 1f else 0f, label = "progress"
    )
    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Row(
            modifier = modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(modifier = Modifier
                .graphicsLayer {
                    scaleX = progress
                    scaleY = scaleX
                    alpha = scaleX
                }
                .size(34.dp)
                .clip(CircleShape)
                .background(Secondary),
                onClick = onEditClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = White
                )) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.edit_icon),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(7.dp))
            IconButton(modifier = Modifier
                .graphicsLayer {
                    scaleX = progress
                    scaleY = scaleX
                    alpha = scaleX
                }
                .size(34.dp)
                .clip(CircleShape)
                .background(Primary),
                onClick = onDeleteClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = White)) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.trash_icon),
                    contentDescription = null
                )
            }
        }

        Row(modifier = modifier
            .anchoredDraggable(
                state = state, orientation = Orientation.Horizontal
            )
            .graphicsLayer {
                translationX = state.requireOffset()
            }
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .clickable { onClick() }
            .padding(all = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(65.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Gray), contentAlignment = Alignment.Center
            ) {
                GlideImage(
                    modifier = Modifier
                        .padding(all = 14.dp)
                        .clip(RoundedCornerShape(4.dp)),
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
                onClick = onCopyClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Secondary60)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_copy),
                    contentDescription = null
                )
            }
        }
    }
}