package com.roaa.exoplayer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun VideoListScreen(
    modifier: Modifier = Modifier,
    videoFolder: VideoFolder,
    videoItemClick: (VideoItem) -> Unit
) {
    Scaffold() {
        Column(
            modifier = Modifier.padding(it)
        ) {
            LazyColumn() {
                items(videoFolder.videos) { videoItem ->
                    VideoItemCard(
                        videoItem = videoItem,
                        videoItemClick = {
                            videoItemClick(it)
                        })
                }
            }
        }
    }
}

@Composable
fun VideoItemCard(
    videoItem: VideoItem,
    videoItemClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .clickable { videoItemClick(videoItem) }
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = context)
                    .data(videoItem.uri)
                    .crossfade(true)
                    .build(),
                contentDescription = videoItem.name,
                modifier = Modifier
                    .aspectRatio(16f / 9f)
                    .weight(0.3f),
                contentScale = ContentScale.FillWidth
            )

            Column() {
                Text(
                    text = videoItem.name
                )
                Text(
                    text = videoItem.duration.toString()
                )
            }
        }
    }
}