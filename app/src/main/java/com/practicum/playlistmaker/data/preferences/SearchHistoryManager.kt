package com.practicum.playlistmaker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.historyDataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history_storage")

class SearchHistoryManager(
    private val preferencesDataStore: DataStore<Preferences>,
    private val scope: CoroutineScope = CoroutineScope(CoroutineName("history-manager") + SupervisorJob())
) {
    companion object {
        private const val MAX_ITEMS = 10
        private const val DELIMITER = ","
        private val HISTORY_PREF_KEY = stringPreferencesKey("search_history_list")
    }

    fun addItem(entry: String) {
        if (entry.isBlank()) return
        scope.launch {
            preferencesDataStore.edit { preferences ->
                val existingHistory = preferences[HISTORY_PREF_KEY].orEmpty()
                val historyList = if (existingHistory.isNotEmpty()) {
                    existingHistory.split(DELIMITER).toMutableList()
                } else {
                    mutableListOf()
                }
                historyList.remove(entry)
                historyList.add(0, entry)
                val limitedList = if (historyList.size > MAX_ITEMS) historyList.subList(0, MAX_ITEMS) else historyList
                preferences[HISTORY_PREF_KEY] = limitedList.joinToString(DELIMITER)
            }
        }
    }

    fun fetchEntries(): Flow<List<String>> = preferencesDataStore.data.map { preferences ->
        val historyStr = preferences[HISTORY_PREF_KEY].orEmpty()
        if (historyStr.isNotEmpty()) {
            historyStr.split(DELIMITER)
        } else {
            emptyList()
        }
    }
}