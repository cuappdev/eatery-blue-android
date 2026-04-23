package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.ui.components.general.CalendarWeekSelector
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterButton
import com.cornellappdev.android.eatery.ui.components.general.FilterRow
import com.cornellappdev.android.eatery.ui.components.general.MealFilter
import com.cornellappdev.android.eatery.ui.components.general.NoEateryFound
import com.cornellappdev.android.eatery.ui.components.upcoming.MealBottomSheet
import com.cornellappdev.android.eatery.ui.components.upcoming.MenuCard
import com.cornellappdev.android.eatery.ui.components.upcoming.UpcomingLoadingItem
import com.cornellappdev.android.eatery.ui.components.upcoming.UpcomingLoadingItem.Companion.CreateUpcomingLoadingItem
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.UpcomingMenusViewState
import com.cornellappdev.android.eatery.ui.viewmodels.UpcomingViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.util.AppStorePopupRepository
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.PreviewData
import com.cornellappdev.android.eatery.util.appStorePopupRepository
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)

@Composable
fun UpcomingMenuScreen(
    upcomingViewModel: UpcomingViewModel = hiltViewModel(),
    appStorePopupRepository: AppStorePopupRepository = appStorePopupRepository(),
    onEateryClick: (Int) -> Unit,
) {
    val viewState = upcomingViewModel.viewStateFlow.collectAsStateWithLifecycle().value

    UpcomingMenuScreenContent(
        viewState = viewState,
        upcomingMenuFilters = upcomingViewModel.upcomingMenuFilters,
        onMealFilterChanged = upcomingViewModel::onMealFilterChanged,
        onToggleFilterClicked = upcomingViewModel::onToggleFilterClicked,
        onResetFiltersClicked = upcomingViewModel::onResetFiltersClicked,
        onSelectDayOffset = upcomingViewModel::selectDayOffset,
        onPingEateries = upcomingViewModel::pingEateries,
        onEateryClick = onEateryClick,
        onEateryCardContract = { appStorePopupRepository.requestRatingPopup() },
    )
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
private fun UpcomingMenuScreenContent(
    viewState: UpcomingMenusViewState,
    upcomingMenuFilters: List<Filter>,
    onMealFilterChanged: (MealFilter) -> Unit,
    onToggleFilterClicked: (Filter) -> Unit,
    onResetFiltersClicked: () -> Unit,
    onSelectDayOffset: (Int) -> Unit,
    onPingEateries: () -> Unit,
    onEateryClick: (Int) -> Unit,
    onEateryCardContract: () -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showMealBottomSheet by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val shimmer = rememberShimmer(ShimmerBounds.View)

    Box(modifier = Modifier.background(currentColors.backgroundDefault)) {
        if (showMealBottomSheet) {
            ModalBottomSheet(
                sheetState = modalBottomSheetState,
                containerColor = currentColors.accentPrimary,
                contentColor = currentColors.textPrimary,
                onDismissRequest = { showMealBottomSheet = false },
                shape = RoundedCornerShape(
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp,
                    topStart = 12.dp,
                    topEnd = 12.dp
                ),
            ) {
                MealBottomSheet(
                    isVisible = modalBottomSheetState.isVisible,
                    selectedMeal = viewState.mealFilter,
                    onSubmit = onMealFilterChanged,
                    hide = {
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) showMealBottomSheet = false
                        }
                    }
                )
            }
        }

        val innerListState = rememberLazyListState()
        val filterRowState = rememberLazyListState()
        val isFirstVisible =
            remember { derivedStateOf { innerListState.firstVisibleItemIndex > 0 } }
        when (val menus = viewState.menus) {
            is EateryApiResponse.Success -> {
                UpcomingMenuShell(
                    innerListState = innerListState,
                    isFirstVisible = isFirstVisible,
                    selectedDay = viewState.selectedDay,
                    selectDayOffset = onSelectDayOffset,
                    showModalBottomSheet = { showMealBottomSheet = true },
                    mealFilter = viewState.mealFilter,
                    upcomingMenuFilters = upcomingMenuFilters,
                    selectedFilters = viewState.selectedFilters,
                    onToggleFilterClicked = onToggleFilterClicked,
                    filterRowState = filterRowState
                ) {
                    if (menus.data.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxHeight(0.7f)
                                    .fillMaxWidth()
                            ) {
                                NoEateryFound(
                                    modifier = Modifier.align(
                                        Alignment.Center
                                    ),
                                    resetFilters = onResetFiltersClicked
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    items(menus.data) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                            Text(
                                modifier = Modifier.padding(start = 6.dp),
                                text = it.header,
                                style = EateryBlueTypography.h4,
                                color = currentColors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            it.menuCards.forEach { eatery ->
                                MenuCard(
                                    menuCardViewState = eatery,
                                    selectEatery = {
                                        onEateryClick(eatery.eateryId)
                                    },
                                    onEateryCardContract = onEateryCardContract
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }

            is EateryApiResponse.Pending -> {
                UpcomingMenuShell(
                    innerListState = innerListState,
                    isFirstVisible = isFirstVisible,
                    selectedDay = viewState.selectedDay,
                    selectDayOffset = onSelectDayOffset,
                    showModalBottomSheet = { showMealBottomSheet = true },
                    mealFilter = viewState.mealFilter,
                    upcomingMenuFilters = upcomingMenuFilters,
                    selectedFilters = viewState.selectedFilters,
                    onToggleFilterClicked = onToggleFilterClicked,
                    filterRowState = filterRowState
                ) {
                    items(UpcomingLoadingItem.upcomingItems) { item ->
                        CreateUpcomingLoadingItem(
                            item,
                            shimmer
                        )
                    }
                }
            }

            is EateryApiResponse.Error -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    UpcomingMenuHeader(isFirstVisible)
                    CalendarWeekSelector(
                        selectedDay = viewState.selectedDay,
                        selectDayOffset = onSelectDayOffset
                    )
                    UpcomingFilterRow(
                        showModalBottomSheet = { showMealBottomSheet = true },
                        mealFilter = viewState.mealFilter,
                        upcomingMenuFilters = upcomingMenuFilters,
                        selectedFilters = viewState.selectedFilters,
                        onToggleFilterClicked = onToggleFilterClicked,
                        filterRowState = filterRowState
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorContent(onTryAgain = onPingEateries)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UpcomingMenuScreenPreview() = EateryPreview {
    val previewState = PreviewData.upcomingMenuPreviewState()
    UpcomingMenuScreenContent(
        viewState = previewState.viewState,
        upcomingMenuFilters = previewState.upcomingMenuFilters,
        onMealFilterChanged = {},
        onToggleFilterClicked = {},
        onResetFiltersClicked = {},
        onSelectDayOffset = {},
        onPingEateries = {},
        onEateryClick = {},
        onEateryCardContract = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun UpcomingMenuScreenEmptyPreview() = EateryPreview {
    val previewState = PreviewData.upcomingMenuEmptyPreviewState()
    UpcomingMenuScreenContent(
        viewState = previewState.viewState,
        upcomingMenuFilters = previewState.upcomingMenuFilters,
        onMealFilterChanged = {},
        onToggleFilterClicked = {},
        onResetFiltersClicked = {},
        onSelectDayOffset = {},
        onPingEateries = {},
        onEateryClick = {},
        onEateryCardContract = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun UpcomingMenuScreenErrorPreview() = EateryPreview {
    val previewState = PreviewData.upcomingMenuErrorPreviewState()
    UpcomingMenuScreenContent(
        viewState = previewState.viewState,
        upcomingMenuFilters = previewState.upcomingMenuFilters,
        onMealFilterChanged = {},
        onToggleFilterClicked = {},
        onResetFiltersClicked = {},
        onSelectDayOffset = {},
        onPingEateries = {},
        onEateryClick = {},
        onEateryCardContract = {}
    )
}


@Composable
private fun UpcomingMenuShell(
    innerListState: LazyListState,
    isFirstVisible: State<Boolean>,
    selectedDay: Int,
    selectDayOffset: (Int) -> Unit,
    showModalBottomSheet: () -> Unit,
    mealFilter: MealFilter,
    upcomingMenuFilters: List<Filter>,
    selectedFilters: List<Filter>,
    onToggleFilterClicked: (Filter) -> Unit,
    filterRowState: LazyListState,
    content: LazyListScope.() -> Unit,
) {
    UpcomingLazyColumn(
        innerListState = innerListState,
        upcomingMenuHeader = { UpcomingMenuHeader(isFirstVisible) },
        calendarWeekSelector = {
            CalendarWeekSelector(
                selectedDay = selectedDay,
                selectDayOffset = selectDayOffset
            )
        },
        filterRow = {
            UpcomingFilterRow(
                showModalBottomSheet = showModalBottomSheet,
                mealFilter = mealFilter,
                upcomingMenuFilters = upcomingMenuFilters,
                selectedFilters = selectedFilters,
                onToggleFilterClicked = onToggleFilterClicked,
                filterRowState = filterRowState
            )
        },
        content = content
    )
}

@Composable
private fun CalendarWeekSelector(
    selectedDay: Int,
    selectDayOffset: (Int) -> Unit,
) {
    Box(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        CalendarWeekSelector(
            dayNames = (0 until 7).map {
                LocalDate.now().plusDays(it.toLong())
                    .format(DateTimeFormatter.ofPattern("EEE"))
            },
            currSelectedDay = selectedDay,
            selectedDay = selectedDay,
            days = (0 until 7).map {
                LocalDate.now().plusDays(it.toLong()).dayOfMonth
            },
            onClick = selectDayOffset,
            closedDays = null
        )
    }
}

@Composable
private fun UpcomingFilterRow(
    showModalBottomSheet: () -> Unit,
    mealFilter: MealFilter,
    upcomingMenuFilters: List<Filter>,
    selectedFilters: List<Filter>,
    onToggleFilterClicked: (Filter) -> Unit,
    filterRowState: LazyListState,
) {
    FilterRow(
        customItemsBefore = {
            item {
                FilterButton(
                    onFilterClicked = {
                        showModalBottomSheet()
                    },
                    selected = true,
                    text = mealFilter.displayName,
                    hasExpandIcon = true,
                )
            }
        },
        filters = upcomingMenuFilters,
        currentFiltersSelected = selectedFilters,
        onFilterClicked = onToggleFilterClicked,
        rowState = filterRowState
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UpcomingLazyColumn(
    innerListState: LazyListState,
    upcomingMenuHeader: @Composable () -> Unit,
    calendarWeekSelector: @Composable () -> Unit,
    filterRow: @Composable () -> Unit,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        state = innerListState, modifier = Modifier.fillMaxSize()
    ) {
        stickyHeader {
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(currentColors.backgroundDefault)
            ) {
                upcomingMenuHeader()
            }
        }
        item {
            calendarWeekSelector()
        }
        item {
            filterRow()
        }
        content()
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun UpcomingMenuHeader(isFirstVisible: State<Boolean>) {
    val colors = currentColors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.backgroundSecondary)
            .then(Modifier.statusBarsPadding())
            .padding(bottom = 7.dp),
    ) {
        AnimatedContent(
            targetState = isFirstVisible.value
        ) { isFirstVisible ->
            if (isFirstVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = "Upcoming Menus",
                        color = currentColors.oppTextPrimary,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 56.dp
                    )
                ) {
                    Text(
                        text = "Upcoming Menus",
                        color = currentColors.oppTextPrimary,
                        style = EateryBlueTypography.h2
                    )
                }
            }
        }
    }
}
