package com.appdev.eateryblueandroid.ui.components.sharedelements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset

enum class SharedElementType {FROM, TO}
internal typealias ElementTag = Any

@Composable
fun SharedElement(
    tag: ElementTag,
    type: SharedElementType,
    eateryId: Int,
    children: @Composable () -> Unit
) {
    val rootState = LocalState.current

    DisposableEffect(tag) {
        onDispose {
            if (type == SharedElementType.TO) rootState.hideEateryDetail()
        }
    }
    Box(
        modifier = Modifier.onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot()
            val offset = IntOffset(
                position.x.toInt(),
                position.y.toInt()
            )
            rootState.register(
                tag,
                eateryId,
                type,
                offset,
                coordinates.size,
                children
            )
        }.then(
            if(type == SharedElementType.TO)
                Modifier.alpha(1f)
            else Modifier
        )
    ) {
        children()
    }
}