package com.appdev.eateryblueandroid.ui.components.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.util.ColorType
import com.appdev.eateryblueandroid.util.Constants
import com.appdev.eateryblueandroid.util.OnboardingRepository
import com.appdev.eateryblueandroid.util.overrideStatusBarColor
import kotlin.math.absoluteValue
import kotlin.math.pow

@Composable
fun OnboardingHeader(
    num: Int,
    pagerOffset: Float
) {
    // Top Bar & Text
    Column(
        modifier = Modifier
            .graphicsLayer {
                val pageOffset = 1f - pagerOffset.coerceIn(-1f, 1f).absoluteValue
                alpha = pageOffset.pow(3)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 21.dp, bottom = 12.dp)
                .height(14.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (num == 3)
                Text(
                    text = "Skip",
                    color = colorResource(R.color.black),
                    textStyle = TextStyle.HEADER_H4,
                    modifier = Modifier
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            OnboardingRepository.saveOnboardingInfo(true)
                            overrideStatusBarColor(Constants.eateryBlueColor, ColorType.INTERP)
                        }
                        .height(14.dp)
                )
        }
        Text(
            text = when (num) {
                0 -> "Upcoming Menus"
                1 -> "Favorites"
                2 -> "Wait Times"
                else -> "Log in with Eatery"
            },
            textStyle = TextStyle.HEADER_H2,
            color = colorResource(id = R.color.eateryBlue),
            modifier = Modifier.padding(start = 16.dp)
        )
        Text(
            text = when (num) {
                0 -> "See menus by date and plan ahead"
                1 -> "Save and quickly find eateries and items"
                2 -> "Check for crowds in real time to avoid lines"
                else -> "See your meal swipes, BRBs, and more"
            },
            textStyle = TextStyle.APPDEV_BODY_MEDIUM,
            color = colorResource(id = R.color.gray06),
            modifier = Modifier.padding(top = 7.dp, start = 16.dp)
        )
    }
}