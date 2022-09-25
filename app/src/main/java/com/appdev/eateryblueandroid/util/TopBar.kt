package com.appdev.eateryblueandroid.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.appdev.eateryblueandroid.util.Constants.eateryBlueColorTransparent

var statusBarOverrideColor : MutableState<Color> = mutableStateOf(eateryBlueColorTransparent)
var statusBarType : MutableState<ColorType> = mutableStateOf(ColorType.INTERP)

enum class ColorType {
    INSTANT, INTERP
}

fun overrideStatusBarColor(color : Color, type : ColorType) {
    statusBarOverrideColor.value = color
    statusBarType.value = type
}
