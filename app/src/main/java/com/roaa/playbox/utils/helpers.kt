package com.roaa.playbox.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun Long.toTimeFormat(): String {
    val duration = this / 1000
    val hours = duration / 3600
    val totalMinutes = (duration % 3600) / 60
    val totalSeconds = duration % 60

    return if (hours > 1) {
        String.format("%02d:%02d:%02d", hours, totalMinutes, totalSeconds)
    } else {
        String.format("%02d:%02d", totalMinutes, totalSeconds)
    }
}