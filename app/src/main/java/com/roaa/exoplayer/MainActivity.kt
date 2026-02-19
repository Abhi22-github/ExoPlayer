package com.roaa.exoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.roaa.exoplayer.navigation.Destinations
import com.roaa.exoplayer.ui.theme.ExoPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExoPlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val backStack =
                        remember { mutableStateListOf<Destinations>(Destinations.FolderListScreen) }


                    NavDisplay(
                        backStack = backStack,
                        onBack = {
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        },
                        entryProvider = entryProvider {
                            entry<Destinations.FolderListScreen> {
                                FolderListScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    videoFolderClick = { videoFolder ->
                                        backStack.add(Destinations.VideoListScreen(videoFolder))
                                    }
                                )
                            }

                            entry<Destinations.VideoListScreen> {
                                VideoListScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    videoFolder = it.videoFolder,
                                    videoItemClick = { videoItem ->
                                        backStack.add(Destinations.VideoPlayerScreen(videoItem))
                                    }
                                )
                            }

                            entry<Destinations.VideoPlayerScreen> {
                                VideoPlayerScreen(
                                    modifier = Modifier.padding(innerPadding),
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
