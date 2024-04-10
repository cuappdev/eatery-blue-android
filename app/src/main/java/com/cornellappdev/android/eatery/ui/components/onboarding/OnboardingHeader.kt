package com.cornellappdev.android.eatery.ui.components.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GraySix
import kotlin.math.absoluteValue
import kotlin.math.pow

@Composable
fun OnboardingHeader(
    num: Int,
    pagerOffset: Float,
    onSkipClicked: () -> Unit
) {
    // Top Bar & Text
    Column(
        modifier = Modifier
            .then(Modifier.statusBarsPadding())
            .graphicsLayer {
                val pageOffset = 1f - pagerOffset.coerceIn(-1f, 1f).absoluteValue
                alpha = pageOffset.pow(3)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = when (num) {
                    0 -> "Home"
                    1 -> "Home"
                    2 -> "Upcoming Menus"
                    3 -> "Upcoming Menus"
                    4 -> "Favorites"
                    5 -> "Favorites"
                    else -> "Log in with Eatery"
                },
                style = EateryBlueTypography.h3,
                color = EateryBlue,
                modifier = Modifier.padding(start = 16.dp)
            )

            if (num == 6)
                TextButton(
                    onClick = {
                        onSkipClicked.invoke()
                    },
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .align(Alignment.Bottom)
                ) {
                    Text(
                        text = "Skip",
                        modifier = Modifier.offset(x = 0.dp, y = (-7).dp),
                        color = Color.Black,
                        style = EateryBlueTypography.h6,
                    )
                }
        }

        Text(
            text = when (num) {
                0 -> "View the eateries Cornell offers"
                1 -> "View the eateries Cornell offers"
                2 -> "See menus by date and plan ahead"
                3 -> "See menus by date and plan ahead"
                4 -> "Save and quickly find eateries"
                5 -> "Save and quickly find eateries"
                else -> "See your meal swipes, BRBs, and more"
            },
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = GraySix,
            modifier = Modifier.padding(top = 7.dp, start = 16.dp)
        )
    }
}
