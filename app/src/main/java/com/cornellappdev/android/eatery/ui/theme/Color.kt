package com.cornellappdev.android.eatery.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

val EateryBlue = Color(0xFF4A90E2)
val GrayZero = Color(0xFFEFF1F4)
val GrayOne = Color(0xFFE1E4E8)
val GrayTwo = Color(0xFFD1D5DA)
val GrayThree = Color(0xFF959da5)
val GrayFive = Color(0xFF586069)
val GraySix = Color(0xFF444D56)
val LightBlue = Color(0xFFE8EFF8)
val LightRed = Color(0xFFFEF0EF)
val Red = Color(0xFFF2655D)
val Green = Color(0xFF63C774)
val Yellow = Color(0xFFFEC50E)
val Orange = Color(0xFFFF990E)

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
