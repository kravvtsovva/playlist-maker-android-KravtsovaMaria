package com.practicum.playlistmaker.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "${UUID.randomUUID()}.jpg"
        val coversDir = File(context.filesDir, "covers").apply { mkdirs() }
        val file = File(coversDir, fileName)
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}