package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.preferences.SearchHistoryManager
import com.practicum.playlistmaker.data.preferences.historyDataStore
import com.practicum.playlistmaker.data.repository.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksLocalRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.domain.SearchHistoryRepository
import com.practicum.playlistmaker.domain.TracksRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private const val URL_BASE = "https://itunes.apple.com"
    private val serviceApi: ITunesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    fun getTracksRepository(): TracksRepository {
        val networkClient = RetrofitNetworkClient(serviceApi)
        return TracksRepositoryImpl(networkClient)
    }

    fun getDatabase(context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    fun getPlaylistsRepository(context: Context): PlaylistsRepositoryImpl {
        val database = getDatabase(context)
        return PlaylistsRepositoryImpl(database.playlistDao(), database.trackDao())
    }

    fun getLocalTracksRepository(context: Context): TracksLocalRepositoryImpl {
        val database = getDatabase(context)
        return TracksLocalRepositoryImpl(database.trackDao(), database.playlistDao())
    }

    fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val preferences = SearchHistoryManager(context.historyDataStore)
        return SearchHistoryRepositoryImpl(preferences)
    }
}