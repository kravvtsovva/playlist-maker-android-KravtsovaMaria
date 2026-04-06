package com.practicum.playlistmaker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.AppTrack
import com.practicum.playlistmaker.ui.materialTheme.YS
import kotlin.math.max

@Composable
fun TrackItem(
    track: AppTrack,
    darkTheme: Boolean,
    onItemClick: () -> Unit = {},
    onItemLongPress: (() -> Unit)? = null
) {
    val backgroundColor = if (!darkTheme) Color.White else Color(0xFF1A1B22)
    val primaryTextColor = if (!darkTheme) Color(0xFF1A1B22) else Color.White
    val secondaryTextColor = if (!darkTheme) Color(0xFFAEAFB4) else Color.White.copy(alpha = 0.84f)
    val iconTint = if (!darkTheme) Color(0xFFAEAFB4) else Color.White

    val modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 61.dp)
        .background(backgroundColor)
        .let { mod ->
            if (onItemLongPress != null) {
                mod.combinedClickable(
                    onClick = onItemClick,
                    onLongClick = onItemLongPress
                )
            } else {
                mod.clickable(onClick = onItemClick)
            }
        }
        .padding(horizontal = 13.dp, vertical = 8.dp)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100 ?: R.drawable.ic_music_image,
            placeholder = painterResource(R.drawable.ic_music_image),
            error = painterResource(R.drawable.ic_music_image),
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(0.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = track.trackName,
                fontFamily = YS,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                color = primaryTextColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            ArtistDuration(
                artistName = track.artistName,
                duration = track.trackTime,
                textColor = secondaryTextColor
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right_icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ArtistDuration(
    artistName: String,
    duration: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Layout(
        modifier = modifier.fillMaxWidth(),
        content = {
            ArtistText(artistName = artistName, textColor = textColor)
            DurationLabel(duration = duration, textColor = textColor)
        }
    ) { measurables, constraints ->
        val durationMeasurable = measurables[1]
        val durationPlaceable = durationMeasurable.measure(constraints.copy(minWidth = 0, minHeight = 0))

        val artistConstraints = constraints.copy(
            maxWidth = constraints.maxWidth - durationPlaceable.width,
            minWidth = 0
        )
        val artistPlaceable = measurables[0].measure(artistConstraints)

        val width = constraints.maxWidth
        val height = max(artistPlaceable.height, durationPlaceable.height)

        layout(width, height) {
            artistPlaceable.placeRelative(0, (height - artistPlaceable.height) / 2)
            durationPlaceable.placeRelative(artistPlaceable.width, (height - durationPlaceable.height) / 2)
        }
    }
}

@Composable
private fun ArtistText(artistName: String, textColor: Color) {
    Text(
        text = artistName,
        fontFamily = YS,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 13.sp,
        color = textColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun DurationLabel(duration: String, textColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(3.dp)
                .background(textColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = duration,
            fontFamily = YS,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            color = textColor
        )
    }
}