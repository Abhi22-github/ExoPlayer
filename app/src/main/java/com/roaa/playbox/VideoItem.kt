package com.roaa.playbox

import android.net.Uri

data class VideoItem(
    val id: Long = 0L,
    val name: String = "",
    val uri: Uri = Uri.EMPTY,
    val size: Long = 0L,
    val duration: Long = 0L,
    val dateAdded: Long = 0L,
    val mimeType: String = "",
    val path: String = "",
    val thumbnailUri: Uri? = null,
    val bucketId: Long = 0L,
    val bucketName: String = ""
)

data class VideoFolder(
    val bucketId: Long,
    val bucketName: String,
    val videos: List<VideoItem>
)
