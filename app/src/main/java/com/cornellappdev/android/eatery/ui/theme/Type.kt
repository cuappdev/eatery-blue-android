package com.cornellappdev.android.eatery.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val body1Style = TextStyle(fontSize = 16.sp)
private val body2Style = TextStyle(fontSize = 14.sp)
private val captionStyle = TextStyle(fontSize = 12.sp)

// Keeps legacy style names while backing them with Material3 TextStyle values.
data class EateryLegacyTypography(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val h5: TextStyle,
    val h6: TextStyle,
    val subtitle1: TextStyle,
    val subtitle2: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val caption: TextStyle,
    val button: TextStyle
)

val EateryBlueTypography = EateryLegacyTypography(
    h1 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 48.sp),
    h2 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 36.sp),
    h3 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 34.sp),
    h4 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    h5 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    h6 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    subtitle1 = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
    subtitle2 = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
    body1 = body1Style,
    body2 = body2Style,
    caption = captionStyle,
    button = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
)

val EateryBlueMaterial3Typography = Typography(
    displayLarge = EateryBlueTypography.h1,
    displayMedium = EateryBlueTypography.h2,
    displaySmall = EateryBlueTypography.h3,
    headlineMedium = EateryBlueTypography.h4,
    titleLarge = EateryBlueTypography.h5,
    titleMedium = EateryBlueTypography.h6,
    titleSmall = EateryBlueTypography.subtitle1,
    bodyLarge = EateryBlueTypography.body1,
    bodyMedium = EateryBlueTypography.body2,
    bodySmall = EateryBlueTypography.caption,
    labelLarge = EateryBlueTypography.button
)

