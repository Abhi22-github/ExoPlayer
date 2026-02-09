package com.roaa.exoplayer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import com.roaa.exoplayer.ui.PlayerUi

@Composable
fun MediaPickerScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    // Creating player using retain composable
    val player = retain {
        ExoPlayer.Builder(context.applicationContext).build()
    }

    var isPlaying by retain {
        mutableStateOf(false)
    }

    var currentPosition by retain {
        mutableLongStateOf(0L)
    }

    var totalDuration by retain {
        mutableLongStateOf(0L)
    }

    // Creating Launcher for activity result to get URI
    // after getting the URI feed it to player
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            player.apply {
                setMediaItem(MediaItem.fromUri(it))
                prepare()
                play()
            }
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
    ) {

        // Button for selecting the video
        Button(
            onClick = {
                videoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.VideoOnly
                    )
                )
            }
        ) {
            Text("Select Video")
        }

        // Showing the video in content Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            ContentFrame(
                player = player,
                modifier = Modifier
                    .fillMaxSize()
            )

            // Showing the UI on the Player Screen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                PlayerUi(
                    playPauseClick = {},
                    modifier = Modifier,
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    totalDuration = totalDuration
                )
            }
        }


    }
}