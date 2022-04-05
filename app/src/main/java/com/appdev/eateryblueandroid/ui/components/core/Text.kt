package com.appdev.eateryblueandroid.ui.components.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.appdev.eateryblueandroid.R
import androidx.compose.material.Text as AndroidText

enum class TextStyle {
    HEADER_H1,
    HEADER_H2,
    HEADER_H3,
    HEADER_H4,
    HEADER_H5,
    HEADER_H6,
    BODY_NORMAL,
    BODY_MEDIUM,
    BODY_SEMIBOLD,
    LABEL_NORMAL,
    LABEL_MEDIUM,
    LABEL_SEMIBOLD,
    MISC_BACK,
    SUBTITLE,
    APPDEV_BODY_MEDIUM,
    HEADER_H1_NORMAL
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.BODY_NORMAL,
    color: Color = colorResource(id = R.color.black),
    maxLines: Int = Integer.MAX_VALUE
) {
    AndroidText(
        text = text,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        color = color,
        fontWeight = fontWeight(textStyle),
        fontSize = fontSize(textStyle),
        letterSpacing = 0.sp,
        modifier = modifier
    )
}

private fun fontSize(textStyle: TextStyle): TextUnit {
    return if (textStyle == TextStyle.HEADER_H1) {
        36.sp
    } else if (textStyle == TextStyle.HEADER_H2) {
        34.sp
    } else if (textStyle == TextStyle.HEADER_H3) {
        24.sp
    } else if (textStyle == TextStyle.HEADER_H4) {
        18.sp
    } else if (textStyle == TextStyle.HEADER_H5) {
        17.sp
    } else if (textStyle == TextStyle.HEADER_H6) {
        17.sp
    } else if (textStyle == TextStyle.BODY_NORMAL) {
        14.sp
    } else if (textStyle == TextStyle.BODY_MEDIUM) {
        14.sp
    } else if (textStyle == TextStyle.BODY_SEMIBOLD) {
        14.sp
    } else if (textStyle == TextStyle.LABEL_NORMAL) {
        12.sp
    } else if (textStyle == TextStyle.LABEL_MEDIUM) {
        12.sp
    } else if (textStyle == TextStyle.LABEL_SEMIBOLD) {
        12.sp
    } else if (textStyle == TextStyle.MISC_BACK) {
        17.sp
    } else if (textStyle == TextStyle.SUBTITLE) {
        20.sp
    } else if (textStyle == TextStyle.APPDEV_BODY_MEDIUM) {
        18.sp
    } else if (textStyle == TextStyle.HEADER_H1_NORMAL) {
        36.sp
    }
    else {
        100.sp
    }
}

private fun fontWeight(textStyle: TextStyle): FontWeight {
    return if (textStyle == TextStyle.HEADER_H1) {
        FontWeight.Bold
    } else if (textStyle == TextStyle.HEADER_H2) {
        FontWeight.SemiBold
    } else if (textStyle == TextStyle.HEADER_H3) {
        FontWeight.SemiBold
    } else if (textStyle == TextStyle.HEADER_H4) {
        FontWeight.SemiBold
    } else if (textStyle == TextStyle.HEADER_H5) {
        FontWeight.SemiBold
    } else if (textStyle == TextStyle.HEADER_H6) {
        FontWeight.Normal
    } else if (textStyle == TextStyle.BODY_NORMAL) {
        FontWeight.Normal
    } else if (textStyle == TextStyle.BODY_MEDIUM) {
        FontWeight.Medium
    } else if (textStyle == TextStyle.BODY_SEMIBOLD) {
        FontWeight.SemiBold
    } else if (textStyle == TextStyle.LABEL_NORMAL) {
        FontWeight.Normal
    } else if (textStyle == TextStyle.LABEL_MEDIUM) {
        FontWeight.Medium
    } else if (textStyle == TextStyle.LABEL_SEMIBOLD) {
        FontWeight.SemiBold
    } else if (textStyle == TextStyle.MISC_BACK) {
        FontWeight.Normal
    } else if (textStyle == TextStyle.SUBTITLE) {
        FontWeight.SemiBold
    } else if (textStyle == TextStyle.APPDEV_BODY_MEDIUM) {
        FontWeight.Medium
    } else if (textStyle == TextStyle.HEADER_H1_NORMAL) {
        FontWeight.Normal
    } else {
        FontWeight.Black
    }
}