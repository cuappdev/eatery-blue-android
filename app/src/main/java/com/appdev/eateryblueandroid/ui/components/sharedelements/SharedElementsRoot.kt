package com.appdev.eateryblueandroid.ui.components.sharedelements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import java.util.*

@Composable
fun SharedElementsRoot(children: @Composable() () -> Unit) {
    val rootState = remember { State() }
    CompositionLocalProvider(LocalState provides rootState) {
        children()
    }
    TransitionOverlay(state = rootState)
}