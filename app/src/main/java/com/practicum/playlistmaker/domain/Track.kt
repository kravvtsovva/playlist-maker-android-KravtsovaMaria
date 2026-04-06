package com.practicum.playlistmaker.domain

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val previewUrl: String?,
    var favorite: Boolean = false,
    var playlistId: Long = 0
)