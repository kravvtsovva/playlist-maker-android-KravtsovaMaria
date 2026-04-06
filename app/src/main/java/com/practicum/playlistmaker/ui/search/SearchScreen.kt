package com.practicum.playlistmaker.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.AppTrack
import com.practicum.playlistmaker.presentation.SearchState
import com.practicum.playlistmaker.ui.components.TrackItem
import com.practicum.playlistmaker.ui.materialTheme.YS

private val DividerPadding = 9.5.dp
private val SearchFieldBgLight = Color(0xFFE6E8EB)
private val SearchFieldBgDark = Color(0xFFE6E8EB)
private val PlaceholderLight = Color(0xFFAEAFB4)
private val PlaceholderDark = Color(0xFF1A1B22)
private val CursorColor = Color(0xFF3F8AE0)

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    isDarkTheme: Boolean,
    onBackClick: () -> Unit,
    onTrackClick: (AppTrack) -> Unit
) {
    val query by viewModel.searchText.collectAsState()
    val state by viewModel.state.collectAsState()
    val history by viewModel.getHistory().collectAsState(emptyList())
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1B22) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1A1B22)
    var isFocused by remember { mutableStateOf(false) }
    val showHistory = (state is SearchState.Empty) && history.isNotEmpty() && isFocused && query.isEmpty()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.size(24.dp),
                        tint = textColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.search),
                    fontFamily = YS,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    color = textColor
                )
            }
            SearchBar(
                query = query,
                onQueryChange = viewModel::onSearchTextChanged,
                onSearch = viewModel::searchTracks,
                onClear = viewModel::clearSearch,
                isDarkTheme = isDarkTheme,
                onFocusChange = { focused -> isFocused = focused },
                showHistory = showHistory
            )
            if (showHistory) {
                HistoryList(
                    history = history,
                    isDarkTheme = isDarkTheme,
                    onHistoryClick = { q ->
                        viewModel.onSearchTextChanged(q)
                        viewModel.searchTracks()
                    }
                )
            }
            when (val currentState = state) {
                SearchState.Empty -> {
                }
                SearchState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CursorColor)
                }
                is SearchState.Content -> LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    items(
                        items = currentState.tracks,
                        key = { it.trackId }
                    ) { track ->
                        TrackItem(
                            track = track,
                            darkTheme = isDarkTheme,
                            onItemClick = { onTrackClick(track) },
                            onItemLongPress = null
                        )
                    }
                }
                SearchState.EmptyResult -> EmptyResultUI(isDarkTheme = isDarkTheme, textColor = textColor)
                is SearchState.Error -> ErrorUI(
                    messageResId = currentState.messageResId,
                    isDarkTheme = isDarkTheme,
                    textColor = textColor,
                    onRetry = { viewModel.searchTracks() }
                )
            }
        }
    }
}


@Composable
private fun HistoryList(
    history: List<String>,
    isDarkTheme: Boolean,
    onHistoryClick: (String) -> Unit
) {
    val placeholderColor = if (!isDarkTheme) PlaceholderLight else PlaceholderDark
    val bottomShape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(bottomShape)
                .background(if (!isDarkTheme) SearchFieldBgLight else SearchFieldBgDark)
        ) {
            HorizontalDivider(
                color = Color(0xFFAEAFB4),
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DividerPadding)
            )
            LazyColumn {
                items(history) { historyQuery ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onHistoryClick(historyQuery) }
                            .padding(vertical = 11.dp, horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search_history_clock_icon),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = placeholderColor
                        )
                        Text(
                            text = historyQuery,
                            fontFamily = YS,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF1A1B22)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyResultUI(isDarkTheme: Boolean, textColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(top = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (!isDarkTheme) R.drawable.ic_no_results_grey else R.drawable.ic_no_results_black
                ),
                contentDescription = stringResource(R.string.no_results),
                modifier = Modifier.size(120.dp),
                tint = Color.Unspecified
            )
            Text(
                text = stringResource(R.string.no_results),
                fontFamily = YS,
                fontSize = 19.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ErrorUI(
    messageResId: Int,
    isDarkTheme: Boolean,
    textColor: Color,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(top = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (!isDarkTheme) R.drawable.ic_search_failed_grey else R.drawable.ic_search_failed_black
                ),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = Color.Unspecified
            )
            Text(
                text = stringResource(messageResId),
                fontFamily = YS,
                fontSize = 19.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3772E7), contentColor = Color.White)
            ) {
                Text(stringResource(R.string.retry_button))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    isDarkTheme: Boolean,
    onFocusChange: (Boolean) -> Unit,
    showHistory: Boolean
) {
    val bg = if (!isDarkTheme) SearchFieldBgLight else SearchFieldBgDark
    val placeholderColor = if (!isDarkTheme) PlaceholderLight else PlaceholderDark
    val cursorColor = CursorColor
    val topShape = if (showHistory) {
        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
    } else {
        RoundedCornerShape(8.dp)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(topShape)
                .background(bg)
        ) {
            SearchFieldContent(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                onClear = onClear,
                placeholderColor = placeholderColor,
                cursorColor = cursorColor,
                onFocusChange = onFocusChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchFieldContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    placeholderColor: Color,
    cursorColor: Color,
    onFocusChange: (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search_16_size),
            contentDescription = stringResource(R.string.search),
            tint = placeholderColor,
            modifier = Modifier
                .size(16.dp)
                .clickable { onSearch() }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            if (query.isEmpty()) {
                Text(
                    text = stringResource(R.string.search),
                    fontFamily = YS,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    color = placeholderColor,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = YS,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    color = Color(0xFF1A1B22)
                ),
                cursorBrush = SolidColor(cursorColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        onFocusChange(focusState.isFocused)
                    },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    onSearch()
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }),
                interactionSource = interactionSource
            )
        }
        if (query.isNotEmpty()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_clear_icon),
                contentDescription = stringResource(R.string.clear),
                tint = placeholderColor,
                modifier = Modifier
                    .size(12.dp)
                    .clickable {
                        onClear()
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
            )
        }
    }
}