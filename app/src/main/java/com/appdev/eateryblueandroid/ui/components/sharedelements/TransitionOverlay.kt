package com.appdev.eateryblueandroid.ui.components.sharedelements

import androidx.compose.ui.graphics.Color
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
internal fun TransitionOverlay(state: State) {
    val animation = updateTransition(targetState = state.showingEateryDetail, label = "Transition")
    Box {
        state.transitions.forEach { mapEntry ->
            val shouldDisplay = animation.currentState != state.showingEateryDetail
                    && mapEntry.value.eateryId == state.lastEateryClicked

            if (shouldDisplay) {
                val position by animation.animateIntOffset(
                    label = "Positioning"
                ) { state ->
                    when(state) {
                        false -> mapEntry.value.start?.offset ?: IntOffset(0,0)
                        true -> mapEntry.value.end?.offset ?: IntOffset(0,0)
                    }
                }

                Box(modifier = Modifier
                    .offset { position }
                ) {
                    when(state.showingEateryDetail) {
                        false -> mapEntry.value.start?.contents?.invoke()
                        true -> mapEntry.value.end?.contents?.invoke()
                    }
                }
            }
        }
    }
}