package com.appdev.eateryblueandroid.ui.components.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.screens.IconData
import kotlin.math.absoluteValue

@Composable
fun OnboardingPage(
    num: Int,
    pagerOffset: Float,
    icons: List<IconData> = listOf()
) {
    val phoneY: Dp = LocalConfiguration.current.screenHeightDp.dp - 204.dp
    val phoneX = (.493f * phoneY.value).dp

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            icons.forEach {
                Icon(
                    painter = it.painter,
                    contentDescription = null,
                    modifier = Modifier
                        .offset(phoneX * it.offsetX, phoneY * it.offsetY)
                        .width(96.dp)
                        .height(96.dp)
                        .align(Alignment.Center)
                        .rotate(it.rotate)
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
                    tint = colorResource(R.color.blue_light)
                )
            }
            // Phone
            Row(
                modifier = Modifier
                    // This is bad but is the only way I could get this to be sized correctly.
                    .height(phoneY)
                    .graphicsLayer {
                        val pageOffset =
                            if (num < 2) 0f else -pagerOffset.coerceIn(0f, 1f)

                        val lerp = { startValue: Float, endValue: Float, fraction: Float ->
                            startValue + (fraction * (endValue - startValue))
                        }

                        scaleX = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue / 10f
                        scaleY = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue / 10f

                        val offsetLerp = lerp(
                            0f,
                            240f,
                            pageOffset
                        )

                        translationX = offsetLerp
                    },
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(
                        id = when (num) {
                            0 -> R.drawable.active_mock_0_4x
                            1 -> R.drawable.active_mock_1_4x
                            else -> R.drawable.active_mock_2_4x
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

            }
        }
    }
}