package com.cornellappdev.android.eateryblue.ui.components.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.ui.theme.LightBlue
import kotlin.math.absoluteValue

@Composable
fun IconSheet(
    iconData: List<IconDatum>, pagerOffset: Float,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        iconData.forEachIndexed { index, it ->
            Icon(
                painter = it.painter,
                contentDescription = null,
                modifier = Modifier
                    .width(96.dp)
                    .height(96.dp)
                    .rotate(it.rotate)
                    .align(if (it.side == Side.RIGHT) Alignment.End else Alignment.Start)

                    // This graphicsLayer modifier will shrink + grow the icons, fade the icons
                    // in and out, and counteract the horizontalPager's offset to make the icons
                    // look stationary.
                    .graphicsLayer {
                        val pageOffset = -pagerOffset.coerceIn(-1f, 1f)

                        val lerp = { startValue: Float, endValue: Float, fraction: Float ->
                            startValue + (fraction * (endValue - startValue))
                        }

                        scaleX = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue
                        scaleY = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue

                        alpha = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue

                        val offsetLerp = lerp(
                            0f,
                            240f,
                            pageOffset
                        )

                        translationX = offsetLerp
                    },
                tint = LightBlue
            )

            // Only adds spacers after the Icons up to the last Icon
            if (index != iconData.lastIndex) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

data class IconDatum(
    val painter: Painter,
    val side: Side,
    val rotate: Float
)

enum class Side {
    LEFT, RIGHT
}
