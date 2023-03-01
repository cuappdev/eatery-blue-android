package com.cornellappdev.android.eateryblue.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val defaultTypography = Typography()
val EateryBlueTypography = Typography(
    h1 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 48.sp),
    h2 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 36.sp),
    h3 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 34.sp),
    h4 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    h5 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    h6 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    caption = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
    subtitle1 = defaultTypography.subtitle1.copy(fontWeight = FontWeight.Medium),
    subtitle2 = defaultTypography.subtitle2.copy(fontWeight = FontWeight.Medium),
    button = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
)
