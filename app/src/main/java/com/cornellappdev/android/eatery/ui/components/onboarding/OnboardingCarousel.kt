package com.cornellappdev.android.eatery.ui.components.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingCarousel(
    fadePage: Boolean,
    onSkipClicked: () -> Unit,
    onProceed: () -> Unit = {}
) {
    val headerPagerState = rememberPagerState(pageCount = { 6 })
    val phonePagerState = rememberPagerState(pageCount = { 6 })
    val iconPagerState = rememberPagerState(pageCount = { 6 })
    val currentPageOffsetFraction by remember {
        derivedStateOf {
            phonePagerState.currentPageOffsetFraction
        }
    }

    // Takes in the phone's pager position and properly moves the header
    // along with it every time the state changes.
    LaunchedEffect(Pair(phonePagerState.currentPage, currentPageOffsetFraction)) {
        headerPagerState.scrollToPage(
            phonePagerState.currentPage,
            currentPageOffsetFraction,
        )

        iconPagerState.scrollToPage(
            phonePagerState.currentPage,
            currentPageOffsetFraction
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
                .fillMaxHeight()
                .systemBarsPadding(),
            verticalArrangement = Arrangement.Top
        ) {
            HorizontalPager(
                state = headerPagerState, userScrollEnabled = false
            ) { page ->
                OnboardingHeader(
                    num = page,
                    pagerOffset = pagerOffsetForPage(headerPagerState, page),
                    onSkipClicked = onSkipClicked
                )
            }

            Box {
                HorizontalPager(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .zIndex(1f),
                    state = phonePagerState,
                    contentPadding = PaddingValues(horizontal = 55.dp)
                ) { page ->
                    when (page) {
                        0 -> {
                            OnboardingPage(
                                num = 0,
                                pagerOffset = pagerOffsetForPage(phonePagerState, 0),
                            )
                        }

                        1 -> {
                            OnboardingPage(
                                num = 1,
                                pagerOffset = pagerOffsetForPage(phonePagerState, 1),
                            )
                        }

                        2 -> {
                            OnboardingPage(
                                num = 2,
                                pagerOffset = pagerOffsetForPage(phonePagerState, 2),
                            )
                        }

                        3 -> {
                            OnboardingPage(
                                num = 3,
                                pagerOffset = pagerOffsetForPage(phonePagerState, 3),
                            )
                        }

                        4 -> {
                            OnboardingPage(
                                num = 4,
                                pagerOffset = pagerOffsetForPage(phonePagerState, 4),
                            )
                        }

                        5 -> {
                            Box {
                                OnboardingPage(
                                    num = 5,
                                    pagerOffset = pagerOffsetForPage(phonePagerState, 5),
                                )
                                //Proceed to Eatery Button
                                Button(
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = .1f.dp,
                                            end = .1f.dp,
                                            bottom = 32.dp,
                                            top = 24.dp
                                        )
                                        .height(48.dp)
                                        .align(Alignment.BottomCenter),
                                    onClick = {
                                        onProceed()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp,
                                        pressedElevation = 4.dp,
                                        disabledElevation = 4.dp,
                                        hoveredElevation = 4.dp
                                    )
                                ) {
                                    Text(
                                        style = EateryBlueTypography.h6,
                                        text = "Proceed to Eatery!",
                                        color = Black
                                    )
                                }
                            }
                        }
                    }
                }

                // Need a separate HorizontalPager. We don't want the IconSheet + LoginPage
                // to be affected by the contentPadding of the HorizontalPager above.
                HorizontalPager(
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
                                pagerOffset = pagerOffsetForPage(iconPagerState, page)
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
                                pagerOffset = pagerOffsetForPage(iconPagerState, page)
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
                                pagerOffset = pagerOffsetForPage(iconPagerState, page)
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
                                pagerOffset = pagerOffsetForPage(iconPagerState, page)
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
                                pagerOffset = pagerOffsetForPage(iconPagerState, page)
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
                                pagerOffset = pagerOffsetForPage(iconPagerState, page)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun pagerOffsetForPage(state: PagerState, page: Int): Float {
    return (state.currentPage - page) + state.currentPageOffsetFraction
}

@DualModePreview
@Composable
private fun OnboardingCarouselPreview() = EateryPreview {
    OnboardingCarousel(
        fadePage = true,
        onSkipClicked = {},
        onProceed = {}
    )
}
