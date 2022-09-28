package com.appdev.eateryblueandroid.ui.components.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.screens.IconData
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
    profileViewModel: ProfileViewModel
) {
    val headerPagerState = rememberPagerState()
    val phonePagerState = rememberPagerState()
    val nextPagerState = rememberPagerState()

    // Takes in the phone's pager position as a flow, and properly moves the header and next button
    // along with it every time the flow is emitted / collected.
    val coroutineScopeHelper = rememberCoroutineScope()
    coroutineScopeHelper.launch {
        snapshotFlow {
            arrayOf(phonePagerState.currentPage.toFloat(), phonePagerState.currentPageOffset)
        }.collect { pageAndOffset ->
            headerPagerState.scrollToPage(
                pageAndOffset[0].toInt(),
                pageAndOffset[1],
            )
            nextPagerState.scrollToPage(
                pageAndOffset[0].toInt(),
                pageAndOffset[1],
            )
        }
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
            state = headerPagerState,
            userScrollEnabled = false
        ) { page ->
            OnboardingHeader(
                num = page,
                pagerOffset = calculateCurrentOffsetForPage(page)
            )
        }

        HorizontalPager(
            count = 4,
            modifier = Modifier
                .then(modifier)
                .padding(top = 16.dp)
                .zIndex(-1f),
            state = phonePagerState,
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
            state = nextPagerState,
            userScrollEnabled = false,
            modifier = Modifier.then(modifier)
        ) { page ->
            if (page < 3)
                OnboardingNextButton(
                    num = page,
                    pagerState = phonePagerState,
                    pagerOffset = calculateCurrentOffsetForPage(page)
                )
        }
    }
}