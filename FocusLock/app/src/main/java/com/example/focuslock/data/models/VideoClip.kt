package com.example.focuslock.data.models

import android.net.Uri

data class VideoClip(
    val id: String,
    val uri: Uri,
    val startTime: Float,
    val endTime: Float,
    val name: String,
    val duration: Float = endTime - startTime,
    val isSelected: Boolean = false
)

data class Filter(
    val name: String,
    val displayName: String,
    val isSelected: Boolean = false
)

data class TextOverlay(
    val text: String,
    val fontSize: Int,
    val color: String,
    val x: Int,
    val y: Int,
    val startTime: Float,
    val endTime: Float
)

data class AudioTrack(
    val uri: Uri,
    val volume: Float,
    val startTime: Float,
    val endTime: Float,
    val isBackgroundMusic: Boolean = false
)

data class ExportSettings(
    val resolution: String = "1080p",
    val aspectRatio: String = "16:9",
    val quality: Int = 80,
    val format: String = "mp4"
)
