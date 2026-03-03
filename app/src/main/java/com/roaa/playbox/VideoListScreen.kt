package com.roaa.playbox

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun VideoListScreen(
    modifier: Modifier = Modifier,
    videoItemClick: (VideoItem) -> Unit,
    viewModel: MainViewModel
) {
    val videoFolder by viewModel.currentVideoFolder.collectAsStateWithLifecycle()
    Scaffold(
      modifier = modifier
    ) {
        VideoList(
            videoList = videoFolder.videos,
            videoItemClick = {
                videoItemClick(it)
            }
        )
    }
}

@Composable
fun VideoList(
    videoList: List<VideoItem>,
    videoItemClick: (VideoItem) -> Unit,
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
        items(videoList) {
            VideoItemView(
                videoItem = it,
                videoItemClick = { videoItem ->
                    videoItemClick(videoItem)
                })
        }
    }
}

@Composable
fun VideoItemView(
    videoItem: VideoItem,
    videoItemClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column() {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    videoItemClick(videoItem)
                }
                .clip(RoundedCornerShape(6.dp)),
            shape = RoundedCornerShape(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                ThumbnailImageForVideo(videoItem = videoItem)
            }
        }
        Text(
            text = videoItem.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = videoItem.duration.toString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ThumbnailImageForVideo(modifier: Modifier = Modifier, videoItem: VideoItem) {
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


