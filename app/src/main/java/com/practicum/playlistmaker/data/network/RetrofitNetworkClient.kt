package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.BaseResponse
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.HttpException
import java.io.IOException

class RetrofitNetworkClient(
    private val api: ITunesApiService
) : NetworkClient {
    override suspend fun doRequest(dto: Any): BaseResponse {
        if (dto !is TracksSearchRequest) {
            return BaseResponse().apply { resultCode = 400 }
        }
        return try {
            api.searchTracks(dto.expression).apply { resultCode = 200 }
        } catch (e: Throwable) {
            BaseResponse().apply {
                resultCode = when (e) {
                    is IOException -> -1
                    is HttpException -> e.code()
                    else -> 500
                }
            }
        }
    }
}