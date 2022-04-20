package com.appdev.eateryblueandroid.ui.components.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R

@Composable
fun DropdownOption(
    title: String,
    body : @Composable () -> Unit
) {
    var dropped by remember { mutableStateOf(false) }
    SettingsOption(
        title = title,
        pointerIcon = if (dropped) painterResource(id = R.drawable.ic_chevron_down_small) else painterResource(id = R.drawable.ic_chevron_up),
        onClick = { dropped = !dropped}
    )
    if (dropped) body()
}