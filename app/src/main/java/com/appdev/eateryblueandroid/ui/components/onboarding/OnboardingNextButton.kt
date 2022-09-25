package com.appdev.eateryblueandroid.ui.components.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.pow

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingNextButton(num: Int, pagerState: PagerState, pagerOffset: Float) {
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = MutableInteractionSource()

    // Next Button
    Surface(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            .height(48.dp)
            .graphicsLayer {
                val pageOffset = 1f - pagerOffset.coerceIn(-1f, 1f).absoluteValue
                alpha = pageOffset.pow(3)
            }
    ) {
        Row(
            modifier = Modifier
                .background(colorResource(id = R.color.gray00))
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple()
                ) {
                    when (num) {
                        0 -> {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                        1 -> {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        }
                        2 -> {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(3)
                            }
                        }
                    }
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                textStyle = TextStyle.HEADER_H4,
                text = "Next"
            )
        }
    }
}