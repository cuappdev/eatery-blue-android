package com.appdev.eateryblueandroid.ui.components.sharedelements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import java.util.*

@Composable
fun SharedElementsRoot(children: @Composable() () -> Unit) {
    val rootState = remember { SharedElementsRootState() }
    children()
}

internal class SharedElementsRootState {
    var rootCoordinates: LayoutCoordinates? = null
}

