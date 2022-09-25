package com.appdev.eateryblueandroid.ui.components.onboarding

import android.icu.math.BigDecimal
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.screens.*
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingViewPager(
    modifier: Modifier = Modifier,
    goBackToMain: () -> Unit,
    profileViewModel: ProfileViewModel
) {
    val firstPagerState = rememberPagerState()
    val secondPagerState = rememberPagerState()
    val thirdPagerState = rememberPagerState()
    val coroutineScopeHelper = rememberCoroutineScope()

    val scrollingFollowingTriple by remember {
        derivedStateOf {
            Triple(secondPagerState, firstPagerState, thirdPagerState)
        }
    }

    Log.d("PagerState", scrollingFollowingTriple.toString())

    coroutineScopeHelper.launch {
        Log.d(
            "PagerState",
            "Init"
        )
        val (scrollingState, followingState, followingState2) = scrollingFollowingTriple
        val pagePart: State<Float> =
            mutableStateOf(scrollingState.currentPage + scrollingState.currentPageOffset)
        val divideAndRemainder: Array<BigDecimal> = arrayOf(
            BigDecimal.valueOf(pagePart.value.toDouble())
                .divide(BigDecimal.ONE),
            BigDecimal.valueOf(pagePart.value.toDouble())
                .remainder(BigDecimal.ONE)
        )

        Log.d(
            "PagerState",
            "Scrolled with " + divideAndRemainder[0] + " and " + divideAndRemainder[1]
        )

        followingState.scrollToPage(
            divideAndRemainder[0].toInt(),
            divideAndRemainder[1].toFloat(),
        )
        followingState2.scrollToPage(
            divideAndRemainder[0].toInt(),
            divideAndRemainder[1].toFloat()
        )
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .then(modifier),
        verticalArrangement = Arrangement.Top
    ) {
        HorizontalPager(
            count = 4,
            state = firstPagerState,
            userScrollEnabled = false
        ) { page ->
            OnboardingHeader(
                num = page,
                pagerState = secondPagerState,
                goBackToMain = goBackToMain,
                pagerOffset = calculateCurrentOffsetForPage(page)
            )
        }

        HorizontalPager(
            count = 4,
            modifier = Modifier
                .then(modifier)
                .padding(top = 16.dp).zIndex(-1f),
            state = secondPagerState,
            contentPadding = PaddingValues(horizontal = (50).dp)
        ) { page ->
            when (page) {
                0 -> {
                    OnboardingPage(
                        num = 0,
                        pagerOffset = calculateCurrentOffsetForPage(0),
                        icons = listOf(
                            IconData(
                                painter = painterResource(id = R.drawable.ic_place),
                                offsetX = -.6f,
                                offsetY = -.33f,
                                rotate = -12f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_brbs),
                                offsetX = -.62f,
                                offsetY = .35f,
                                rotate = -24f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_item),
                                offsetX = .6f,
                                offsetY = .1f,
                                rotate = 0f
                            )
                        )
                    )
                }
                1 -> {
                    OnboardingPage(
                        num = 1,
                        pagerOffset = calculateCurrentOffsetForPage(1),
                        icons = listOf(
                            IconData(
                                painter = painterResource(id = R.drawable.ic_selected_off),
                                offsetX = -.55f,
                                offsetY = -.35f,
                                rotate = -12f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_bell),
                                offsetX = .6f,
                                offsetY = .28f,
                                rotate = 0f
                            )
                        )
                    )
                }
                2 -> {
                    OnboardingPage(
                        num = 2,
                        pagerOffset = calculateCurrentOffsetForPage(2),
                        icons = listOf(
                            IconData(
                                painter = painterResource(id = R.drawable.ic_clock),
                                offsetX = -.52f,
                                offsetY = .2f,
                                rotate = -12f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_watch_big),
                                offsetX = .55f,
                                offsetY = -.15f,
                                rotate = 24f
                            )
                        )
                    )
                }
                3 -> {
                    LoginPage(
                        profileViewModel = profileViewModel,
                        pagerOffset = calculateCurrentOffsetForPage(3)
                    )
                }
            }
        }

        HorizontalPager(
            count = 4,
            state = thirdPagerState,
            userScrollEnabled = false,
            modifier = Modifier.then(modifier)
        ) { page ->
            if (page < 3)
                OnboardingNextButton(
                    num = page,
                    pagerState = secondPagerState,
                    pagerOffset = calculateCurrentOffsetForPage(page)
                )
        }
    }
}