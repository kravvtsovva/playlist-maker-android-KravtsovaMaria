package com.practicum.playlistmaker.presentation

sealed class SearchState {
    data object Empty : SearchState()
    data object Loading : SearchState()
    data class Content(val tracks: List<AppTrack>) : SearchState()
    data object EmptyResult : SearchState()
    data class Error(val messageResId: Int) : SearchState()
}