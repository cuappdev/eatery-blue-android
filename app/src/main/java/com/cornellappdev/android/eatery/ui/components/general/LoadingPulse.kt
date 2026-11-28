package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

/** Fades the content between full and half opacity, for skeleton loading placeholders. */
@Composable
fun Modifier.loadingPulse(): Modifier {
    val alpha by rememberInfiniteTransition(label = "Loading pulse").animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse alpha"
    )
    return graphicsLayer { this.alpha = alpha }
}

@DualModePreview
@Composable
private fun LoadingPulsePreview() = EateryPreview {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .loadingPulse()
            .fillMaxWidth()
            .height(34.dp)
            .background(currentColors.accentPrimary, CircleShape)
    )
}
