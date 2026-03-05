package com.roaa.playbox.composition

import androidx.compose.runtime.staticCompositionLocalOf
import com.roaa.playbox.viewmodels.MainViewModel

val localViewModel = staticCompositionLocalOf<MainViewModel> {
    error("MainViewModel is not available")
}