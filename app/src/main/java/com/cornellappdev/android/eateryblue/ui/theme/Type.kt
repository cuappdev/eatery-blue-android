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
    subtitle1 = defaultTypography.subtitle1.copy(fontWeight = FontWeight.Medium),
    subtitle2 = defaultTypography.subtitle2.copy(fontWeight = FontWeight.Medium),
    button = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
)
//
//private fun fontSize(textStyle: TextStyle): TextUnit {
//    return if (textStyle == TextStyle.HEADER_H1) {
//        36.sp
//    } else if (textStyle == TextStyle.HEADER_H2) {
//        34.sp
//    } else if (textStyle == TextStyle.HEADER_H3) {
//        24.sp
//    } else if (textStyle == TextStyle.HEADER_H4) {
//        18.sp
//    } else if (textStyle == TextStyle.HEADER_H5) {
//        17.sp
//    } else if (textStyle == TextStyle.HEADER_H6) {
//        17.sp
//    } else if (textStyle == TextStyle.BODY_NORMAL) {
//        14.sp
//    } else if (textStyle == TextStyle.BODY_MEDIUM) {
//        14.sp
//    } else if (textStyle == TextStyle.BODY_SEMIBOLD) {
//        14.sp
//    } else if (textStyle == TextStyle.LABEL_NORMAL) {
//        12.sp
//    } else if (textStyle == TextStyle.LABEL_MEDIUM) {
//        12.sp
//    } else if (textStyle == TextStyle.LABEL_SEMIBOLD) {
//        12.sp
//    } else if (textStyle == TextStyle.MISC_BACK) {
//        17.sp
//    } else if (textStyle == TextStyle.SUBTITLE) {
//        20.sp
//    } else if (textStyle == TextStyle.APPDEV_BODY_MEDIUM) {
//        18.sp
//    } else if (textStyle == TextStyle.HEADER_H1_NORMAL) {
//        36.sp
//    } else if (textStyle == TextStyle.SUPER_TITLE) {
//        48.sp
//    }
//    else {
//        100.sp
//    }
//}
//
//private fun fontWeight(textStyle: TextStyle): FontWeight {
//    return if (textStyle == TextStyle.HEADER_H1) {
//        FontWeight.Bold
//    } else if (textStyle == TextStyle.HEADER_H2) {
//        FontWeight.SemiBold
//    } else if (textStyle == TextStyle.HEADER_H3) {
//        FontWeight.SemiBold
//    } else if (textStyle == TextStyle.HEADER_H4) {
//        FontWeight.SemiBold
//    } else if (textStyle == TextStyle.HEADER_H5) {
//        FontWeight.SemiBold
//    } else if (textStyle == TextStyle.HEADER_H6) {
//        FontWeight.Normal
//    } else if (textStyle == TextStyle.BODY_NORMAL) {
//        FontWeight.Normal
//    } else if (textStyle == TextStyle.BODY_MEDIUM) {
//        FontWeight.Medium
//    } else if (textStyle == TextStyle.BODY_SEMIBOLD) {
//        FontWeight.SemiBold
//    } else if (textStyle == TextStyle.LABEL_NORMAL) {
//        FontWeight.Normal
//    } else if (textStyle == TextStyle.LABEL_MEDIUM) {
//        FontWeight.Medium
//    } else if (textStyle == TextStyle.LABEL_SEMIBOLD) {
//        FontWeight.SemiBold
//    } else if (textStyle == TextStyle.MISC_BACK) {
//        FontWeight.Normal
//    } else if (textStyle == TextStyle.SUBTITLE) {
//        FontWeight.SemiBold
//    } else if (textStyle == TextStyle.APPDEV_BODY_MEDIUM) {
//        FontWeight.Medium
//    } else if (textStyle == TextStyle.HEADER_H1_NORMAL) {
//        FontWeight.Normal
//    } else if (textStyle == TextStyle.SUPER_TITLE) {
//        FontWeight.Bold
//    } else {
//        FontWeight.Black
//    }
//}
//
