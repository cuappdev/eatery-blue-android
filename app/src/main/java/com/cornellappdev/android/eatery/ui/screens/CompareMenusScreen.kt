package com.cornellappdev.android.eatery.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.ui.components.details.EateryDetailsStickyHeader
import com.cornellappdev.android.eatery.ui.components.details.EateryHourBottomSheet
import com.cornellappdev.android.eatery.ui.components.home.BottomSheetContent
import com.cornellappdev.android.eatery.ui.components.settings.Issue
import com.cornellappdev.android.eatery.ui.components.settings.ReportBottomSheet
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.CompareMenusViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.ReportUiState
import com.cornellappdev.android.eatery.util.AppStorePopupRepository
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.PreviewData
import com.cornellappdev.android.eatery.util.appStorePopupRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun CompareMenusScreen(
    eateryIds: List<Int>,
    compareMenusViewModel: CompareMenusViewModel = hiltViewModel(),
    appStorePopupRepository: AppStorePopupRepository = appStorePopupRepository(),
    onEateryClick: (eatery: Eatery) -> Unit,
) {
    val eateryIdsKey = remember(eateryIds) { eateryIds.hashCode() }
    LaunchedEffect(eateryIdsKey) {
        compareMenusViewModel.openEatery(eateryIds)
    }

    val uiState by compareMenusViewModel.uiState.collectAsStateWithLifecycle()
    val reportState by compareMenusViewModel.reportState.collectAsStateWithLifecycle()

    CompareMenusScreenContent(
        eateries = uiState.eateries,
        events = uiState.events,
        reportState = reportState,
        onSendReport = compareMenusViewModel::sendReport,
        onClearReportState = compareMenusViewModel::clearReportState,
        onRequestRatingPopup = { appStorePopupRepository.requestRatingPopup() },
        onEateryClick = onEateryClick,
    )
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
private fun CompareMenusScreenContent(
    eateries: List<Eatery>,
    events: List<Event?>,
    reportState: ReportUiState,
    onSendReport: (String, String, Int?) -> Unit,
    onClearReportState: () -> Unit,
    onRequestRatingPopup: () -> Unit,
    onEateryClick: (Eatery) -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var sheetContent by remember { mutableStateOf(BottomSheetContent.HOURS) }
    val closeBottomSheet: () -> Unit = {
        coroutineScope.launch {
            modalBottomSheetState.hide()
            showBottomSheet = false
            onClearReportState()
        }
    }
    val openBottomSheet: (BottomSheetContent) -> Unit = { content ->
        sheetContent = content
        showBottomSheet = true
    }

    val issue by remember { mutableStateOf<Issue?>(null) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = currentColors.backgroundDefault)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.compare_menus_title),
                fontSize = 20.sp,
                style = EateryBlueTypography.h5,
                fontWeight = FontWeight(600),
                color = currentColors.textPrimary
            )
        }
        HorizontalDivider(
            color = currentColors.borderDefault,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        val firstPagerState = rememberPagerState(pageCount = { eateries.size })
        val secondPagerState = rememberPagerState(pageCount = { eateries.size })

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
                        if (divideAndRemainder[1].toFloat() > 0.5f) 0.5f else (-0.5f).coerceAtLeast(
                            divideAndRemainder[1].toFloat()
                        )
                    followingState.scrollToPage(
                        divideAndRemainder[0].toInt(),
                        pageOffsetFraction,
                    )
                }
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = closeBottomSheet,
                sheetState = modalBottomSheetState,
                containerColor = currentColors.backgroundDefault,
                contentColor = currentColors.textPrimary,
                shape = RoundedCornerShape(
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp,
                    topStart = 12.dp,
                    topEnd = 12.dp
                )
            ) {
                when (sheetContent) {
                    BottomSheetContent.HOURS -> {
                        val eatery = eateries.getOrNull(firstPagerState.currentPage)
                        eatery?.let {
                            EateryHourBottomSheet(
                                onDismiss = closeBottomSheet,
                                eatery = eatery,
                                onReportIssue = { sheetContent = BottomSheetContent.REPORT }
                            )
                        }
                    }

                    BottomSheetContent.REPORT -> {
                        eateries.getOrNull(firstPagerState.currentPage)?.id?.let {
                            ReportBottomSheet(
                                issue = issue,
                                eateryId = it,
                                reportState = reportState,
                                sendReport = onSendReport,
                                clearReportState = onClearReportState,
                                hide = closeBottomSheet
                            )
                        }
                    }

                    else -> {}
                }
            }
        }

        Column {
            MenuPager(
                eateries,
                firstPagerState,
                events,
                onOpenSheet = openBottomSheet,
                onRequestRatingPopup = onRequestRatingPopup,
                onEateryClick = onEateryClick
            )
            TitlePager(eateries, secondPagerState)
        }

    }


}

@DualModePreview
@Composable
private fun CompareMenusScreenPreview() = EateryPreview {
    val previewState = PreviewData.compareMenusPreviewState()

    CompareMenusScreenContent(
        eateries = previewState.eateries,
        events = previewState.events,
        reportState = ReportUiState.Idle,
        onSendReport = { _, _, _ -> },
        onClearReportState = {},
        onRequestRatingPopup = {},
        onEateryClick = {}
    )
}


