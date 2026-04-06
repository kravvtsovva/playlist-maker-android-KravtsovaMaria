package com.practicum.playlistmaker.ui.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.materialTheme.YS
import com.practicum.playlistmaker.ui.components.PlaylistItem

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistManagementViewModel,
    onCreatePlaylist: () -> Unit,
    onOpenPlaylist: (Long) -> Unit,
    onBackClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val playlists by viewModel.allPlaylists.collectAsState(emptyList())
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var playlistIdToDelete by remember { mutableStateOf<Long?>(null) }
    var playlistNameToDelete by remember { mutableStateOf("") }

    val backgroundColorScheme = if (isDarkTheme) Color(0xFF1A1B22) else Color.White
    val textColorScheme = if (isDarkTheme) Color.White else Color(0xFF1A1B22)
    val borderColor = Color(0xFFAEAFB4)
    val fabBackground = if (isDarkTheme) Color.White.copy(alpha = 0.25f) else Color(0xFF1A1B22).copy(alpha = 0.25f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColorScheme)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 16.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left_icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = textColorScheme
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.playlists),
                    fontFamily = YS,
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColorScheme
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 0.dp)
            ) {
                items(playlists, key = { it.id }) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        textColor = textColorScheme,
                        secondaryColor = borderColor,
                        chevronColor = borderColor,
                        backgroundColor = backgroundColorScheme,
                        onItemClick = { onOpenPlaylist(playlist.id) },
                        onItemLongPress = {
                            playlistIdToDelete = playlist.id
                            playlistNameToDelete = playlist.name
                            showDeleteConfirmation = true
                        },
                        displayChevron = true
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onCreatePlaylist,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 32.dp)
                .size(51.dp),
            containerColor = fabBackground,
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_plus_icon),
                contentDescription = stringResource(R.string.create_playlist),
                tint = Color.White,
                modifier = Modifier.padding(13.dp)
            )
        }

        if (showDeleteConfirmation && playlistIdToDelete != null) {
            AlertDialog(
                shape = RoundedCornerShape(4.dp),
                onDismissRequest = { showDeleteConfirmation = false },
                containerColor = Color.White,
                tonalElevation = 16.dp,
                title = null,
                text = {
                    Text(
                        text = stringResource(
                            R.string.delete_playlist_message,
                            playlistNameToDelete
                        ),
                        color = Color(0xFF1A1B22),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.25.sp,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.removePlaylist(playlistIdToDelete!!)
                        showDeleteConfirmation = false
                    }) {
                        Text(
                            text = stringResource(R.string.confirm).uppercase(),
                            color = Color(0xFF3772E7),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            letterSpacing = 1.25.sp
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text(
                            text = stringResource(R.string.cancel).uppercase(),
                            color = Color(0xFF3772E7),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            letterSpacing = 1.25.sp
                        )
                    }
                }
            )
        }
    }
}