package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val EthioDarkColorScheme = darkColorScheme(
    primary = BrightGold,
    secondary = LightGold,
    tertiary = LightBlueAccent,
    background = DarkBlueBg,
    surface = DarkBlueSurface,
    onPrimary = Color(0xFF221100),
    onSecondary = Color.Black,
    onBackground = IceWhite,
    onSurface = IceWhite,
    outline = CardBorderGold
)

private val EthioLightColorScheme = lightColorScheme(
    primary = MatteGold,
    secondary = BrightGold,
    tertiary = LightBlueAccent,
    background = DarkBlueBg,
    surface = DarkBlueSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = IceWhite,
    onSurface = IceWhite,
    outline = CardBorderGold
)

@Composable
fun EthioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We enforce our luxurious Dark Blue & Gold Theme across dark/light mode for unified branding
    val colorScheme = if (darkTheme) EthioDarkColorScheme else EthioLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
