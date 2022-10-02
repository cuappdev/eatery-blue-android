package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
    var stage: Int by remember {
        mutableStateOf(0)
    }

    val goBackToMain = {
        stage = 0
    }

    val alpha = animateFloatAsState(
        targetValue = if (stage == 0) 1f else 0f
    )

    Box {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

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
            Button(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, end = 48.dp, bottom = 32.dp, top = 24.dp)
                    .height(48.dp),
                onClick = { stage = 1 },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.white)),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 4.dp,
                    disabledElevation = 4.dp,
                    hoveredElevation = 4.dp,
                    focusedElevation = 4.dp
                )
            ) {
                Text(
                    textStyle = TextStyle.HEADER_H4,
                    text = "Get Started"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 46.dp),
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

        if (stage == 1)
            OnboardingViewPager(
                modifier = Modifier.alpha(1 - alpha.value),
                profileViewModel = profileViewModel,
                goBackToMain = goBackToMain
            )
    }
}

data class IconData(
    val painter: Painter,
    val offsetX: Float,
    val offsetY: Float,
    val rotate: Float
)
