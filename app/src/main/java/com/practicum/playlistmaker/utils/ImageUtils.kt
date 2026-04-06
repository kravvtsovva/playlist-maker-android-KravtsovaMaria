package com.practicum.playlistmaker.utils

object ImageUtils {
    fun getArtworkUrl(url: String?, size: String = "100x100"): String? {
        if (url == null) return null
        if (url.startsWith("http") && url.contains("100x100")) {
            return url.replace("100x100", size)
        }
        return url
    }
}