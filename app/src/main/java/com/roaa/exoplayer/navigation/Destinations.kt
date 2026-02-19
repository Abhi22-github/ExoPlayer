package com.roaa.exoplayer.navigation

import androidx.navigation3.runtime.NavKey
import com.roaa.exoplayer.VideoFolder
import com.roaa.exoplayer.VideoItem
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destinations : NavKey {

    @Serializable
    data object FolderListScreen : Destinations

    @Serializable
    data class VideoListScreen(val videoFolder: VideoFolder) : Destinations

    @Serializable
    data class VideoPlayerScreen(val videoItem: VideoItem) : Destinations

}