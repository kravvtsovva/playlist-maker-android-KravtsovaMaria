package com.practicum.playlistmaker.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.PlaylistCollectionRepository
import com.practicum.playlistmaker.domain.TrackStorageRepository

class PlaylistViewModelFactory(
    private val playlistsRepository: PlaylistCollectionRepository,
    private val tracksLocalRepository: TrackStorageRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistManagementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaylistManagementViewModel(playlistsRepository, tracksLocalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}