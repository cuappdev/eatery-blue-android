package com.cornellappdev.android.eatery.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.details.EateryHourBottomSheet
import com.cornellappdev.android.eatery.ui.components.general.SearchBar
import com.cornellappdev.android.eatery.ui.components.home.BottomSheetContent
import com.cornellappdev.android.eatery.ui.components.home.MainLoadingItem
import com.cornellappdev.android.eatery.ui.components.settings.Issue
import com.cornellappdev.android.eatery.ui.components.settings.ReportBottomSheet
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.theme.Green
import com.cornellappdev.android.eatery.ui.theme.Red
import com.cornellappdev.android.eatery.ui.theme.Yellow
import com.cornellappdev.android.eatery.ui.viewmodels.CompareMenusViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun CompareMenusScreen(
    eateryIds: List<Int>,
    compareMenusViewModel: CompareMenusViewModel = hiltViewModel(),
    onEateryClick: (eatery: Eatery) -> Unit,
) {
    //todo i think this is bad practice? check how to do this via saved state handles
    compareMenusViewModel.openEatery(eateryIds)

    val eateriesApiResponse by compareMenusViewModel.eateryFlow.collectAsState()
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    val coroutineScope = rememberCoroutineScope()

    var sheetContent by remember { mutableStateOf(BottomSheetContent.HOURS) }

    val issue by remember { mutableStateOf<Issue?>(null) }

    Column {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Compare Menus",
                fontSize = 20.sp,
                style = EateryBlueTypography.h5,
                fontWeight = FontWeight(600)
            )
        }
        Divider(
            color = GrayZero,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )
        when (eateriesApiResponse) {
            is EateryApiResponse.Success -> {
                val eateries = (eateriesApiResponse as EateryApiResponse.Success<List<Eatery>>).data
                val firstPagerState = rememberPagerState()
                val secondPagerState = rememberPagerState()

                val scrollingFollowingPair by remember {
                    derivedStateOf {
                        if (firstPagerState.isScrollInProgress) {
                            firstPagerState to secondPagerState
                        } else if (secondPagerState.isScrollInProgress) {
                            secondPagerState to firstPagerState
                        } else null
                    }
                }
                LaunchedEffect(scrollingFollowingPair) {
                    val (scrollingState, followingState) = scrollingFollowingPair
                        ?: return@LaunchedEffect
                    snapshotFlow { scrollingState.currentPage + scrollingState.currentPageOffsetFraction }
                        .collect { pagePart ->
                            val divideAndRemainder = BigDecimal.valueOf(pagePart.toDouble())
                                .divideAndRemainder(BigDecimal.ONE)
                            val pageOffsetFraction =
                                if (divideAndRemainder[1].toFloat() > 0.5f) 0.5f else Math.max(
                                    -0.5f,
                                    divideAndRemainder[1].toFloat()
                                )
                            followingState.scrollToPage(
                                divideAndRemainder[0].toInt(),
                                pageOffsetFraction,
                            )
                        }
                }
                ModalBottomSheetLayout(
                    sheetState = modalBottomSheetState, sheetContent = {
                        when (sheetContent) {
                            BottomSheetContent.HOURS -> EateryHourBottomSheet(onDismiss = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }, eatery = eateries[firstPagerState.currentPage], onReportIssue = {
                                sheetContent = BottomSheetContent.REPORT
                            })

                            BottomSheetContent.REPORT -> {
                                eateries[0].id?.let {
                                    ReportBottomSheet(issue = issue,
                                        eateryid = it,
                                        sendReport = { issue, report, eateryid ->
                                            compareMenusViewModel.sendReport(
                                                issue,
                                                report,
                                                eateryid
                                            )
                                        }) {
                                        coroutineScope.launch {
                                            modalBottomSheetState.hide()
                                        }
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                ) {
                    Column {
                        HorizontalPager(
                            pageCount = eateries.size,
                            state = firstPagerState,
                            modifier = Modifier.fillMaxHeight(0.92f),
                            flingBehavior = PagerDefaults.flingBehavior(
                                state = firstPagerState,
                                pagerSnapDistance = PagerSnapDistance.atMost(1)
                            )
                        ) { page ->
                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier
                                        .height(IntrinsicSize.Min)
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                                        .border(
                                            1.dp, GrayZero, RoundedCornerShape(8.dp)
                                        ),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .padding(vertical = 12.dp)
                                            .weight(1f, true)
                                            .clickable {
                                                sheetContent = BottomSheetContent.HOURS
                                                coroutineScope.launch {
                                                    modalBottomSheetState.show()
                                                }
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Schedule,
                                                contentDescription = "Hours Icon",
                                                tint = GrayFive
                                            )
                                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                            Text(
                                                text = "Hours", style = TextStyle(
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 16.sp
                                                ), color = GrayFive
                                            )
                                        }
                                        val openUntil = eateries[page].getOpenUntil()
                                        Text(
                                            modifier = Modifier.padding(top = 2.dp),
                                            text =
                                            if (openUntil == null) "Closed"
                                            else if (eateries[page].isClosingSoon()) "Closing at $openUntil"
                                            else ("Open until $openUntil"),
                                            style = TextStyle(
                                                fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                                            ),
                                            color = if (openUntil == null) Red
                                            else if (eateries[page].isClosingSoon()) Yellow
                                            else Green
                                        )
                                    }
                                }
                                val currentEvent by remember { mutableStateOf(eateries[page].getCurrentDisplayedEvent()) }
                                if (currentEvent != null) {
                                    Box(
                                        modifier = Modifier
                                            .background(GrayZero)
                                            .padding(start = 8.dp, end = 8.dp, top = 20.dp)
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                    ) {
                                        LazyColumn {
                                            currentEvent!!.menu?.forEach { category ->
                                                item {
                                                    Column(modifier = Modifier.background(Color.White)) {
                                                        Text(
                                                            text = category.category ?: "Category",
                                                            style = EateryBlueTypography.h5,
                                                            modifier = Modifier.padding(
                                                                horizontal = 16.dp,
                                                                vertical = 12.dp
                                                            )
                                                        )
                                                        category.items?.forEach { menuItem ->
                                                            Column {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    modifier = Modifier.padding(
                                                                        top = 12.dp,
                                                                        bottom = 12.dp,
                                                                        start = 16.dp,
                                                                        end = 16.dp
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        text = menuItem.name
                                                                            ?: "Item Name",
                                                                        style = EateryBlueTypography.button,
                                                                        modifier = Modifier.weight(
                                                                            1f
                                                                        )
                                                                    )
                                                                }
                                                                if (menuItem != category.items.last()) {
                                                                    Spacer(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .height(1.dp)
                                                                            .background(
                                                                                GrayZero,
                                                                                CircleShape
                                                                            )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        Divider(
                                                            color = GrayZero,
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(10.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            if (currentEvent!!.menu == null || currentEvent!!.menu?.isEmpty() == true) {
                                                item {
                                                    Row(
                                                        modifier = Modifier
                                                            .background(Color.White)
                                                            .clip(shape = RoundedCornerShape(12.dp))
                                                            .fillMaxWidth()
                                                            .padding(top = 12.dp, bottom = 12.dp),
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = "Sorry, there is no menu available now.",
                                                            color = Color.Black,
                                                            style = EateryBlueTypography.h5,
                                                            modifier = Modifier.padding(start = 8.dp),
                                                            fontWeight = FontWeight(500),
                                                        )
                                                    }
                                                    Divider(
                                                        color = GrayZero,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(10.dp)
                                                    )
                                                }
                                            }
                                            item {

                                            }
                                            item {
                                                Column(modifier = Modifier.background(Color.White)) {
                                                    Card(
                                                        shape = RoundedCornerShape(20.dp),
                                                        onClick = {
                                                            onEateryClick(eateries[page])
                                                        },
                                                        backgroundColor = GrayZero,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(top = 8.dp, bottom = 8.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(
                                                                end = 12.dp,
                                                                top = 10.dp,
                                                                bottom = 10.dp
                                                            ),
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            Icon(
                                                                painter = painterResource(R.drawable.ic_eatery),
                                                                contentDescription = null,
                                                                tint = Color.Black
                                                            )
                                                            Text(
                                                                text = "View Eatery Details",
                                                                color = Color.Black,
                                                                modifier = Modifier.padding(start = 8.dp),
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        HorizontalPager(
                            pageCount = eateries.size,
                            state = secondPagerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(GrayZero),
                            contentPadding = PaddingValues(horizontal = 100.dp),
                            pageSpacing = 2.dp,
                            verticalAlignment = Alignment.CenterVertically,
                            flingBehavior = PagerDefaults.flingBehavior(
                                state = secondPagerState,
                                //pager snap distance literally does nothing
                                pagerSnapDistance = PagerSnapDistance.atMost(1),
                            ),
                        ) { page ->
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clip(shape = RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .fillMaxSize(0.9f)
                                    .wrapContentSize(Alignment.Center)

                            ) {
                                eateries[page].name?.let {
                                    Text(
                                        text = it,
                                        style = EateryBlueTypography.button
                                    )
                                }
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }
}
