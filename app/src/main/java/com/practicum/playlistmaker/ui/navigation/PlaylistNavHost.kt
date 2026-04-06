package com.practicum.playlistmaker.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.practicum.playlistmaker.domain.PlaylistCollectionRepository
import com.practicum.playlistmaker.domain.TrackStorageRepository
import com.practicum.playlistmaker.domain.TracksRepository
import com.practicum.playlistmaker.domain.SearchHistoryRepository
import com.practicum.playlistmaker.presentation.AppTrack
import com.practicum.playlistmaker.ui.favorites.FavoritesScreen
import com.practicum.playlistmaker.ui.favorites.FavoritesViewModel
import com.practicum.playlistmaker.ui.favorites.FavoritesViewModelFactory
import com.practicum.playlistmaker.ui.playlists.CreatePlaylistScreen
import com.practicum.playlistmaker.ui.playlists.PlaylistDetailsScreen
import com.practicum.playlistmaker.ui.playlists.PlaylistManagementViewModel
import com.practicum.playlistmaker.ui.playlists.PlaylistViewModelFactory
import com.practicum.playlistmaker.ui.playlists.PlaylistsScreen
import com.practicum.playlistmaker.ui.search.SearchScreen
import com.practicum.playlistmaker.ui.search.SearchViewModel
import com.practicum.playlistmaker.ui.search.SearchViewModelFactory
import com.practicum.playlistmaker.ui.settings.SettingsScreen
import com.practicum.playlistmaker.ui.track.TrackDetailsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.playlistmaker.ui.main.MainScreenView

@Composable
fun PlaylistNavHost(
    searchRepository: TracksRepository,
    playlistsRepository: PlaylistCollectionRepository,
    tracksLocalRepository: TrackStorageRepository,
    searchHistoryRepository: SearchHistoryRepository,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MAIN.name) {
        composable(Screen.MAIN.name) {
            MainScreenView(
                onSearchTap = { navController.navigate(Screen.SEARCH.name) },
                onPlaylistTap = { navController.navigate(Screen.PLAYLISTS.name) },
                onFavoritesTap = { navController.navigate(Screen.FAVORITES.name) },
                onSettingsTap = { navController.navigate(Screen.SETTINGS.name) },
                darkThemeEnabled = isDarkTheme
            )
        }
        composable(Screen.SEARCH.name) {
            val factory = SearchViewModelFactory(searchRepository, searchHistoryRepository)
            val vm: SearchViewModel = viewModel(factory = factory)
            SearchScreen(
                viewModel = vm,
                isDarkTheme = isDarkTheme,
                onBackClick = { navController.popBackStack() },
                onTrackClick = { track: AppTrack ->
                    navController.navigate(
                        "${Screen.TRACK_DETAILS.name}/" +
                                "${track.trackId}/" +
                                "${Uri.encode(track.trackName)}/" +
                                "${Uri.encode(track.artistName)}/" +
                                "${Uri.encode(track.trackTime)}/" +
                                "${Uri.encode(track.artworkUrl100)}"
                    )
                }
            )
        }
        composable(Screen.SETTINGS.name) {
            SettingsScreen(
                darkThemeEnabled = isDarkTheme,
                onThemeToggle = onToggleTheme,
                onBackPressed = { navController.popBackStack() }
            )
        }
        composable(Screen.PLAYLISTS.name) {
            val factory = PlaylistViewModelFactory(playlistsRepository, tracksLocalRepository)
            val vm: PlaylistManagementViewModel = viewModel(factory = factory)
            PlaylistsScreen(
                viewModel = vm,
                onCreatePlaylist = { navController.navigate(Screen.CREATE_PLAYLIST.name) },
                onOpenPlaylist = { playlistId: Long -> navController.navigate("${Screen.PLAYLIST_DETAILS.name}/$playlistId") },
                onBackClick = { navController.popBackStack() },
                isDarkTheme = isDarkTheme
            )
        }
        composable(Screen.CREATE_PLAYLIST.name) {
            val factory = PlaylistViewModelFactory(playlistsRepository, tracksLocalRepository)
            val vm: PlaylistManagementViewModel = viewModel(factory = factory)
            CreatePlaylistScreen(
                viewModel = vm,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                isDarkTheme = isDarkTheme
            )
        }
        composable(
            route = "${Screen.TRACK_DETAILS.name}/{trackId}/{trackName}/{artistName}/{trackTime}/{artworkUrl100}",
            arguments = listOf(
                navArgument("trackId") { type = NavType.LongType },
                navArgument("trackName") { type = NavType.StringType },
                navArgument("artistName") { type = NavType.StringType },
                navArgument("trackTime") { type = NavType.StringType },
                navArgument("artworkUrl100") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val tId = backStackEntry.arguments?.getLong("trackId") ?: 0L
            val tName = backStackEntry.arguments?.getString("trackName") ?: ""
            val aName = backStackEntry.arguments?.getString("artistName") ?: ""
            val time = backStackEntry.arguments?.getString("trackTime") ?: "0:00"
            val artwork = backStackEntry.arguments?.getString("artworkUrl100")
            val appTrack = AppTrack(tId, tName, aName, time, artwork)
            val factory = PlaylistViewModelFactory(playlistsRepository, tracksLocalRepository)
            val vm: PlaylistManagementViewModel = viewModel(factory = factory)
            TrackDetailsScreen(
                appTrack = appTrack,
                playlistViewModel = vm,
                onBackClick = { navController.popBackStack() },
                isDarkTheme = isDarkTheme
            )
        }
        composable(Screen.FAVORITES.name) {
            val factory = FavoritesViewModelFactory(tracksLocalRepository)
            val vm: FavoritesViewModel = viewModel(factory = factory)
            FavoritesScreen(
                viewModel = vm,
                onBackClicked = { navController.popBackStack() },
                onTrackSelected = { track ->
                    navController.navigate(
                        "${Screen.TRACK_DETAILS.name}/" +
                                "${track.trackId}/" +
                                "${Uri.encode(track.trackName)}/" +
                                "${Uri.encode(track.artistName)}/" +
                                "${Uri.encode(track.trackTime)}/" +
                                "${Uri.encode(track.artworkUrl100)}"
                    )
                },
                darkMode = isDarkTheme
            )
        }
        composable(
            route = "${Screen.PLAYLIST_DETAILS.name}/{playlistId}",
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
            val factory = PlaylistViewModelFactory(playlistsRepository, tracksLocalRepository)
            val vm: PlaylistManagementViewModel = viewModel(factory = factory)
            PlaylistDetailsScreen(
                playlistId = playlistId,
                viewModel = vm,
                onBackClick = { navController.popBackStack() },
                onTrackClick = { track ->
                    navController.navigate(
                        "${Screen.TRACK_DETAILS.name}/" +
                                "${track.trackId}/" +
                                "${Uri.encode(track.trackName)}/" +
                                "${Uri.encode(track.artistName)}/" +
                                "${Uri.encode(track.trackTime)}/" +
                                "${Uri.encode(track.artworkUrl100)}"
                    )
                },
                isDarkTheme = isDarkTheme
            )
        }
    }
}