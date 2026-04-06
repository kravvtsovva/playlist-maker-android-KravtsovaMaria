package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.Resource
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> emit(Resource.Error("NETWORK_ERROR"))
            200 -> {
                with(response as TracksSearchResponse) {
                    val tracks = results.map {
                        Track(
                            trackId = it.trackId,
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTimeMillis = it.trackTimeMillis,
                            artworkUrl100 = it.artworkUrl100,
                            previewUrl = it.previewUrl
                        )
                    }
                    emit(Resource.Success(tracks))
                }
            }
            else -> emit(Resource.Error("SERVER_ERROR"))
        }
    }
}