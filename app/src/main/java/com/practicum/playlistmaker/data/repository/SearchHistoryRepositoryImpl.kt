package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.preferences.SearchHistoryManager
import com.practicum.playlistmaker.domain.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepositoryImpl(
    private val preferences: SearchHistoryManager
) : SearchHistoryRepository {
    override fun addSearchQuery(query: String) {
        preferences.addItem(query)
    }

    override fun getSearchHistory(): Flow<List<String>> = preferences.fetchEntries()
}