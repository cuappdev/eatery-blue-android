package com.cornellappdev.android.eateryblue.ui.components.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.cornellappdev.android.eateryblue.R
import kotlin.math.absoluteValue

@Composable
fun OnboardingPage(
    num: Int,
    pagerOffset: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    // This graphicsLayer modifier will shrink + grow the phone, and counteract
                    // the horizontalPager's offset to make the last phone look stationary.
                    .graphicsLayer {
                        val pageOffset =
                            if (num < 6) 0f else -pagerOffset.coerceIn(0f, 1f)

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
                            0 -> R.drawable.active_mock_home_dark_4x
                            1 -> R.drawable.active_mock_home_4x
                            2 -> R.drawable.active_mock_upcoming_dark_4x
                            3 -> R.drawable.active_mock_upcoming_menus_4x
                            4 -> R.drawable.active_mock_favorite_dark_4x
                            else -> R.drawable.active_mock_favorites_4x
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

            }
        }
    }
}
