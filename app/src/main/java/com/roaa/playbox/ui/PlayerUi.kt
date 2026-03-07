package com.roaa.playbox.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.roaa.playbox.R
import com.roaa.playbox.actions.PlayerUiActions
import com.roaa.playbox.ui.theme.primaryBlue
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerUi(
    modifier: Modifier = Modifier,
    actions: (PlayerUiActions) -> Unit,
    isPlaying: Boolean = true,
    isOrientationLocked: Boolean = false,
    isScreenInLandscape: Boolean = false,
    isBuffering: Boolean = false,
    currentPosition: Long = 0L,
    duration: Long = 0L
) {

    val playIconRotationState by animateFloatAsState(
        targetValue = if (isPlaying) 180f else 0f,
        label = ""
    )

    val lockIconRotationState by animateFloatAsState(
        targetValue = if (isOrientationLocked) -0f else 0f
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        if (isBuffering) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(20.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 12.dp)
                .align(Alignment.BottomCenter),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(currentPosition),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatDuration(duration),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { newPosition ->
                        actions(PlayerUiActions.OnSeekPositionChange(newPosition.toLong()))
                    },
                    onValueChangeFinished = {
                        actions(PlayerUiActions.OnSeekPositionChangeFinished)
                    },
                    valueRange = 0f..duration.toFloat(),
                    modifier = Modifier
                        .weight(1f),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .shadow(elevation = 4.dp, CircleShape)
                                .background(Color.White)
                        )
                    },
                    track = { sliderState ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(sliderState.value / duration)
                                    .fillMaxHeight()
                                    .background(primaryBlue)
                            )
                        }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        actions(PlayerUiActions.LockRotation)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            rotationZ = lockIconRotationState
                        }
                ) {
                    val icon = if (isOrientationLocked) {
                        if (isScreenInLandscape) {
                            R.drawable.lock_lanscape
                        } else {
                            R.drawable.lock_portrait
                        }
                    } else {
                        R.drawable.rotate_lock
                    }
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector =
                            ImageVector.vectorResource(icon),
                        contentDescription = "lock/unlock",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        actions(PlayerUiActions.PlayPauseClicked)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            rotationZ = playIconRotationState
                        }
                ) {
                    val icon = if (isPlaying) R.drawable.pause_icon else R.drawable.play_icon
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector =
                            ImageVector.vectorResource(icon),
                        contentDescription = "play/pause",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(48.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector =
                            ImageVector.vectorResource(R.drawable.aspect_ratio_icon),
                        contentDescription = "lock/unlock",
                        tint = Color.White
                    )
                }
            }
        }


    }

}

fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
    }
}