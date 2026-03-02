package com.roaa.exoplayer.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destinations : NavKey {

    @Serializable
    data object FolderListScreen : Destinations

    @Serializable
    data object VideoListScreen : Destinations

    @Serializable
    data object VideoPlayerScreen : Destinations

}