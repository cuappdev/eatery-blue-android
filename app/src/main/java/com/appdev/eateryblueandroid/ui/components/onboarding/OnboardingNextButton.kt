package com.appdev.eateryblueandroid.ui.components.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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

    // Next Button
    Button(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            .height(48.dp)
            .graphicsLayer {
                val pageOffset = 1f - pagerOffset.coerceIn(-1f, 1f).absoluteValue
                alpha = pageOffset.pow(3)
            },
        onClick = {
            when (num) {
                0 -> coroutineScope.launch {
                    pagerState.animateScrollToPage(1)
                }

                1 -> coroutineScope.launch {
                    pagerState.animateScrollToPage(2)
                }

                2 -> coroutineScope.launch {
                    pagerState.animateScrollToPage(3)
                }

            }
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.gray00)),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
            hoveredElevation = 0.dp,
            focusedElevation = 0.dp
        )
    ) {
        Text(
            textStyle = TextStyle.HEADER_H4,
            text = "Next"
        )
    }
}