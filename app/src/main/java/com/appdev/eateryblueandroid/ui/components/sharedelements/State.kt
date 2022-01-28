package com.appdev.eateryblueandroid.ui.components.sharedelements

import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

internal val LocalState = compositionLocalOf<State> { error("No active state!") }

internal class State {
    val transitions = mutableStateMapOf<ElementTag, TransitionData>()
    var showingEateryDetail by mutableStateOf(false)
    var lastEateryClicked = -1

    fun register(
        tag: ElementTag,
        eateryId: Int,
        type: SharedElementType,
        offset: IntOffset,
        size: IntSize,
        contents: @Composable() () -> Unit
    ) {
        if (type == SharedElementType.FROM) {
            transitions[tag] = TransitionData.startRegistered(
                existing = transitions[tag],
                eateryId = eateryId,
                offset = offset,
                size = size,
                contents = contents
            )
        } else {
            lastEateryClicked = eateryId
            showingEateryDetail = true
            transitions[tag] = TransitionData.endRegistered(
                existing = transitions[tag],
                eateryId = eateryId,
                offset = offset,
                size = size,
                contents = contents,
            )
        }
    }

    fun hideEateryDetail() {
        showingEateryDetail = false
    }
}

internal class TransitionData(
    var start: Element?,
    var end: Element?,
    var eateryId: Int?
) {
    companion object {
        fun startRegistered(
            existing: TransitionData?,
            eateryId: Int,
            offset: IntOffset,
            size: IntSize,
            contents: @Composable () -> Unit
        ): TransitionData {
            return TransitionData(
                start = Element(offset, size, contents),
                end = existing?.end,
                eateryId = eateryId
            )
        }

        fun endRegistered(
            existing: TransitionData?,
            eateryId: Int,
            offset: IntOffset,
            size: IntSize,
            contents: @Composable() () -> Unit
        ): TransitionData {
            return TransitionData(
                start = existing?.start,
                end =  Element(offset, size, contents),
                eateryId = eateryId
            )
        }
    }
}

internal data class Element(
    var offset: IntOffset,
    var size: IntSize,
    var contents: @Composable () -> Unit
)
