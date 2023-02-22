package com.cornellappdev.android.eateryblue.ui.components.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.User
import com.cornellappdev.android.eateryblue.ui.components.login.LoginPage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingCarousel(
    fadePage: Boolean,
    onSkipClicked: () -> Unit,
    onLoginSuccess: (User) -> Unit,
    onLoginError: () -> Unit = {}
) {
    val headerPagerState = rememberPagerState()
    val phonePagerState = rememberPagerState()
    val iconPagerState = rememberPagerState()

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
                count = 4, state = headerPagerState, userScrollEnabled = false
            ) { page ->
                OnboardingHeader(
                    num = page,
                    pagerOffset = calculateCurrentOffsetForPage(page),
                    onSkipClicked = onSkipClicked
                )
            }

            Box {
                HorizontalPager(
                    count = 4,
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
                        // Leave this empty. The fourth page is filled by the
                        // HorizontalPager below.
                        3 -> {

                        }
                    }
                }

                // Need a separate HorizontalPager. We don't want the IconSheet + LoginPage
                // to be affected by the contentPadding of the HorizontalPager above.
                HorizontalPager(
                    count = 4,
                    state = iconPagerState,
                    userScrollEnabled = false,
                    modifier = Modifier.zIndex(if (iconPagerState.currentPage == 3) 2f else -1f)
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
                        2 -> {
                            IconSheet(
                                iconData = listOf(
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_clock),
                                        side = Side.LEFT,
                                        rotate = -12f
                                    ),
                                    IconDatum(
                                        painter = painterResource(id = R.drawable.ic_watch_big),
                                        side = Side.RIGHT,
                                        rotate = 24f
                                    ),
                                ),
                                pagerOffset = calculateCurrentOffsetForPage(page)
                            )
                        }
                        3 -> {
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                LoginPage(onSuccess = onLoginSuccess, onError = onLoginError)
                            }
                        }
                    }
                }
            }
        }
    }
}
