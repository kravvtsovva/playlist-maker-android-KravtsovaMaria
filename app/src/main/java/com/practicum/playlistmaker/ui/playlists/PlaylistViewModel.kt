package com.practicum.playlistmaker.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.Playlist
import com.practicum.playlistmaker.domain.PlaylistCollectionRepository
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.TrackStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistManagementViewModel(
    private val playlistRepo: PlaylistCollectionRepository,
    private val trackLocalRepo: TrackStorageRepository
) : ViewModel() {
    val allPlaylists: Flow<List<Playlist>> = playlistRepo.fetchAllPlaylists().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _playlistCoverUri = MutableStateFlow<String?>(null)
    val playlistCoverUri: Flow<String?> = _playlistCoverUri.asStateFlow()

    fun updateCoverUri(uri: String?) {
        _playlistCoverUri.value = uri
    }

    fun fetchPlaylist(id: Long): Flow<Playlist?> = playlistRepo.fetchPlaylistById(id)

    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            playlistRepo.createPlaylist(name, description, _playlistCoverUri.value)
        }
    }

    fun removePlaylist(id: Long) {
        viewModelScope.launch {
            playlistRepo.removePlaylist(id)
        }
    }

    fun addTrackToPlaylist(track: Track, playlistId: Long) {
        viewModelScope.launch {
            trackLocalRepo.addTrackToPlaylist(track, playlistId)
        }
    }

    fun setTrackFavoriteStatus(track: Track, favoriteStatus: Boolean) {
        viewModelScope.launch {
            trackLocalRepo.setTrackFavoriteStatus(track, favoriteStatus)
        }
    }

    fun fetchTrackById(trackId: Long): Flow<Track?> = trackLocalRepo.fetchTrackById(trackId)

    fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        viewModelScope.launch {
            trackLocalRepo.removeTrackFromPlaylist(trackId, playlistId)
        }
    }
}