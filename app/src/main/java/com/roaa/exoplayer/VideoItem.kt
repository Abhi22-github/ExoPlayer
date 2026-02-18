package com.roaa.exoplayer

import android.net.Uri

data class VideoItem(
    val id: Long,
    val name: String,
    val uri: Uri,
    val size: Long,
    val duration: Long,
    val dateAdded: Long,
    val mimeType: String,
    val path: String,
    val thumbnailUri: Uri? = null,
    val bucketId: Long,
    val bucketName: String
)

data class VideoFolder(
    val bucketId: Long,
    val bucketName: String,
    val videos: List<VideoItem>
)
