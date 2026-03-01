package com.roaa.exoplayer.navigation

import androidx.navigation3.runtime.NavKey
import com.roaa.exoplayer.VideoItem
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destinations : NavKey {

    @Serializable
    data object FolderListScreen : Destinations

    @Serializable
    data object VideoListScreen : Destinations

    @Serializable
    data class VideoPlayerScreen(val videoItem: VideoItem) : Destinations

}