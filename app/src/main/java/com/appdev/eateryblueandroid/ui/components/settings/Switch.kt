package com.appdev.eateryblueandroid.ui.components.profile

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.annotation.Px
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R

@Composable
fun Switch(
    initialValue: Boolean = true,
    width: Dp = 51.dp,
    height: Dp = 31.dp,
    spaceBetweenThumbAndTrack: Dp = 2.dp,
    scale: Float = 1.0f,
    checkedTrackColor: Color = colorResource(id = R.color.eateryBlue),
    checkedThumbColor: Color = colorResource(id = R.color.white),
    uncheckedTrackColor: Color = colorResource(id = R.color.gray01),
    uncheckedThumbColor: Color = colorResource(id = R.color.white),
    onCheckedChange : (Boolean) -> Unit,
    enabled : Boolean = true
) {
    var checked by remember { mutableStateOf(initialValue) }
    val thumbRadius = height / 2 - spaceBetweenThumbAndTrack
    val view = LocalView.current
    val animatePos = animateFloatAsState(
        targetValue =
        if (checked) {
            with(LocalDensity.current) {
                (width - thumbRadius - spaceBetweenThumbAndTrack).toPx()
            }
        } else {
            with(LocalDensity.current) {
                (thumbRadius + spaceBetweenThumbAndTrack).toPx()
            }
        }
    )
    val animateColor = animateColorAsState(
        targetValue =
        if (checked) {
            checkedTrackColor
        } else {
            uncheckedTrackColor
        }
    )


    Canvas(
        modifier = Modifier
            .size(width = width, height = height)
            .scale(scale)
            .pointerInput(enabled) {
                detectTapGestures(
                    onTap = {
                        if (enabled) {
                            checked = !checked
                            onCheckedChange(checked)
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }
                    }
                )
            }
            .alpha(if (enabled) 1.0f else .5f)
    ) {
        drawRoundRect(
            color = animateColor.value,
            cornerRadius = CornerRadius(x = (height / 2).toPx(), y = (height / 2).toPx())
        )
        drawCircle(
            color = if (checked) checkedThumbColor else uncheckedThumbColor,
            radius = thumbRadius.toPx(),
            center = Offset(
                x = animatePos.value,
                y = size.height / 2
            )
        )
    }
}
