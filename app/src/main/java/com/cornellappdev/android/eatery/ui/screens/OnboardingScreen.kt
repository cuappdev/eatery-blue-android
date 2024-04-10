package com.cornellappdev.android.eatery.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.onboarding.OnboardingCarousel
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayThree
import com.cornellappdev.android.eatery.ui.viewmodels.OnboardingViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    proceedHome: () -> Unit
) {
    val pagerState = rememberPagerState(0)
    val coroutineScope = rememberCoroutineScope()
    var fadePage by rememberSaveable { mutableStateOf(false) }

    Box {
        HorizontalPager(
            count = 2, state = pagerState,
            modifier = Modifier.fillMaxSize(), userScrollEnabled = false
        ) { page ->
            when (page) {
                0 ->
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            painter = painterResource(id = R.drawable.ic_eaterylogo_blue),
                            tint = EateryBlue,
                            modifier = Modifier
                                .width(96.dp)
                                .height(96.dp),
                            contentDescription = null
                        )

                        Text(
                            text = "Eatery",
                            color = EateryBlue,
                            style = EateryBlueTypography.h1
                        )

                        // Get Started Button
                        Button(
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 48.dp, end = 48.dp, bottom = 32.dp, top = 24.dp)
                                .height(48.dp),
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(1)
                                    fadePage = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 4.dp,
                                disabledElevation = 4.dp,
                                hoveredElevation = 4.dp
                            )
                        ) {
                            Text(
                                style = EateryBlueTypography.h6,
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
                                tint = GrayThree
                            )
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = GrayThree,
                                            fontWeight = FontWeight.Normal
                                        )
                                    ) {
                                        append("Cornell")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            color = GrayThree,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    ) {
                                        append("AppDev")
                                    }
                                }
                            )
                        }
                    }

                1 -> {
                    OnboardingCarousel(
                        fadePage = fadePage,
                        onSkipClicked = {
                            onboardingViewModel.updateOnboardingCompleted()
                            proceedHome.invoke()
                        },
                        onLoginSuccess = {
                            onboardingViewModel.updateOnboardingCompleted()
                            proceedHome.invoke()
                        },
                        onProceed = {
                            onboardingViewModel.updateOnboardingCompleted()
                            proceedHome.invoke()
                        }
                    )
                }
            }
        }
    }

    BackHandler(enabled = true) {
        // Back is disabled until onboarding is done.
    }
}
