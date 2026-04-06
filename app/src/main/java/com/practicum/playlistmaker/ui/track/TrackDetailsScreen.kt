package com.practicum.playlistmaker.ui.track


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.presentation.AppTrack
import com.practicum.playlistmaker.ui.materialTheme.YS
import com.practicum.playlistmaker.ui.playlists.PlaylistManagementViewModel
import com.practicum.playlistmaker.ui.components.PlaylistItem
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.flow.firstOrNull
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.practicum.playlistmaker.utils.ImageUtils
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    appTrack: AppTrack,
    playlistViewModel: PlaylistManagementViewModel,
    onBackClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    LaunchedEffect(appTrack) {
        val existing = playlistViewModel.fetchTrackById(appTrack.trackId).firstOrNull()
        isFavorite = existing?.favorite ?: false
    }
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1B22) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1A1B22)
    val secondaryTextColor = if (isDarkTheme) Color.White.copy(alpha = 0.84f) else Color(0xFF1A1B22).copy(alpha = 0.7f)
    val buttonBg = if (isDarkTheme) Color.White.copy(alpha = 0.25f) else Color(0xFF1A1B22).copy(alpha = 0.25f)
    val overlayColor = Color(0xFF1A1B22).copy(alpha = 0.5f)
    val sheetBg = backgroundColor
    val sheetText = textColor
    val sheetGray = if (isDarkTheme) Color.White else Color(0xFFAEAFB4)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(start = 16.dp)
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_left_icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = textColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(26.dp))

                val artworkUrl = appTrack.artworkUrl100
                val lowResUrl = ImageUtils.getArtworkUrl(artworkUrl, "100x100")
                val highResUrl = ImageUtils.getArtworkUrl(artworkUrl, "1000x1000") ?: artworkUrl

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(highResUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = rememberAsyncImagePainter(lowResUrl ?: R.drawable.ic_music_image),
                    error = painterResource(R.drawable.ic_music_image),
                    fallback = painterResource(R.drawable.ic_music_image),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(24.dp))
                var textHeightPx by remember { mutableStateOf(0) }
                val density = LocalDensity.current.density
                val fixedTop = 418f
                val minSpacer = 32f
                val targetFromTop = 620f
                val spacerHeight = max(minSpacer, targetFromTop - fixedTop - (textHeightPx / density)).dp

                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .onSizeChanged { textHeightPx = it.height }
                ) {
                    Text(
                        text = appTrack.trackName,
                        fontFamily = YS,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = appTrack.artistName,
                        fontFamily = YS,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        color = secondaryTextColor
                    )
                }
                Spacer(modifier = Modifier.height(spacerHeight))
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp)
                ) {
                    IconButton(onClick = { showSheet = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_for_playlist_icon),
                            contentDescription = stringResource(R.string.add_to_playlist),
                            tint = Color.White,
                            modifier = Modifier
                                .size(51.dp)
                                .background(buttonBg, RoundedCornerShape(69.dp))
                                .padding(11.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        isFavorite = !isFavorite
                        val domainTrack = Track(
                            trackId = appTrack.trackId,
                            trackName = appTrack.trackName,
                            artistName = appTrack.artistName,
                            trackTimeMillis = parseTimeToMillis(appTrack.trackTime),
                            artworkUrl100 = appTrack.artworkUrl100,
                            previewUrl = null
                        )
                        playlistViewModel.setTrackFavoriteStatus(domainTrack, isFavorite)
                    }) {
                        Icon(
                            painter = painterResource(id = if (isFavorite) R.drawable.ic_favorite_fill_icon else R.drawable.ic_favorite_line_icon),
                            contentDescription = stringResource(R.string.favorite),
                            tint = Color.White,
                            modifier = Modifier
                                .size(51.dp)
                                .background(buttonBg, RoundedCornerShape(69.dp))
                                .padding(8.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.duration),
                        fontFamily = YS,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        color = Color(0xFFAEAFB4)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = appTrack.trackTime,
                        fontFamily = YS,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        color = textColor,
                        textAlign = TextAlign.Right
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = sheetBg,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                dragHandle = null,
                scrimColor = overlayColor
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
                                .clip(RoundedCornerShape(44.dp))
                                .background(sheetGray)
                        )
                    }
                    Spacer(modifier = Modifier.height(23.dp))

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.add_to_playlist),
                        fontFamily = YS,
                        fontWeight = FontWeight.Medium,
                        fontSize = 19.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = sheetText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(23.dp))

                    val playlists by playlistViewModel.allPlaylists.collectAsState(emptyList())
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(playlists, key = { it.id }) { pl ->
                            PlaylistItem(
                                playlist = pl,
                                textColor = sheetText,
                                secondaryColor = sheetGray,
                                backgroundColor = sheetBg,
                                displayChevron = false,
                                onItemClick = {
                                    val domainTrack = Track(
                                        trackId = appTrack.trackId,
                                        trackName = appTrack.trackName,
                                        artistName = appTrack.artistName,
                                        trackTimeMillis = parseTimeToMillis(appTrack.trackTime),
                                        artworkUrl100 = appTrack.artworkUrl100,
                                        previewUrl = null
                                    )
                                    scope.launch {
                                        playlistViewModel.addTrackToPlaylist(domainTrack, pl.id)
                                        showSheet = false
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}
private fun parseTimeToMillis(trackTime: String): Long {
    val parts = trackTime.split(":")
    val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return (minutes * 60 + seconds) * 1000L
}