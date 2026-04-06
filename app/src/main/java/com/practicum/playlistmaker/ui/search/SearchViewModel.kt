package com.practicum.playlistmaker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Resource
import com.practicum.playlistmaker.domain.SearchHistoryRepository
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.TracksRepository
import com.practicum.playlistmaker.presentation.SearchState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.practicum.playlistmaker.presentation.toAppTrack

class SearchViewModel(
    private val repository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _state = MutableStateFlow<SearchState>(SearchState.Empty)
    val state = _state.asStateFlow()

    fun onSearchTextChanged(changedText: String) {
        _searchText.value = changedText
        if (changedText.isEmpty()) {
            _state.value = SearchState.Empty
        }
    }

    fun searchTracks() {
        if (_searchText.value.isEmpty()) return
        _state.value = SearchState.Loading
        viewModelScope.launch {
            repository.searchTracks(_searchText.value)
                .collect { result ->
                    processResult(result)
                }
            searchHistoryRepository.addSearchQuery(_searchText.value)
        }
    }

    private fun processResult(foundTracks: Resource<List<Track>>) {
        val tracks = mutableListOf<Track>()
        if (foundTracks.data != null) {
            tracks.addAll(foundTracks.data.filter { it.trackTimeMillis > 0 })
        }
        when (foundTracks) {
            is Resource.Success -> {
                if (tracks.isEmpty()) {
                    _state.value = SearchState.EmptyResult
                } else {
                    _state.value = SearchState.Content(tracks.map { it.toAppTrack() })
                }
            }
            is Resource.Error -> {
                val resId = when (foundTracks.message) {
                    "NETWORK_ERROR" -> R.string.error_no_internet
                    "SERVER_ERROR" -> R.string.error_server
                    else -> R.string.unknown_error
                }
                _state.value = SearchState.Error(resId)
            }
        }
    }

    fun clearSearch() {
        _searchText.value = ""
        _state.value = SearchState.Empty
    }

    fun getHistory(): Flow<List<String>> = searchHistoryRepository.getSearchHistory()

}