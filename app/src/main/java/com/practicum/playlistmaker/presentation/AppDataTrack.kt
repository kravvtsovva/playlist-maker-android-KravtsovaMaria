package com.practicum.playlistmaker.presentation

import com.practicum.playlistmaker.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

data class AppTrack(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String? = null
)

fun Track.toAppTrack(): AppTrack {
    val trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    return AppTrack(trackId, trackName, artistName, trackTime, artworkUrl100)
}