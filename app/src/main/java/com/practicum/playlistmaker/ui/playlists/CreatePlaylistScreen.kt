package com.practicum.playlistmaker.ui.playlists

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.materialTheme.YS
import kotlinx.coroutines.launch
import com.practicum.playlistmaker.utils.saveImageToInternalStorage


@Composable
fun CreatePlaylistScreen(
    viewModel: PlaylistManagementViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    isDarkTheme: Boolean
) {
    val scope = rememberCoroutineScope()
    val namePlaceholder = stringResource(R.string.playlist_name)
    val descriptionPlaceholder = stringResource(R.string.playlist_description)
    var name by remember { mutableStateOf(namePlaceholder) }
    var description by remember { mutableStateOf(descriptionPlaceholder) }
    var nameFocused by remember { mutableStateOf(false) }
    var descriptionFocused by remember { mutableStateOf(false) }
    val coverImageUriString: String? by viewModel.playlistCoverUri.collectAsState(null)
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val localPath = saveImageToInternalStorage(context, it)
            localPath?.let { path ->
                viewModel.updateCoverUri(path)
            }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        }
    }
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1B22) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1A1B22)
    val labelColor = Color(0xFF3772E7)
    val borderColor = Color(0xFFAEAFB4)
    val buttonColor = if (name.isNotBlank() && name != namePlaceholder) Color(0xFF3772E7) else Color(0xFFAEAFB4)
    val buttonTextColor = Color.White

    Box(modifier = Modifier
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
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = textColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.new_playlist),
                    fontFamily = YS,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.height(132.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(100.dp)
                    .clickable {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            imagePickerLauncher.launch("image/*")
                        } else {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                imagePickerLauncher.launch("image/*")
                            } else {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                coverImageUriString?.let { uriString ->
                    AsyncImage(
                        model = uriString.toUri(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.ic_add_playlist_photo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(borderColor)
                )
            }
            Spacer(modifier = Modifier.height(88.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            val focused = focusState.isFocused
                            if (focused && name == namePlaceholder) {
                                name = ""
                            }
                            if (!focused && name.isBlank()) {
                                name = namePlaceholder
                            }
                            nameFocused = focused
                        },
                    shape = RoundedCornerShape(4.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = backgroundColor,
                        unfocusedContainerColor = backgroundColor,
                        focusedIndicatorColor = labelColor,
                        unfocusedIndicatorColor = borderColor,
                        focusedLabelColor = labelColor,
                        unfocusedLabelColor = labelColor,
                        cursorColor = labelColor
                    ),
                    label = if (nameFocused || (name.isNotBlank() && name != namePlaceholder)) {
                        {
                            Text(
                                text = namePlaceholder,
                                fontFamily = YS,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = labelColor
                            )
                        }
                    } else null,
                    placeholder = {},
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = YS,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        color = if (name == namePlaceholder && !nameFocused) {
                            if (isDarkTheme) Color.White else Color(0xFF1A1B22)
                        } else textColor
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(72.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            val focused = focusState.isFocused
                            if (focused && description == descriptionPlaceholder) {
                                description = ""
                            }
                            if (!focused && description.isBlank()) {
                                description = descriptionPlaceholder
                            }
                            descriptionFocused = focused
                        },
                    shape = RoundedCornerShape(4.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = backgroundColor,
                        unfocusedContainerColor = backgroundColor,
                        focusedIndicatorColor = labelColor,
                        unfocusedIndicatorColor = borderColor,
                        focusedLabelColor = labelColor,
                        unfocusedLabelColor = labelColor,
                        cursorColor = labelColor
                    ),
                    label = if (descriptionFocused || (description.isNotBlank() && description != descriptionPlaceholder)) {
                        {
                            Text(
                                text = descriptionPlaceholder,
                                fontFamily = YS,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = labelColor
                            )
                        }
                    } else null,
                    placeholder = {},
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = YS,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        color = if (description == descriptionPlaceholder && !descriptionFocused) {
                            if (isDarkTheme) Color.White else Color(0xFF1A1B22)
                        } else textColor
                    ),
                    singleLine = true
                )
            }
        }
        Button(
            onClick = {
                scope.launch {
                    if (name.isNotBlank() && name != namePlaceholder) {
                        val desc = if (description == descriptionPlaceholder) "" else description
                        viewModel.createPlaylist(name.trim(), desc.trim())
                        onSaved()
                    }
                }
            },
            enabled = name.isNotBlank() && name != namePlaceholder,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.dp)
                .width(326.dp)
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = buttonTextColor)
        ) {
            Text(
                text = stringResource(R.string.create),
                fontFamily = YS,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 19.sp
            )
        }
    }
}