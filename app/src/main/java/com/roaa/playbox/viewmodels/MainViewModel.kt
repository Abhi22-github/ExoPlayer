package com.roaa.playbox.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roaa.playbox.models.VideoFolder
import com.roaa.playbox.models.VideoItem
import com.roaa.playbox.utils.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private lateinit var videoRepository: VideoRepository

    private val _videos: MutableStateFlow<List<VideoFolder>> = MutableStateFlow(emptyList())
    val videos: StateFlow<List<VideoFolder>> = _videos

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentVideoFolder: MutableStateFlow<VideoFolder> =
        MutableStateFlow(VideoFolder(0, "", emptyList()))
    val currentVideoFolder: StateFlow<VideoFolder> = _currentVideoFolder

    private val _currentVideoItem: MutableStateFlow<VideoItem> =
        MutableStateFlow(VideoItem())
    val currentVideoItem: StateFlow<VideoItem> = _currentVideoItem

    private val _currentExternalVideoItem: MutableStateFlow<VideoItem> =
        MutableStateFlow(VideoItem())
    val currentExternalVideoItem: StateFlow<VideoItem> = _currentExternalVideoItem


    fun initializeVideoRepository(videoRepository: VideoRepository) {
        this.videoRepository = videoRepository
    }

    fun loadVideos() {
        if (_videos.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.emit(true)
            _videos.emit(videoRepository.getAllVideo())
            _isLoading.emit(false)
        }
    }

    fun getVideoItemFromUri(uri: Uri) = videoRepository.getVideoItemFromUri(uri)

    fun setVideoFolder(videoFolder: VideoFolder) {
        viewModelScope.launch {
            _currentVideoFolder.emit(videoFolder)
        }
    }

    fun setVideoItem(videoItem: VideoItem) {
        viewModelScope.launch {
            _currentVideoItem.emit(videoItem)
        }
    }

    fun setExternalVideoItem(videoItem: VideoItem) {
        viewModelScope.launch {
            _currentExternalVideoItem.emit(videoItem)
        }
    }
}