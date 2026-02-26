package com.roaa.exoplayer

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.roaa.exoplayer.navigation.Destinations
import com.roaa.exoplayer.ui.theme.ExoPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            ExoPlayerTheme {
                var shouldHideAppBar by rememberSaveable { mutableStateOf(false) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AnimatedVisibility(
                            visible = !shouldHideAppBar,
                            enter = fadeIn() + slideInVertically { -it },
                            exit = fadeOut() + slideOutVertically { -it }
                        ) {
                            TopAppBar(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding(),
                                onMoreClick = {}
                            )
                        }
                    }
                ) { innerPadding ->
                    val backStack =
                        rememberSaveable { mutableStateListOf<Destinations>(Destinations.FolderListScreen) }

                    NavDisplay(
                        modifier = Modifier,
                        backStack = backStack,
                        onBack = {
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        },
                        transitionSpec = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(350)
                            ) + fadeIn() togetherWith slideOutHorizontally(
                                targetOffsetX = { -it / 4 },
                                animationSpec = tween(350)
                            ) + fadeOut()
                        }, // normal back (pop)
                        popTransitionSpec = {
                            slideInHorizontally(
                                initialOffsetX = { -it / 4 },
                                animationSpec = tween(350)
                            ) + fadeIn() togetherWith slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(350)
                            )
                        },
                        predictivePopTransitionSpec = {
                            // usually a shorter offset so the previous screen peeks in during swipe
                            slideInHorizontally(
                                initialOffsetX = { -it / 4 },
                                animationSpec = tween(350)
                            ) + fadeIn() togetherWith slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(350)
                            )
                        },
                        entryProvider = entryProvider {
                            entry<Destinations.FolderListScreen> {
                                shouldHideAppBar = false
                                FolderListScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    videoFolderClick = { videoFolder ->
                                        backStack.add(Destinations.VideoListScreen(videoFolder))
                                    }
                                )
                            }

                            entry<Destinations.VideoListScreen> {
                                shouldHideAppBar = false
                                VideoListScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    videoFolder = it.videoFolder,
                                    videoItemClick = { videoItem ->
                                        backStack.add(Destinations.VideoPlayerScreen(videoItem))
                                    }
                                )
                            }

                            entry<Destinations.VideoPlayerScreen> {
                                shouldHideAppBar = true
                                VideoPlayerScreen(
                                    modifier = Modifier,
                                    videoItem = it.videoItem
                                )
                            }
                        }
                    )

                }
            }
        }
    }
}