@Composable
@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
private fun MenuPager(
    eateries: List<Eatery>,
    firstPagerState: PagerState,
    events: List<Event?>,
    onOpenSheet: (BottomSheetContent) -> Unit,
    onRequestRatingPopup: () -> Unit,
    onEateryClick: (eatery: Eatery) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    HorizontalPager(
        state = firstPagerState,
        modifier = Modifier.fillMaxHeight(0.92f),
        flingBehavior = PagerDefaults.flingBehavior(
            state = firstPagerState,
            pagerSnapDistance = PagerSnapDistance.atMost(1)
        )
    ) { page ->
        val listState = rememberLazyListState()
        Box {
            val currentEvent = events.getOrNull(page)
            val fullMenuList = mutableListOf<String>()
            currentEvent?.menu?.forEach { category ->
                category.name?.let { fullMenuList.add(it) }
                category.items?.forEach { item ->
                    item.name?.let { fullMenuList.add(it) }
                }
            }
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 12.dp
                        )
                        .border(
                            1.dp, currentColors.borderDefault, RoundedCornerShape(8.dp)
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
                                onOpenSheet(BottomSheetContent.HOURS)
                            }
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = currentColors.textSecondary
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = stringResource(R.string.hours_title), style = TextStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ), color = currentColors.textSecondary
                            )
                        }
                        val eatery = eateries.getOrNull(page) ?: return@Column
                        val openUntil = eatery.getOpenUntil()
                        Text(
                            modifier = Modifier.padding(top = 2.dp),
                            text = when {
                                openUntil == null -> stringResource(R.string.closed)
                                eatery.isClosingSoon() -> stringResource(
                                    R.string.closing_at,
                                    openUntil
                                )

                                else -> stringResource(R.string.open_until, openUntil)
                            },
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = if (openUntil == null) currentColors.error
                            else if (eatery.isClosingSoon()) currentColors.warning
                            else currentColors.success
                        )

                    }
                }

                EateryDetailsStickyHeader(
                    currentEvent,
                    "",
                    fullMenuList,
                    listState,
                    0,
                    onRequestRatingPopup = onRequestRatingPopup,
                    onItemClick = { index ->
                        coroutineScope.launch {
                            listState.animateScrollToItem(index)
                        }
                    }
                )



                if (currentEvent != null) {
                    Box(
                        modifier = Modifier
                            .background(currentColors.accentPrimary)
                            .padding(8.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(currentColors.accentPrimary),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currentEvent.menu?.forEach { category ->
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = currentColors.backgroundDefault,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            Text(
                                                text = category.name ?: "Category",
                                                style = EateryBlueTypography.h5,
                                                modifier = Modifier.padding(
                                                    horizontal = 16.dp,
                                                    vertical = 12.dp
                                                ),
                                                color = currentColors.textPrimary
                                            )
                                            category.items?.forEachIndexed { index, menuItem ->
                                                if (index > 0) {
                                                    HorizontalDivider(
                                                        thickness = Dp.Hairline,
                                                        color = currentColors.borderDefault
                                                    )
                                                }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 12.dp
                                                    ),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = menuItem.name ?: "Item Name",
                                                        style = EateryBlueTypography.button,
                                                        modifier = Modifier.weight(1f),
                                                        color = currentColors.textPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (currentEvent.menu.isNullOrEmpty()) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = currentColors.backgroundDefault,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = stringResource(R.string.compare_menus_no_menu),
                                                color = currentColors.textPrimary,
                                                style = EateryBlueTypography.h5,
                                                modifier = Modifier.padding(start = 8.dp),
                                                fontWeight = FontWeight(500),
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Card(
                                    shape = RoundedCornerShape(100.dp),
                                    onClick = {
                                        onEateryClick(eateries[page])
                                    },
                                    colors = CardDefaults.cardColors(containerColor = currentColors.backgroundDefault),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_eatery),
                                            contentDescription = null,
                                            tint = currentColors.textPrimary
                                        )
                                        Text(
                                            text = stringResource(R.string.view_eatery_details),
                                            color = currentColors.textPrimary,
                                            modifier = Modifier.padding(start = 8.dp),
                                            fontWeight = FontWeight.Bold,
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
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun TitlePager(
    eateries: List<Eatery>,
    secondPagerState: PagerState
) {
    HorizontalPager(
        state = secondPagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(currentColors.backgroundDefault),
        contentPadding = PaddingValues(horizontal = 100.dp),
        pageSpacing = 2.dp,
        verticalAlignment = Alignment.CenterVertically,
        flingBehavior = PagerDefaults.flingBehavior(
            state = secondPagerState,
            //pager snap distance literally does nothing
            pagerSnapDistance = PagerSnapDistance.atMost(1),
        ),
    ) { page ->
        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .shadow(2.dp, shape = RoundedCornerShape(8.dp))
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(currentColors.backgroundDefault)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                eateries[page].name?.let {
                    Text(
                        text = it,
                        style = EateryBlueTypography.button,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (page == secondPagerState.currentPage) currentColors.textPrimary else currentColors.textSecondary
                    )
                }
            }
        }
    }
}
