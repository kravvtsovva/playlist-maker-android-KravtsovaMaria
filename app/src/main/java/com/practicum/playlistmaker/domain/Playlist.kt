package com.practicum.playlistmaker.domain

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val tracks: List<Track> = emptyList(),
    val coverImageUri: String? = null
)