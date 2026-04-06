package com.practicum.playlistmaker.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.materialTheme.YS

private val HighlightBlue = Color(0xFF3772E7)
private val GrayText = Color(0xFFAEAFB4)
private val LightBackground = Color.White
private val DarkBackground = Color(0xFF1A1B22)
private val BlackTitle = Color(0xFF1A1B22)
private val WhiteTitle = Color.White

@Composable
fun MainScreenView(
    onSearchTap: () -> Unit,
    onSettingsTap: () -> Unit,
    onPlaylistTap: () -> Unit,
    onFavoritesTap: () -> Unit,
    darkThemeEnabled: Boolean
) {
    val chevronColor = if (darkThemeEnabled) WhiteTitle else GrayText
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HighlightBlue)
                .padding(horizontal = 16.dp, vertical = 28.dp)
        ) {
            Text(
                text = stringResource(R.string.playlist_maker),
                color = Color.White,
                fontFamily = YS,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
        val backgroundContainer = if (darkThemeEnabled) DarkBackground else LightBackground
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(backgroundContainer)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { MenuEntry(iconResId = R.drawable.ic_search_main_icon, label = stringResource(R.string.search), onClick = onSearchTap, chevronColor, darkThemeEnabled) }
                item { MenuEntry(iconResId = R.drawable.ic_playlists_icon, label = stringResource(R.string.playlists), onClick = onPlaylistTap, chevronColor, darkThemeEnabled) }
                item { MenuEntry(iconResId = R.drawable.ic_favorite_heart, label = stringResource(R.string.favorites), onClick = onFavoritesTap, chevronColor, darkThemeEnabled) }
                item { MenuEntry(iconResId = R.drawable.ic_settings_icon, label = stringResource(R.string.settings), onClick = onSettingsTap, chevronColor, darkThemeEnabled) }
            }
        }
    }
}

@Composable
private fun MenuEntry(
    iconResId: Int,
    label: String,
    onClick: () -> Unit,
    chevronColor: Color,
    darkTheme: Boolean
) {
    val bgColor = if (darkTheme) DarkBackground else LightBackground
    val textColor = if (darkTheme) WhiteTitle else BlackTitle
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp)
            .background(bgColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = textColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = textColor,
            fontFamily = YS,
            fontSize = 22.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right_icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = chevronColor
        )
    }
}