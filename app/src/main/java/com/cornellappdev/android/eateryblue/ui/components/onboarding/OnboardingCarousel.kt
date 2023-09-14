package com.cornellappdev.android.eateryblue.ui.components.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.User
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
fun OnboardingCarousel(
    fadePage: Boolean,
    onSkipClicked: () -> Unit,
    onLoginSuccess: (User) -> Unit,
    onLoginError: () -> Unit = {},
    onProceed: () -> Unit = {}
) {
    val headerPagerState = rememberPagerState()
    val phonePagerState = rememberPagerState()
    val iconPagerState = rememberPagerState()

    val navController = rememberAnimatedNavController()

    // Takes in the phone's pager position and properly moves the header
    // along with it every time the state changes.
    LaunchedEffect(Pair(phonePagerState.currentPage, phonePagerState.currentPageOffset)) {
        headerPagerState.scrollToPage(
            phonePagerState.currentPage,
            phonePagerState.currentPageOffset,
        )

        iconPagerState.scrollToPage(
            phonePagerState.currentPage,
            phonePagerState.currentPageOffset
        )
    }

    AnimatedVisibility(
        visible = fadePage,
        enter = fadeIn(
            initialAlpha = 0f,
            animationSpec = tween(durationMillis = 2500)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 2500)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top
        ) {

            HorizontalPager(
                count = 6, state = headerPagerState, userScrollEnabled = false
            ) { page ->
                OnboardingHeader(
                    num = page,
                    pagerOffset = calculateCurrentOffsetForPage(page),
                    onSkipClicked = onSkipClicked
                )
            }

            Box {

                HorizontalPager(
                    count = 6,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 0.dp, end = 0.dp)
                        .zIndex(1f),
                    state = phonePagerState,
                    contentPadding = PaddingValues(horizontal = 55.dp)
                ) { page ->
                    when (page) {
                        0 -> {
                            OnboardingPage(
                                num = 0,
                                pagerOffset = calculateCurrentOffsetForPage(0),
                            )
                        }
                        1 -> {
                            OnboardingPage(
                                num = 1,
                                pagerOffset = calculateCurrentOffsetForPage(1),
                            )
                        }

                        2 -> {
                            OnboardingPage(
                                num = 2,
                                pagerOffset = calculateCurrentOffsetForPage(2),
                            )
                        }
                        3 -> {
                            OnboardingPage(
                                num = 3,
                                pagerOffset = calculateCurrentOffsetForPage(3),
                            )
                        }
                        4 -> {
                            OnboardingPage(
                                num = 4,
                                pagerOffset = calculateCurrentOffsetForPage(4),
                            )
                        }
                        5 -> {
                            OnboardingPage(
                                num = 5,
                                pagerOffset = calculateCurrentOffsetForPage(5),
                            )
                            //Proceed to Eatery Button
                            Button(
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 48.dp,
                                        end = 48.dp,
                                        bottom = 32.dp,
                                        top = 24.dp
                                    )
                                    .height(48.dp)
                                    .align(Alignment.BottomCenter),
                                onClick = {
                                    onProceed()
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
                                    text = "Proceed to Eatery!"
                                )
                            }
                        }
                        // Leave this empty. The sixth page is filled by the
                        // HorizontalPager below.
//                        6 -> {
//
//                        }
                    }
                }

                // Need a separate HorizontalPager. We don't want the IconSheet + LoginPage
                // to be affected by the contentPadding of the HorizontalPager above.
                HorizontalPager(
                    count = 6,
                    state = iconPagerState,
                    userScrollEnabled = false,
                    modifier = Modifier.zIndex(if (iconPagerState.currentPage == 6) 2f else -1f)
                ) { page ->
                    when (page) {
                        0 -> {
                            IconSheet(
                                iconData = listOf(
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_place),
                                        side = Side.LEFT,
                                        rotate = -12f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_item),
                                        side = Side.RIGHT,
                                        rotate = 0f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_brbs),
                                        side = Side.LEFT,
                                        rotate = -24f
                                    )
                                ),
                                pagerOffset = calculateCurrentOffsetForPage(page)
                            )
                        }
                        1 -> {
                            IconSheet(
                                iconData = listOf(
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_place),
                                        side = Side.LEFT,
                                        rotate = -12f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_item),
                                        side = Side.RIGHT,
                                        rotate = 0f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_brbs),
                                        side = Side.LEFT,
                                        rotate = -24f
                                    )
                                ),
                                pagerOffset = calculateCurrentOffsetForPage(page)
                            )
                        }
                        2 -> {
                            IconSheet(
                                iconData = listOf(
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_place),
                                        side = Side.LEFT,
                                        rotate = -12f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_item),
                                        side = Side.RIGHT,
                                        rotate = 0f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_brbs),
                                        side = Side.LEFT,
                                        rotate = -24f
                                    )
                                ),
                                pagerOffset = calculateCurrentOffsetForPage(page)
                            )
                        }
                        3 -> {
                            IconSheet(
                                iconData = listOf(
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_place),
                                        side = Side.LEFT,
                                        rotate = -12f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_item),
                                        side = Side.RIGHT,
                                        rotate = 0f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_brbs),
                                        side = Side.LEFT,
                                        rotate = -24f
                                    )
                                ),
                                pagerOffset = calculateCurrentOffsetForPage(page)
                            )
                        }
                        4 -> {
                            IconSheet(
                                iconData = listOf(
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_selected_off),
                                        side = Side.RIGHT,
                                        rotate = -12f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_bell),
                                        side = Side.LEFT,
                                        rotate = 0f
                                    )
                                ),
                                pagerOffset = calculateCurrentOffsetForPage(page)
                            )
                        }
                        5 -> {
                            IconSheet(
                                iconData = listOf(
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_selected_off),
                                        side = Side.RIGHT,
                                        rotate = -12f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_bell),
                                        side = Side.LEFT,
                                        rotate = 0f
                                    )
                                ),
                                pagerOffset = calculateCurrentOffsetForPage(page)
                            )
                        }
//                        6 -> {
//                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
//                                LoginPage(onSuccess = onLoginSuccess, onError = onLoginError)
//                            }
//                        }
                    }
                }
            }
        }
    }
}
