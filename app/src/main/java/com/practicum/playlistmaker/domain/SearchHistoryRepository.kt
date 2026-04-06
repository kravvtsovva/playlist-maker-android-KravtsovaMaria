package com.practicum.playlistmaker.domain

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun addSearchQuery(query: String)
    fun getSearchHistory(): Flow<List<String>>
}