package com.cornellappdev.android.eatery.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalColorMode = staticCompositionLocalOf { ColorTheme.darkMode }

@Composable
fun AppColorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) ColorTheme.darkMode else ColorTheme.lightMode
    CompositionLocalProvider(LocalColorMode provides colors) {
        content()
    }
}

val currentColors: ColorMode
    @Composable get() = LocalColorMode.current

object ColorTheme
{
    val lightMode = ColorMode(
        error = ErrorLight,
        success = SuccessLight,
        backgroundDefault = BgDefaultLight,
        backgroundSecondary = BgSecondaryLight,
        backgroundDefault10 = BgDefault10Light,
        backgroundDefault92 = BgDefault92Light,
        backgroundSurface = BgSurfaceLight,
        textPrimary = TextPrimaryLight,
        textSecondary = TextSecondaryLight,
        accentPrimary = AccentPrimaryLight,
        accentPressed = AccentPressedLight,
        borderDefault = BorderDefaultLight
    )
    val darkMode = ColorMode(
        error = ErrorDark,
        success = SuccessDark,
        backgroundDefault = BgDefaultDark,
        backgroundSecondary = BgSecondaryDark,
        backgroundDefault10 = BgDefault10Dark,
        backgroundDefault92 = BgDefault92Dark,
        backgroundSurface = BgSurfaceDark,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        accentPrimary = AccentPrimaryDark,
        accentPressed = AccentPressedDark,
        borderDefault = BorderDefaultDark
    )

}

data class ColorMode(
    val error : Color,
    val success : Color,
    val backgroundDefault : Color,
    val backgroundSecondary : Color,
    val backgroundDefault10 : Color,
    val backgroundDefault92 : Color,
    val backgroundSurface : Color,
    val textPrimary : Color,
    val textSecondary : Color,
    val accentPrimary : Color,
    val accentPressed : Color,
    val borderDefault : Color
)
