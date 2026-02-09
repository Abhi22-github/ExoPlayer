package com.roaa.exoplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.roaa.exoplayer.R

@Composable
fun PlayerUi(
    playPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    currentPosition: Long = 0L,
    totalDuration: Long = 0L
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        IconButton(
            onClick = playPauseClick,
            modifier = Modifier
                .size(100.dp)
        ) {
            val icon = if (isPlaying) R.drawable.pause_icon else R.drawable.play_icon
            Icon(
                modifier = modifier.size(48.dp),
                imageVector =
                    ImageVector.vectorResource(icon),
                contentDescription = "play/pause",
            )
        }

    }

}