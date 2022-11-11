package com.appdev.eateryblueandroid.ui.components.general

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel
import kotlin.math.min

@Composable
fun BottomSheet(
    bottomSheetViewModel: BottomSheetViewModel
) {
    val state = bottomSheetViewModel.state.collectAsState()
    val target = if (state.value is BottomSheetViewModel.State.Visible) 0f else 1f
    val hiddenFraction = animateFloatAsState(
        targetValue = target,
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        ),
        finishedListener = {
            if (state.value is BottomSheetViewModel.State.Hiding) {
                bottomSheetViewModel.hidden()
            }
        }
    ).value

    state.value.let { stateValue ->
        when (stateValue) {
            is BottomSheetViewModel.State.Hidden -> Column() {}
            is BottomSheetViewModel.State.Hiding ->
                BottomSheetOverlay(
                    hide = { },
                    contents = (stateValue.contents),
                    hiddenFraction = hiddenFraction
                )
            is BottomSheetViewModel.State.Visible ->
                BottomSheetOverlay(
                    hide = { bottomSheetViewModel.hide() },
                    contents = (stateValue.contents),
                    hiddenFraction = hiddenFraction,
                    onSpaceTap = stateValue.onHide
                )
        }
    }
}

@Composable
fun BottomSheetOverlay(
    hide: (@Composable () -> Unit) -> Unit,
    contents: @Composable () -> Unit,
    hiddenFraction: Float,
    onSpaceTap : () -> Unit = { }
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onSpaceTap()
                hide(contents) }
            .background(
                Color.Black.copy(
                    alpha = min(
                        1 - hiddenFraction,
                        0.6f
                    )
                )
            ),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(modifier = Modifier.fillMaxHeight(hiddenFraction)) {}
        Surface(
            shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(interactionSource = interactionSource, indication = null) {}
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                contents()
            }
        }
        BackHandler {
            hide(contents)
        }
    }
}