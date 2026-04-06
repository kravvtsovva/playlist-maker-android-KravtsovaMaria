package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.BaseResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): BaseResponse
}