package com.practicum.playlistmaker.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.TrackStorageRepository

class FavoritesViewModelFactory(
    private val tracksLocalRepository: TrackStorageRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(tracksLocalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}