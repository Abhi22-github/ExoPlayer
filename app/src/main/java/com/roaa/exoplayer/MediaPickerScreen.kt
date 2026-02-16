package com.roaa.exoplayer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import com.roaa.exoplayer.ui.PlayerUi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MediaPickerScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    var seekPosition by retain {
        mutableLongStateOf(0L)
    }

    var totalDuration by retain {
        mutableLongStateOf(0L)
    }

    var isBuffering by retain {
        mutableStateOf(false)
    }

    // Button for selecting the video
    var showVideoList by remember { mutableStateOf(false) }

    var isSeeking by retain{
        mutableStateOf(false)
    }

    var isPlayerUiVisible by retain {
        mutableStateOf(false)
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

    RetainedEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                super.onIsPlayingChanged(playing)
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    totalDuration = player.duration.coerceAtLeast(0)
                }
            }

        }

        player.addListener(listener)
        onRetire {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player, isPlaying, isSeeking) {
        while(isPlaying) {
            if(!isSeeking) {
                currentPosition = player.currentPosition.coerceAtLeast(0)
            }
            delay(250L)
        }
    }

    LaunchedEffect(isPlayerUiVisible, isSeeking, isPlaying) {
        delay(5000L)
        if(!isPlayerUiVisible && !isSeeking) {
            isPlayerUiVisible = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
    ) {

        if (showVideoList) {
            VideoListScreen()
        } else {
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
        }


        // Showing the video in content Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable(
                    interactionSource = null,
                    indication = null
                ) {
                    isPlayerUiVisible = !isPlayerUiVisible
                }
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
                AnimatedVisibility(
                    visible = isPlayerUiVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    PlayerUi(
                        playPauseClick = {
                            when {
                                !isPlaying && player.playbackState == Player.STATE_ENDED -> {
                                    player.seekTo(0)
                                    player.play()
                                }
                                isPlaying -> player.pause()
                                !isPlaying -> player.play()
                            }
                        },
                        modifier = Modifier,
                        isBuffering = isBuffering,
                        isPlaying = isPlaying,
                        currentPosition = currentPosition,
                        duration = totalDuration,
                        onSeekBarPositionChange = {
                            isSeeking = true
                            seekPosition = it
                            currentPosition = it
                        },
                        onSeekBarPositionChangeFinish = {
                            player.seekTo(seekPosition)
                            isSeeking = false
                        }
                    )
                }
            }
        }
    }
}