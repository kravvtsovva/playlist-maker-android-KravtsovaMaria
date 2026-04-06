package com.practicum.playlistmaker.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.ui.materialTheme.PlaylistMakerTheme
import com.practicum.playlistmaker.ui.navigation.PlaylistNavHost

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark = remember { mutableStateOf(false) }

            val playlistsRepo = remember {
                Creator.getPlaylistsRepository(applicationContext)
            }

            val tracksLocalRepo = remember {
                Creator.getLocalTracksRepository(applicationContext)
            }

            val searchRepository = remember { Creator.getTracksRepository() }

            val searchHistoryRepo = remember { Creator.getSearchHistoryRepository(applicationContext) }

            PlaylistMakerTheme(darkTheme = isDark.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlaylistNavHost(
                        searchRepository = searchRepository,
                        playlistsRepository = playlistsRepo,
                        tracksLocalRepository = tracksLocalRepo,
                        searchHistoryRepository = searchHistoryRepo,
                        isDarkTheme = isDark.value,
                        onToggleTheme = { isDark.value = it }
                    )
                }
            }
        }
    }
}