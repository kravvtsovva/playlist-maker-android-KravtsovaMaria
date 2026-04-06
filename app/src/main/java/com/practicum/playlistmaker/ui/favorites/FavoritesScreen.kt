package com.practicum.playlistmaker.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.AppTrack
import com.practicum.playlistmaker.ui.materialTheme.YS
import androidx.compose.ui.graphics.Color
import com.practicum.playlistmaker.presentation.toAppTrack
import com.practicum.playlistmaker.ui.components.TrackItem

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onBackClicked: () -> Unit,
    onTrackSelected: (AppTrack) -> Unit,
    darkMode: Boolean
) {
    val favoriteTracks by viewModel.tracksFavoriteState.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
    }

    val bgColor = if (darkMode) Color(0xFF1A1B22) else Color.White
    val fontColor = if (darkMode) Color.White else Color(0xFF1A1B22)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
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
                IconButton(onClick = onBackClicked) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left_icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = fontColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.favorites),
                    fontFamily = YS,
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Medium,
                    color = fontColor
                )
            }

            if (favoriteTracks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.padding(top = 150.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val iconResId = if (darkMode) R.drawable.ic_no_results_black else R.drawable.ic_no_results_grey

                        Icon(
                            painter = painterResource(iconResId),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = Color.Unspecified
                        )

                        Text(
                            text = stringResource(R.string.favorites_empty),
                            fontFamily = YS,
                            fontSize = 19.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = fontColor,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(favoriteTracks) { _, track ->
                        TrackItem(
                            track = track.toAppTrack(),
                            darkTheme = darkMode,
                            onItemClick = { onTrackSelected(track.toAppTrack()) },
                            onItemLongPress = {
                                viewModel.updateFavoriteStatus(track, false)
                            }
                        )
                    }
                }
            }
        }
    }
}