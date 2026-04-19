package com.cornellappdev.android.eatery.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = EateryBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlue,
    onPrimaryContainer = Black,
    secondary = GrayFive,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Black,
    surface = Color.White,
    onSurface = Black,
    error = Red,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = EateryBlue,
    onPrimary = Color.White,
    primaryContainer = GraySix,
    onPrimaryContainer = Color.White,
    secondary = GrayThree,
    onSecondary = Black,
    background = Black,
    onBackground = GrayZero,
    surface = Black,
    onSurface = GrayZero,
    error = Red,
    onError = Color.White
)

@Composable
fun EateryBlueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
            // todo - dark mode later
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // todo - dark mode later
//        darkTheme -> DarkColorScheme
        darkTheme -> LightColorScheme
        else -> LightColorScheme
    }

    // For bottom sheet background colors
    val colorScheme = baseColorScheme.copy(
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = Color.White,
        surfaceContainer = Color.White,
        surfaceContainerHigh = Color.White,
        surfaceContainerHighest = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EateryBlueMaterial3Typography,
        content = content
    )
}