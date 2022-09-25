package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.onboarding.OnboardingViewPager
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun OnboardingScreen(
    profileViewModel: ProfileViewModel
) {
    val interactionSource = MutableInteractionSource()
    var stage: Int by remember {
        mutableStateOf(0)
    }

    val goBackToMain = {
        stage = 0
    }

    val alpha = animateFloatAsState(
        targetValue = if (stage == 0 || stage == 2) 1f else 0f
    )

    Box {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .alpha(alpha.value),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(.9f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eaterylogo_blue),
                    tint = colorResource(R.color.eateryBlue),
                    modifier = Modifier
                        .width(96.dp)
                        .height(96.dp),
                    contentDescription = null
                )
                Text(
                    text = "Eatery",
                    color = colorResource(id = R.color.eateryBlue),
                    textStyle = TextStyle.SUPER_TITLE
                )

                // Get Started Button
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 48.dp, end = 48.dp),
                    elevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .background(colorResource(id = R.color.white))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple()
                            ) {
                                stage = 1

                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            textStyle = TextStyle.HEADER_H4,
                            text = "Get Started",
                            modifier = Modifier.padding(top = 13.5.dp, bottom = 13.5.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_appdev),
                    modifier = Modifier.padding(end = 6.dp),
                    contentDescription = null,
                    tint = colorResource(id = R.color.gray03)
                )
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = colorResource(R.color.gray03),
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("Cornell")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = colorResource(R.color.gray03),
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("AppDev")
                        }
                    }
                )
            }
        }

        if (stage == 1 || stage == 3)
            OnboardingViewPager(
                Modifier.alpha(1 - alpha.value),
                goBackToMain = goBackToMain,
                profileViewModel = profileViewModel
            )
    }
}

data class IconData(
    val painter: Painter,
    val offsetX: Float,
    val offsetY: Float,
    val rotate: Float
)
