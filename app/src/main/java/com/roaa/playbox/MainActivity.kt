package com.roaa.playbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.roaa.playbox.actions.TopAppBarActions
import com.roaa.playbox.composition.localViewModel
import com.roaa.playbox.navigation.Destinations
import com.roaa.playbox.screens.FolderListScreen
import com.roaa.playbox.screens.PlayBoxTopAppBar
import com.roaa.playbox.screens.VideoListScreen
import com.roaa.playbox.screens.VideoPlayerScreen
import com.roaa.playbox.ui.theme.ExoPlayerTheme
import com.roaa.playbox.utils.BottomAppBarState
import com.roaa.playbox.utils.VideoRepository
import com.roaa.playbox.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: MainViewModel by viewModels()
        setContent {
            ExoPlayerTheme {
                // initializing the repo with context
                val context = LocalContext.current
                viewModel.initializeVideoRepository(VideoRepository(context))

                val backStack = rememberNavBackStack(Destinations.FolderListScreen)
                val currentDestination by remember { derivedStateOf { backStack.lastOrNull() } }

                // for hiding the app bar.
                val shouldHideAppBar by remember { derivedStateOf { currentDestination is Destinations.VideoPlayerScreen } }

                // for changing the App name and icon to back button
                val shouldShowBackButton by remember { derivedStateOf { currentDestination is Destinations.VideoListScreen } }

                // for app toolbar selection
                var selectedAppBarScreen by remember { mutableStateOf(BottomAppBarState.VideoScreen) }

                fun navigateBack() {
                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                }

                val modifier = Modifier.background(MaterialTheme.colorScheme.background)

                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

                CompositionLocalProvider(
                    localViewModel provides viewModel
                ) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            AnimatedVisibility(
                                visible = !shouldHideAppBar,
                                enter = fadeIn() + slideInVertically { -it },
                                exit = fadeOut() + slideOutVertically { -it }
                            ) {
                                Column() {
                                    PlayBoxTopAppBar(
                                        modifier = modifier
                                            .fillMaxWidth()
                                            .statusBarsPadding(),
                                        shouldShowBackButton = shouldShowBackButton,
                                        scrollBehavior = scrollBehavior,
                                        actions = {
                                            when (it) {
                                                TopAppBarActions.BackButtonClicked -> {
                                                    navigateBack()
                                                }

                                                TopAppBarActions.MoreButtonClicked -> {}
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box() {

                            NavDisplay(
                                modifier = Modifier,
                                backStack = backStack,
                                onBack = {
                                    navigateBack()
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
                                        FolderListScreen(
                                            modifier = modifier.padding(innerPadding),
                                            videoFolderClick = {
                                                backStack.add(Destinations.VideoListScreen)
                                            }
                                        )
                                    }

                                    entry<Destinations.VideoListScreen> {
                                        VideoListScreen(
                                            modifier = modifier.padding(innerPadding),
                                            videoItemClick = { videoItem ->
                                                viewModel.setVideoItem(videoItem)
                                                backStack.add(Destinations.VideoPlayerScreen)
                                            }
                                        )
                                    }

                                    entry<Destinations.VideoPlayerScreen> {
                                        VideoPlayerScreen(
                                            modifier = modifier
                                        )
                                    }
                                }
                            )

                            HorizontalFloatingToolbar(
                                expanded = false,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = -ScreenOffset),
                                colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                                    toolbarContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                ToolbarButtons(
                                    iconVector = R.drawable.video_icon,
                                    iconText = "Video",
                                    isSelected = selectedAppBarScreen == BottomAppBarState.VideoScreen,
                                    buttonType = BottomAppBarState.VideoScreen,
                                    appBarButtonClicked = {
                                        selectedAppBarScreen = it
                                    }
                                )
                                ToolbarButtons(
                                    iconVector = R.drawable.more_icon,
                                    iconText = "More",
                                    isSelected = selectedAppBarScreen == BottomAppBarState.MoreScreen,
                                    buttonType = BottomAppBarState.MoreScreen,
                                    appBarButtonClicked = {
                                        selectedAppBarScreen = it
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolbarButtons(
    @DrawableRes iconVector: Int,
    iconText: String,
    buttonType: BottomAppBarState,
    appBarButtonClicked: (BottomAppBarState) -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val containerColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 500)
    )

    val contentColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(durationMillis = 500)
    )

    FilledTonalButton(
        onClick = {
            appBarButtonClicked(buttonType)
        },
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(iconVector),
                contentDescription = iconText,
                tint = contentColor
            )
            Text(
                text = iconText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
