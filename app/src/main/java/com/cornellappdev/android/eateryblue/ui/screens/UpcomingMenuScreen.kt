package com.cornellappdev.android.eateryblue.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.ui.components.general.Filter
import com.cornellappdev.android.eateryblue.ui.components.general.FilterRowUpcoming
import com.cornellappdev.android.eateryblue.ui.components.general.NoEateryFound
import com.cornellappdev.android.eateryblue.ui.components.upcoming.MealBottomSheet
import com.cornellappdev.android.eateryblue.ui.components.upcoming.MenuCard
import com.cornellappdev.android.eateryblue.ui.components.upcoming.UpcomingLoadingItem
import com.cornellappdev.android.eateryblue.ui.components.upcoming.UpcomingLoadingItem.Companion.CreateUpcomingLoadingItem
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.viewmodels.UpcomingViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
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
    onEateryClick: (eatery: Eatery) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val filters = upcomingViewModel.filtersFlow.collectAsState().value
    val eateriesApiResponse = upcomingViewModel.eateryFlow.collectAsState().value

    /** instantiates the meal filter list*/

    val selectedMealFilters = remember { mutableStateListOf<Filter>() }
    if (selectedMealFilters.isEmpty()) selectedMealFilters.add(Filter.BREAKFAST)
    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                // Handles the case where filters reset as well (by adding an empty list).
                upcomingViewModel.addMealFilters(selectedMealFilters)
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
    Log.d("list for cal", dayWeeks.toList().toString())
    dayWeeks.forEach {
        var dayName = ""
        when (it) {
            1 -> dayName = "Mon"
            2 -> dayName = "Tues"
            3 -> dayName = "Wed"
            4 -> dayName = "Thurs"
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
    Box {
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
                    selectedFilters = selectedMealFilters,
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (i in 0..6) {
                                Box(modifier = Modifier.width(40.dp)) {
                                    Text(
                                        text = dayNames[i],
                                        color = GrayFive,
                                        textAlign = TextAlign.Center,
                                        style = EateryBlueTypography.caption,
                                        modifier = Modifier.align(Alignment.TopCenter)
                                    )
                                    if (i == selectedDay) {
                                        Canvas(
                                            modifier = Modifier
                                                .size(size = 35.dp)
                                                .align(Alignment.BottomCenter)
                                        ) {
                                            drawCircle(
                                                color = GrayFive,
                                            )
                                        }
                                    }
                                    TextButton(onClick = {
                                        selectedDay = i
                                    }) {
                                        Text(
                                            text = days[i].toString(),
                                            color = if (i != selectedDay) Black else White,
                                            style = EateryBlueTypography.h6,
                                            fontWeight = FontWeight.Normal,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 30.dp)
                                        )
                                    }


                                }
                            }
                        }
                    }
                    item {
                        FilterRowUpcoming(
                            modifier = Modifier.padding(start = 16.dp),
                            currentFiltersSelected = filters,
                            onMealsClicked = {
                                coroutineScope.launch {
                                    modalBottomSheetState.show()
                                }
                            },
                            onFilterClicked = { filter ->
                                if (filters.contains(
                                        filter
                                    )
                                ) {
                                    upcomingViewModel.removeFilter(filter)
                                } else {
                                    upcomingViewModel.addFilter(filter)
                                }
                            })
                    }
                    when (eateriesApiResponse) {
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
                            val eateries = eateriesApiResponse.data
                            if (filters.isNotEmpty()) {
                                if (eateries.isNotEmpty()) {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .wrapContentHeight()
                                                .padding(
                                                    start = 16.dp,
                                                    end = 16.dp,
                                                    top = 12.dp,
                                                    bottom = 12.dp
                                                ),
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            val northEateries =
                                                eateries.filter { it.campusArea == "North" }
                                            val westEateries =
                                                eateries.filter { it.campusArea == "West" }
                                            val centralEateries =
                                                eateries.filter { it.campusArea == "Central" }
                                            if (northEateries.isNotEmpty()) {
                                                Text(
                                                    modifier = Modifier.padding(start = 6.dp),
                                                    text = "North",
                                                    style = EateryBlueTypography.h4
                                                )
                                                northEateries.forEach { eatery ->
                                                    MenuCard(
                                                        eatery = eatery,
                                                        day = selectedDay,
                                                        meal = selectedMealFilters
                                                    ) {
                                                        onEateryClick(it)
                                                    }
                                                }

                                            }
                                            if (centralEateries.isNotEmpty()) {
                                                Text(
                                                    modifier = Modifier.padding(start = 6.dp),
                                                    text = "Central",
                                                    style = EateryBlueTypography.h4
                                                )
                                                centralEateries.forEach { eatery ->
                                                    MenuCard(
                                                        eatery = eatery,
                                                        day = selectedDay,
                                                        meal = selectedMealFilters
                                                    ) {
                                                        onEateryClick(it)
                                                    }
                                                }

                                            }
                                            if (westEateries.isNotEmpty()) {
                                                Text(
                                                    modifier = Modifier.padding(start = 6.dp),
                                                    text = "West",
                                                    style = EateryBlueTypography.h4
                                                )
                                                westEateries.forEach { eatery ->
                                                    MenuCard(
                                                        eatery = eatery,
                                                        day = selectedDay,
                                                        meal = selectedMealFilters
                                                    ) {
                                                        onEateryClick(it)
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            } else {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillParentMaxHeight(0.7f)
                                            .fillMaxWidth()
                                    ) {
                                        NoEateryFound(
                                            modifier = Modifier.align(
                                                Alignment.Center
                                            )
                                        ) {
                                            upcomingViewModel.resetFilters()
                                        }
                                    }
                                }
                            }
                        }

                    }

                }
            })
    }
}
