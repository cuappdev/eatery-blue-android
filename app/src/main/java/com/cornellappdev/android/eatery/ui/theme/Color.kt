package com.cornellappdev.android.eatery.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

// Error colors
val ErrorLight = Color(0xFFF2655D)
val ErrorDark = Color(0xFFF2655D)

// Success colors
val SuccessLight = Color(0xFF63C774)
val SuccessDark = Color(0xFF76CE85)

// Warning colors (e.g., for "Closing Soon" status)
val WarningLight = Color(0xFFFFA500)
val WarningDark = Color(0xFFFFA500)

// Background colors
val BgDefaultLight = Color(0xFFFFFFFF)
val BgDefaultDark = Color(0xFF141414)

val BgSecondaryLight = Color(0xFF4A90E2)
val BgSecondaryDark = Color(0xFF609EE6)

val BgDefault10Light = Color(0x1AFFFFFF)  // FFFFFF with 10% opacity
val BgDefault10Dark = Color(0x1A141414)   // 141414 with 10% opacity

val BgDefault92Light = Color(0xEBFFFFFF)  // FFFFFF with 92% opacity
val BgDefault92Dark = Color(0xEB141414)   // 141414 with 92% opacity

val BgSurfaceLight = Color(0xFFFAFAFA)
val BgSurfaceDark = Color(0xFF121212)

val OppTextPrimaryLight = Color(0xFFFFFFFF)
val OppTextPrimaryDark = Color(0xFF050505)
// Text colors
val TextPrimaryLight = Color(0xFF050505)
val TextPrimaryDark = Color(0xFFFFFFFF)

val TextSecondaryLight = Color(0xFF586069)
val TextSecondaryDark = Color(0xFF9EA8B5)

// Accent colors
val AccentPrimaryLight = Color(0xFFEFF1F4)
val AccentPrimaryDark = Color(0xFF272727)

val AccentPressedLight = Color(0xFFE8EFF8)
val AccentPressedDark = Color(0xFF1C1C1C)

// Border colors
val BorderDefaultLight = Color(0xFFE1E4E8)
val BorderDefaultDark = Color(0xFF282828)

// Favorite colors
val FavoriteLight = Color(0xFFFFD700)
val FavoriteDark = Color(0xFFFFD700)


/**
 * Interpolates a color between [color1] and [color2] by choosing a color a [fraction] in between.
 * Uses HSV interpolation, which generally gives more aesthetically pleasing results than RGB.
 *
 * @param fraction  Float in [0..1]. 0 = color1, 1 = color2. In between interpolates between.
 */
fun colorInterp(fraction: Float, color1: Color, color2: Color): Color {
    val fractionToUse = fraction.coerceIn(0f, 1f)
    val HSV1 = FloatArray(3)
    val HSV2 = FloatArray(3)
    android.graphics.Color.colorToHSV(color1.toArgb(), HSV1)
    android.graphics.Color.colorToHSV(color2.toArgb(), HSV2)

    for (i in 0..2) {
        HSV2[i] = interpolate(fractionToUse, HSV1[i], HSV2[i])
    }
    return Color.hsv(
        HSV2[0],
        HSV2[1],
        HSV2[2],
        interpolate(fractionToUse, color1.alpha, color2.alpha)
    )
}

/**
 * Interpolates between two floats.
 */
private fun interpolate(fraction: Float, a: Float, b: Float): Float {
    return a + (b - a) * fraction
}

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
        oppTextPrimary = OppTextPrimaryLight,
        textSecondary = TextSecondaryLight,
        contentBrand = BgSecondaryLight,
        contentSubtle = BgDefault10Light,
        accentPrimary = AccentPrimaryLight,
        accentPressed = AccentPressedLight,
        borderDefault = BorderDefaultLight,
        favorite = FavoriteLight,
        warning = WarningLight,
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
        oppTextPrimary = OppTextPrimaryDark,
        textSecondary = TextSecondaryDark,
        contentBrand = BgSecondaryDark,
        contentSubtle = BgDefault10Dark,
        accentPrimary = AccentPrimaryDark,
        accentPressed = AccentPressedDark,
        borderDefault = BorderDefaultDark,
        favorite = FavoriteDark,
        warning = WarningDark,
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
    val oppTextPrimary : Color,
    val textSecondary : Color,
    val contentBrand: Color,
    val contentSubtle: Color,
    val accentPrimary : Color,
    val accentPressed: Color,
    val borderDefault: Color,
    val favorite: Color,
    val warning: Color
)
