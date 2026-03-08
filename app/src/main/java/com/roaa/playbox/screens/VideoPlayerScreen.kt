package com.roaa.playbox.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.OrientationEventListener
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import com.roaa.playbox.actions.PlayerUiActions
import com.roaa.playbox.composition.localViewModel
import com.roaa.playbox.ui.PlayerUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("EffectKeys")
@Composable
fun VideoPlayerScreen(
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val viewModel = localViewModel.current
    val activity = context as Activity
    var lastOrientation by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()
    var isOrientationLocked by remember { mutableStateOf(false) }
    val isScreenInLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    fun createContentScaleMap(): Map<Int, ContentScale> {
        return mapOf(
            1 to ContentScale.Fit,
            2 to ContentScale.Crop,
            3 to ContentScale.FillBounds,
            4 to ContentScale.FillWidth,
            5 to ContentScale.FillHeight,
            6 to ContentScale.Inside
        )
    }

    val contentScaleMap = createContentScaleMap()
    var currentContentScale by remember { mutableIntStateOf(1) }


    val orientationListener = remember {
        object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return

                val newOrientation = when (orientation) {
                    in 330..360, in 0..30 -> Configuration.ORIENTATION_PORTRAIT
                    in 60..120 -> Configuration.ORIENTATION_LANDSCAPE
                    in 150..210 -> Configuration.ORIENTATION_PORTRAIT
                    in 240..300 -> Configuration.ORIENTATION_LANDSCAPE
                    else -> null
                }
                if (newOrientation != null && newOrientation != lastOrientation) {
                    scope.launch {
                        delay(300)
                        lastOrientation = newOrientation
                        activity.requestedOrientation =
                            if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            } else {
                                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                            }
                    }
                }
            }
        }
    }

    LaunchedEffect(isOrientationLocked) {
        if (isOrientationLocked) {
            orientationListener.disable()
        } else {
            orientationListener.enable()
        }
    }

    DisposableEffect(Unit) {
        orientationListener.enable()
        onDispose {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            orientationListener.disable()
        }
    }

    val videoItem by viewModel.currentVideoItem.collectAsStateWithLifecycle()

    // Creating player using retain composable
    val player = retain {
        ExoPlayer.Builder(context.applicationContext).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA).build(),
                true
            )
        }
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

    var isSeeking by retain {
        mutableStateOf(false)
    }

    var isPlayerUiVisible by retain {
        mutableStateOf(false)
    }

    LaunchedEffect(videoItem) {
        videoItem.uri?.let {
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
        while (isPlaying) {
            if (!isSeeking) {
                currentPosition = player.currentPosition.coerceAtLeast(0)
            }
            delay(250L)
        }
    }

    LaunchedEffect(isPlayerUiVisible, isSeeking, isPlaying) {
        delay(5000L)
        if (!isPlayerUiVisible && !isSeeking) {
            isPlayerUiVisible = false
        }
    }

    Column(
        modifier = modifier
            .background(Color.Black)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
    ) {

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
                    .fillMaxSize(),
                contentScale = contentScaleMap.getOrDefault(currentContentScale, ContentScale.Fit)
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()      // top safe area
                            .navigationBarsPadding()  // bottom safe area // custom top & bottom spacing
                    ) {
                        PlayerUi(
                            modifier = Modifier,
                            isBuffering = isBuffering,
                            isPlaying = isPlaying,
                            currentPosition = currentPosition,
                            duration = totalDuration,
                            isOrientationLocked = isOrientationLocked,
                            isScreenInLandscape = isScreenInLandscape,
                            actions = { action: PlayerUiActions ->
                                when (action) {
                                    PlayerUiActions.LockRotation -> {
                                        isOrientationLocked = !isOrientationLocked
                                    }

                                    is PlayerUiActions.OnSeekPositionChange -> {
                                        isSeeking = true
                                        seekPosition = action.position
                                        currentPosition = action.position
                                    }

                                    PlayerUiActions.OnSeekPositionChangeFinished -> {
                                        player.seekTo(seekPosition)
                                        isSeeking = false
                                    }

                                    PlayerUiActions.PlayPauseClicked -> {
                                        when {
                                            !isPlaying && player.playbackState == Player.STATE_ENDED -> {
                                                player.seekTo(0)
                                                player.play()
                                            }

                                            isPlaying -> player.pause()
                                            !isPlaying -> player.play()
                                        }
                                    }

                                    PlayerUiActions.ChangeContentScale -> {
                                        currentContentScale =
                                            ++currentContentScale % contentScaleMap.size
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}