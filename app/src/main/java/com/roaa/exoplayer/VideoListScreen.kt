package com.roaa.exoplayer

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun VideoListScreen(
    modifier: Modifier = Modifier,
    videoFolder: VideoFolder,
    videoItemClick: (VideoItem) -> Unit
) {
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
                .clip(RoundedCornerShape(12.dp))) {
            Box(
                modifier = Modifier
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                ThumbnailImage(videoItem = videoItem)
            }
        }
        Text(
            text = videoItem.name, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Text(
            text = videoItem.duration.toString(), maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}


