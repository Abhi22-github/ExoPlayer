package com.roaa.exoplayer

import android.net.Uri

data class VideoItem(
    val id: Long,
    val name: String,
    val uri: Uri,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val mimeType: String,
    val path: String
)
