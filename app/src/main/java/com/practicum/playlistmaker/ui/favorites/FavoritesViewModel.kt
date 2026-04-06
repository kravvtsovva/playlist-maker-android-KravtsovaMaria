package com.practicum.playlistmaker.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.TrackStorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val trackRepository: TrackStorageRepository
) : ViewModel() {

    private val _tracksFavoriteState = MutableStateFlow<List<Track>>(emptyList())
    val tracksFavoriteState = _tracksFavoriteState.asStateFlow()

    fun fetchFavorites() {
        viewModelScope.launch {
            trackRepository.fetchFavoriteTracks().collectLatest { favorites ->
                _tracksFavoriteState.value = favorites
            }
        }
    }

    fun updateFavoriteStatus(track: Track, favoriteStatus: Boolean) {
        viewModelScope.launch {
            trackRepository.setTrackFavoriteStatus(track, favoriteStatus)
        }
    }
}