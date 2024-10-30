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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.ui.components.general.CalendarWeekSelector
import com.cornellappdev.android.eatery.ui.components.general.FilterRowUpcoming
import com.cornellappdev.android.eatery.ui.components.upcoming.MealBottomSheet
import com.cornellappdev.android.eatery.ui.components.upcoming.MenuCard
import com.cornellappdev.android.eatery.ui.components.upcoming.UpcomingLoadingItem
import com.cornellappdev.android.eatery.ui.components.upcoming.UpcomingLoadingItem.Companion.CreateUpcomingLoadingItem
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.viewmodels.UpcomingViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)

@Composable
fun UpcomingMenuScreen(
    upcomingViewModel: UpcomingViewModel = hiltViewModel(),
    onEateryClick: (eateryId: Int) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipHalfExpanded = true,
        initialValue = ModalBottomSheetValue.Hidden
    )

    val viewState = upcomingViewModel.viewStateFlow.collectAsState().value
    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                // Handles the case where filters reset as well (by adding an empty list).
            }
        }
    }

    /** Handles the number and calender at the top*/
    val zoneId: ZoneId? = ZoneId.of("America/New_York")
    val today = LocalDate.now(zoneId)
    val currentDay by remember { mutableStateOf(today) }
    val dayWeek: Int = currentDay.dayOfWeek.value
    val dayNum: Int = currentDay.dayOfMonth
    val dayNames = mutableListOf<String>()
    val dayWeeks = mutableListOf<Int>()
    val days = mutableListOf<Int>()


    dayWeeks.add(dayWeek)
    days.add(dayNum)
    for (i in 1 until 7) {
        dayWeeks.add(currentDay.plusDays(i.toLong()).dayOfWeek.value)
        days.add(currentDay.plusDays(i.toLong()).dayOfMonth)
    }

    dayWeeks.forEach {
        var dayName = ""
        when (it) {
            1 -> dayName = "Mon"
            2 -> dayName = "Tue"
            3 -> dayName = "Wed"
            4 -> dayName = "Thu"
            5 -> dayName = "Fri"
            6 -> dayName = "Sat"
            7 -> dayName = "Sun"

        }
        dayNames.add(dayName)
    }

    val coroutineScope = rememberCoroutineScope()

    var selectedDay by remember { mutableStateOf(0) }

    val listState = rememberLazyListState()
    remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val shimmer = rememberShimmer(ShimmerBounds.View)

    Box(modifier = Modifier.background(White)) {
        ModalBottomSheetLayout(
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(
                bottomStart = 0.dp,
                bottomEnd = 0.dp,
                topStart = 12.dp,
                topEnd = 12.dp
            ),
            sheetElevation = 8.dp,
            sheetContent = {
                MealBottomSheet(
                    sheetState = modalBottomSheetState,
                    selectedMeal = viewState.mealFilter,
                    onSubmit = {
                        upcomingViewModel.changeMealFilter(it)
                    },
                    onReset = {
                        upcomingViewModel.resetMealFilter()
                    },
                    hide = {
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                        }
                    }
                )
            },
            content = { ->
                val innerListState = rememberLazyListState()
                val isFirstVisible =
                    remember { derivedStateOf { innerListState.firstVisibleItemIndex > 0 } }
                LazyColumn(
                    state = innerListState, modifier = Modifier.fillMaxSize()
                ) {

                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(EateryBlue)
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
                                            color = White,
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
                                            color = White,
                                            style = EateryBlueTypography.h2
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        val weekDayIndex = 0
                        var today by remember { mutableStateOf(weekDayIndex) }
                        Box(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
                        ) {
                            CalendarWeekSelector(
                                dayNames = dayNames,
                                currSelectedDay = selectedDay,
                                selectedDay = today,
                                days = days,
                                onClick = { i -> selectedDay = i },
                                closedDays = null
                            )
                        }
                    }
                    item {
                        FilterRowUpcoming(
                            modifier = Modifier.padding(start = 16.dp),
                            mealFilter = viewState.mealFilter,
                            selectedFilters = viewState.selectedFilters,
                            onMealsClicked = {
                                coroutineScope.launch {
                                    modalBottomSheetState.show()
                                }
                            },
                            onFilterClicked = { filter ->
                                if (viewState.selectedFilters.contains(filter)
                                ) {
                                    upcomingViewModel.removeLocationFilter(filter)
                                } else {
                                    upcomingViewModel.addLocationFilter(filter)
                                }
                            })
                    }
                    when (val menus = viewState.menus) {
                        is EateryApiResponse.Pending -> {
                            items(UpcomingLoadingItem.upcomingItems) { item ->
                                CreateUpcomingLoadingItem(
                                    item,
                                    shimmer
                                )
                            }
                        }

                        is EateryApiResponse.Error -> {
                            item { Text(text = "error") }
                        }

                        is EateryApiResponse.Success -> {
                            items(menus.data) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                                    Text(
                                        modifier = Modifier.padding(start = 6.dp),
                                        text = it.header,
                                        style = EateryBlueTypography.h4
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    it.menuCards.forEach { eatery ->
                                        MenuCard(
                                            eatery
                                        ) {
                                            onEateryClick(eatery.eateryId)
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }

                            }
                        }
                    }
                }
            }
        )
    }
}
