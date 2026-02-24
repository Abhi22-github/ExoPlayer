package com.roaa.exoplayer

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FolderListScreen(
    videoFolderClick: (VideoFolder) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val permissionState = rememberMultiplePermissionsState(permission)

    var videos by remember { mutableStateOf<List<VideoFolder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            isLoading = true
            val repository = VideoRepository(context)
            videos = repository.getAllVideo()
            isLoading = false
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
                        videoFolderClick(videoFolder)
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
    LazyVerticalGrid(
        state = rememberLazyGridState(),
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
                .clip(RoundedCornerShape(12.dp))) {
            Box(
                modifier = Modifier
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                FolderThumbnailCollage(videoList = videoFolder.videos)
            }
        }
        Text(
            text = videoFolder.bucketName, maxLines = 1, overflow = TextOverflow.Ellipsis
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
                ThumbnailImage(videoItem = previewList[0], modifier = Modifier.fillMaxSize())
            }

            2 -> {
                Row() {
                    previewList.forEach {
                        ThumbnailImage(
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
                            ThumbnailImage(
                                videoItem = it, modifier = Modifier.weight(1f)
                            )
                        }
                        previewList.getOrNull(1)?.let {
                            ThumbnailImage(
                                videoItem = it, modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        previewList.getOrNull(2)?.let {
                            ThumbnailImage(
                                videoItem = it, modifier = Modifier.weight(1f)
                            )
                        }
                        previewList.getOrNull(3)?.let {
                            ThumbnailImage(
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
fun ThumbnailImage(modifier: Modifier = Modifier, videoItem: VideoItem) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(videoItem.uri)
            .crossfade(true)
            .build(),
        contentDescription = videoItem.name,
        modifier = modifier.aspectRatio(16f / 9f),
        contentScale = ContentScale.FillWidth
    )
}