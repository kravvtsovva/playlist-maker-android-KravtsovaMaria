package com.practicum.playlistmaker.domain

import kotlinx.coroutines.flow.Flow

interface TrackStorageRepository {
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long)
    suspend fun setTrackFavoriteStatus(track: Track, isFavorite: Boolean)
    fun fetchFavoriteTracks(): Flow<List<Track>>
    fun fetchTrackById(trackId: Long): Flow<Track?>
}