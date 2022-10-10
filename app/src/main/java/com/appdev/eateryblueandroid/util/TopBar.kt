package com.appdev.eateryblueandroid.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.appdev.eateryblueandroid.util.Constants.eateryBlueColor
import com.appdev.eateryblueandroid.util.Constants.eateryBlueColorTransparent

private var statusBarOverrideColor : MutableState<Color> = mutableStateOf(eateryBlueColor)
private var statusBarType : MutableState<ColorType> = mutableStateOf(ColorType.INTERP)

enum class ColorType {
    INSTANT, INTERP
}

fun overrideStatusBarColor(color : Color, type : ColorType) {
    statusBarOverrideColor.value = color
    statusBarType.value = type
}

fun statusBarColorState() : State<Color> = statusBarOverrideColor
fun statusBarTypeState() : State<ColorType> = statusBarType