package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.materialTheme.YS

private val ColorWhite = Color.White
private val ColorBlack = Color.Black
private val BgColorLight = ColorWhite
private val ListBgLight = ColorWhite
private val TextColorLight = ColorBlack
private val IconTintLight = Color(0xFFAEAFB4)
private val TrackColorLight = Color(0xFFE6E8EB)
private val KnobColorLight = Color(0xFFAEAFB4)

private val BgColorDark = Color(0xFF1A1B22)
private val ListBgDark = BgColorDark
private val TextColorDark = ColorWhite
private val IconTintDark = ColorWhite
private val ActiveBlueColor = Color(0xFF3772E7)
private val TrackFallbackDark = Color(0xFF2C3E66)
private val KnobFallbackDark = Color(0xFFAEAFB4)

// Переключатель с анимацией
@Composable
fun CustomSwitch(
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    darkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val knobOffset by animateDpAsState(if (isChecked) 17.dp else 0.dp)
    val trackColor by animateColorAsState(
        targetValue = when {
            isChecked -> ActiveBlueColor.copy(alpha = 0.48f)
            darkTheme -> TrackFallbackDark
            else -> TrackColorLight
        }
    )
    val knobColor by animateColorAsState(
        targetValue = when {
            isChecked -> ActiveBlueColor
            darkTheme -> KnobFallbackDark.copy(alpha = 0.6f)
            else -> KnobColorLight
        }
    )
    Box(
        modifier = modifier
            .width(35.dp)
            .height(18.dp)
            .clickable { onToggle(!isChecked) }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(35.dp)
                .height(12.dp)
                .clip(MaterialTheme.shapes.small)
                .background(trackColor)
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(18.dp)
                .offset(x = knobOffset)
                .background(knobColor, shape = CircleShape)
        )
    }
}

// Основная страница настроек
@Composable
fun SettingsScreen(
    darkThemeEnabled: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val titleTextColor = if (!darkThemeEnabled) TextColorLight else TextColorDark
    val backgroundColor = if (!darkThemeEnabled) BgColorLight else BgColorDark
    val shareMsg = stringResource(R.string.share_message)
    val email = stringResource(R.string.support_email)
    val emailSubject = stringResource(R.string.support_subject)
    val emailBody = stringResource(R.string.support_body)
    val userAgreementUrl = stringResource(R.string.user_agreement_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Заголовок с кнопкой назад
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(start = 16.dp)
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left_icon),
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier.size(24.dp),
                    tint = titleTextColor
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.settings_title),
                fontFamily = YS,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Medium,
                color = titleTextColor
            )
        }

        // Меню настроек
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SettingsMenuItem(
                    titleText = stringResource(R.string.settings_item_toggle),
                    isSwitchItem = true,
                    switchState = darkThemeEnabled,
                    onSwitchChanged = onThemeToggle,
                    darkTheme = darkThemeEnabled
                )
            }
            item {
                SettingsMenuItem(
                    titleText = stringResource(R.string.settings_item_share),
                    iconResId = R.drawable.ic_share_button,
                    onItemClicked = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareMsg)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, null))
                    },
                    darkTheme = darkThemeEnabled
                )
            }
            item {
                SettingsMenuItem(
                    titleText = stringResource(R.string.settings_item_support),
                    iconResId = R.drawable.ic_support_button,
                    onItemClicked = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:".toUri()
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                            putExtra(Intent.EXTRA_TEXT, emailBody)
                        }
                        context.startActivity(emailIntent)
                    },
                    darkTheme = darkThemeEnabled
                )
            }
            item {
                SettingsMenuItem(
                    titleText = stringResource(R.string.settings_item_agreement),
                    iconResId = R.drawable.ic_chevron_right_icon,
                    onItemClicked = {
                        val agreementIntent = Intent(Intent.ACTION_VIEW, userAgreementUrl.toUri())
                        context.startActivity(agreementIntent)
                    },
                    darkTheme = darkThemeEnabled
                )
            }
        }
    }
}

// Компонент пункта меню
@Composable
fun SettingsMenuItem(
    titleText: String,
    isSwitchItem: Boolean = false,
    switchState: Boolean = false,
    onSwitchChanged: (Boolean) -> Unit = {},
    onItemClicked: () -> Unit = {},
    iconResId: Int? = null,
    darkTheme: Boolean = false
) {
    val textColor = if (!darkTheme) TextColorLight else TextColorDark
    val iconTint = if (!darkTheme) IconTintLight else IconTintDark
    val backgroundColor = if (!darkTheme) ListBgLight else ListBgDark

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .background(backgroundColor)
            .clickable(enabled = !isSwitchItem) { onItemClicked() }
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titleText,
            fontFamily = YS,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Normal,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        if (isSwitchItem) {
            CustomSwitch(
                isChecked = switchState,
                onToggle = onSwitchChanged,
                darkTheme = darkTheme
            )
        } else {
            iconResId?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = titleText,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint
                )
            }
        }
    }
}