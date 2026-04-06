package com.practicum.playlistmaker.ui.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Playlist
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.presentation.AppTrack
import com.practicum.playlistmaker.ui.components.TrackItem
import com.practicum.playlistmaker.ui.materialTheme.YS
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import coil.request.ImageRequest
import coil.size.Size
import com.practicum.playlistmaker.presentation.toAppTrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailsScreen(
    playlistId: Long,
    viewModel: PlaylistManagementViewModel,
    onBackClick: () -> Unit,
    onTrackClick: (AppTrack) -> Unit,
    isDarkTheme: Boolean
) {
    var playlist by remember { mutableStateOf<Playlist?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeletePlaylistDialog by remember { mutableStateOf(false) }
    var showDeleteTrackDialog by remember { mutableStateOf(false) }
    var trackToDelete by remember { mutableStateOf<Track?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(playlistId) {
        playlist = viewModel.fetchPlaylist(playlistId).firstOrNull()
    }

    val grayShade = Color(0xFFAEAFB4)
    val backgroundMain = Color(0xFFE6E8EB)
    val mainTextColor = Color(0xFF1A1B22)
    val sheetBackgroundColor = if (isDarkTheme) Color(0xFF1A1B22) else Color.White
    val sheetTextColor = if (isDarkTheme) Color.White else Color(0xFF1A1B22)
    val sheetGrayColor = if (isDarkTheme) Color.White.copy(alpha = 0.84f) else Color(0xFFAEAFB4)
    val sheetLightGrayColor = if (isDarkTheme) Color.White else Color(0xFFE6E8EB)

    if (playlist == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundMain),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val playlistData = playlist!!

    BottomSheetScaffold(
        sheetPeekHeight = if (playlistData.tracks.isNotEmpty()) 61.dp else 0.dp,
        sheetContainerColor = sheetBackgroundColor,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(4.dp)
                        .background(sheetLightGrayColor, RoundedCornerShape(44.dp))
                )
            }
        },
        sheetContent = {
            LazyColumn {
                items(playlistData.tracks) { track ->
                    TrackItem(
                        track = track.toAppTrack(),
                        darkTheme = isDarkTheme,
                        onItemClick = { onTrackClick(track.toAppTrack()) },
                        onItemLongPress = {
                            trackToDelete = track
                            showDeleteTrackDialog = true
                        }
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundMain)
                .padding(innerPadding)
        ) {
            item {

                val coverSize = 360.dp
                val defaultSize = 100.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(coverSize),
                    contentAlignment = Alignment.Center
                ) {
                    val context = LocalContext.current

                    playlistData.coverImageUri?.let { uriString ->
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uriString.toUri())
                                .size(Size.ORIGINAL)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.size(coverSize),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Image(
                        painter = painterResource(id = R.drawable.ic_add_playlist_photo),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(grayShade),
                        modifier = Modifier
                            .size(defaultSize)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Spacer(Modifier.height(24.dp))

                Text(
                    text = playlistData.name,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontFamily = YS,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = mainTextColor
                )
                Spacer(Modifier.height(12.dp))

                if (playlistData.description.isNotBlank()) {
                    Text(
                        text = playlistData.description,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontFamily = YS,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = mainTextColor
                    )
                    Spacer(Modifier.height(8.dp))
                }

                val totalMinutes = playlistData.tracks.sumOf { it.trackTimeMillis } / 60000
                val minutesCount = totalMinutes.toInt()
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        pluralStringResource(R.plurals.minutes, minutesCount, minutesCount),
                        fontFamily = YS,
                        fontSize = 18.sp,
                        color = mainTextColor
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(grayShade, CircleShape)
                            .offset(y = 5.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        pluralStringResource(R.plurals.tracks, playlistData.tracks.size, playlistData.tracks.size),
                        fontFamily = YS,
                        fontSize = 18.sp,
                        color = mainTextColor
                    )
                }
                Spacer(Modifier.height(16.dp))

                IconButton(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more_button),
                        contentDescription = null,
                        tint = mainTextColor
                    )
                }
                Spacer(Modifier.height(16.dp))
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = sheetBackgroundColor,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(50.dp)
                            .height(4.dp)
                            .background(sheetLightGrayColor, RoundedCornerShape(44.dp))
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(61.dp)
                        .padding(start = 13.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    playlistData.coverImageUri?.let { uriString ->
                        AsyncImage(
                            model = uriString.toUri(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(45.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Image(
                        painter = painterResource(id = R.drawable.ic_add_playlist_photo),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(sheetGrayColor),
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = playlistData.name,
                            fontFamily = YS,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            lineHeight = 19.sp,
                            color = sheetTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            pluralStringResource(R.plurals.tracks, playlistData.tracks.size, playlistData.tracks.size),
                            fontFamily = YS,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            color = sheetGrayColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.share),
                    fontFamily = YS,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO share */ }
                        .padding(start = 16.dp, top = 21.dp, bottom = 21.dp),
                    color = sheetTextColor
                )
                Text(
                    text = stringResource(R.string.edit_playlist),
                    fontFamily = YS,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO */ }
                        .padding(start = 16.dp, top = 21.dp, bottom = 21.dp),
                    color = sheetTextColor
                )
                Text(
                    text = stringResource(R.string.delete_playlist),
                    fontFamily = YS,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showBottomSheet = false
                            showDeletePlaylistDialog = true
                        }
                        .padding(start = 16.dp, top = 21.dp, bottom = 21.dp),
                    color = sheetTextColor
                )
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    if (showDeletePlaylistDialog) {
        AlertDialog(
            shape = RoundedCornerShape(4.dp),
            onDismissRequest = { showDeletePlaylistDialog = false },
            containerColor = Color.White,
            tonalElevation = 16.dp,
            title = null,
            text = {
                Text(
                    text = stringResource(R.string.delete_playlist_message, playlistData.name),
                    color = Color(0xFF1A1B22),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.25.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removePlaylist(playlistId)
                    showDeletePlaylistDialog = false
                    onBackClick()
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
                TextButton(onClick = { showDeletePlaylistDialog = false }) {
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

    if (showDeleteTrackDialog && trackToDelete != null) {
        AlertDialog(
            shape = RoundedCornerShape(4.dp),
            onDismissRequest = { showDeleteTrackDialog = false },
            containerColor = Color.White,
            tonalElevation = 16.dp,
            title = null,
            text = {
                Text(
                    text = stringResource(R.string.delete_playlist_message, trackToDelete!!.trackName),
                    color = Color(0xFF1A1B22),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.25.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        viewModel.removeTrackFromPlaylist(trackToDelete!!.trackId, playlistId)
                    }
                    showDeleteTrackDialog = false
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
                TextButton(onClick = { showDeleteTrackDialog = false }) {
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