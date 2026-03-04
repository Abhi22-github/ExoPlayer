package com.roaa.playbox.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.roaa.playbox.viewmodels.MainViewModel
import com.roaa.playbox.models.VideoFolder
import com.roaa.playbox.models.VideoItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FolderListScreen(
    videoFolderClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val scope = rememberCoroutineScope()

    val permissionState = rememberMultiplePermissionsState(permission)

    val videos by viewModel.videos.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            viewModel.loadVideos()
        }
    }

    when {
        permissionState.allPermissionsGranted -> {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            } else {
                VideoList(
                    modifier = modifier,
                    videoFolderList = videos,
                    videoFolderClick = { videoFolder ->
                        scope.launch {
                            viewModel.setVideoFolder(videoFolder)
                        }
                        videoFolderClick()
                    })
            }
        }

        else -> {
            PermissionRequestScreen(
                onRequestPermission = { permissionState.launchMultiplePermissionRequest() })
        }
    }
}

@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("This app needs storage permission to access videos")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}

@Composable
fun VideoList(
    videoFolderList: List<VideoFolder>,
    videoFolderClick: (VideoFolder) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyGridState()
    LazyVerticalGrid(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 0.dp, horizontal = 16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(videoFolderList) {
            VideoFolderItem(
                videoFolder = it,
                videoFolderClick = { videoFolder ->
                    videoFolderClick(videoFolder)
                })
        }
    }
}

@Composable
fun VideoFolderItem(
    videoFolder: VideoFolder,
    videoFolderClick: (VideoFolder) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column() {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    videoFolderClick(videoFolder)
                }
                .clip(RoundedCornerShape(6.dp)),
            shape = RoundedCornerShape(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                FolderThumbnailCollage(videoList = videoFolder.videos)
            }
        }
        Text(
            text = videoFolder.bucketName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun FolderThumbnailCollage(
    modifier: Modifier = Modifier,
    videoList: List<VideoItem> = emptyList(),
) {
    val previewList = videoList.take(4)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        when (previewList.size) {
            1 -> {
                ThumbnailImageForFolder(
                    videoItem = previewList[0],
                    modifier = Modifier.fillMaxSize()
                )
            }

            2 -> {
                Row() {
                    previewList.forEach {
                        ThumbnailImageForFolder(
                            videoItem = it, modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        previewList.getOrNull(0)?.let {
                            ThumbnailImageForFolder(
                                videoItem = it, modifier = Modifier.weight(1f)
                            )
                        }
                        previewList.getOrNull(1)?.let {
                            ThumbnailImageForFolder(
                                videoItem = it, modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        previewList.getOrNull(2)?.let {
                            ThumbnailImageForFolder(
                                videoItem = it, modifier = Modifier.weight(1f)
                            )
                        }
                        previewList.getOrNull(3)?.let {
                            ThumbnailImageForFolder(
                                videoItem = it, modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

        }
    }

}

@Composable
fun ThumbnailImageForFolder(modifier: Modifier = Modifier, videoItem: VideoItem) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(videoItem.uri)
            .crossfade(true)
            .build(),
        contentDescription = videoItem.name,
        modifier = modifier.aspectRatio(16f / 9f),
        contentScale = ContentScale.Crop
    )
}