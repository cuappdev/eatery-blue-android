package com.cornellappdev.android.eateryblue.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.viewmodels.UpcomingViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryRetrievalState
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun UpcomingMenuScreen(
    upcomingViewModel: UpcomingViewModel = hiltViewModel(),
    showBottomBar: MutableState<Boolean>,
    onEateryClick: (eatery: Eatery) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    /** instantiates the meal filter list*/

    val selectedMealFilters = remember { mutableStateListOf<Filter>() }
    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                // Handles the case where filters reset as well (by adding an empty list).
                upcomingViewModel.addMealFilters(selectedMealFilters)
            }
        }
    }
    /** Handles the number and calender at the top*/
    val currentDay = LocalDate.now()
    val dayWeek: Int = currentDay.dayOfWeek.value
    val dayNum: Int = currentDay.dayOfMonth
    var days = mutableListOf<Int>()
    var dayNames = mutableListOf<String>("Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat")
    for (i in dayWeek downTo 1) {
        days.add(currentDay.minusDays(i.toLong()).dayOfMonth)
    }
    days.add(dayNum)
    for (i in 1 until 7 - dayWeek) {
        days.add(currentDay.plusDays(i.toLong()).dayOfMonth)
    }

    var selectedDay by remember { mutableStateOf(dayWeek) }


    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val isFirstVisible =
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
                val listState = rememberLazyListState()
                val isFirstVisible =
                    remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
                LazyColumn(
                    state = listState, modifier = Modifier.fillMaxSize()
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
                                            color = Color.White,
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
                                            color = Color.White,
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
                            currentFiltersSelected = upcomingViewModel.currentFiltersSelected,
                            onMealsClicked = {
                                coroutineScope.launch {
                                    modalBottomSheetState.show()
                                }
                            },
                            onFilterClicked = { filter ->
                                if (upcomingViewModel.currentFiltersSelected.contains(filter)) {
                                    upcomingViewModel.removeFilter(filter)
                                } else {
                                    upcomingViewModel.addFilter(filter)
                                }
                            })
                    }
                    when (upcomingViewModel.eateryRetrievalState) {
                        is EateryRetrievalState.Pending -> {
                            items(UpcomingLoadingItem.upcomingItems) { item ->
                                UpcomingLoadingItem.CreateUpcomingLoadingItem(item, shimmer)
                            }
                        }
                        is EateryRetrievalState.Error -> {
                            item { Text(text = "error") }
                        }
                        is EateryRetrievalState.Success -> {
                            if (upcomingViewModel.filteredResults.isNotEmpty()) {
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
                                        upcomingViewModel.filteredResults.forEach { eatery ->
                                            MenuCard(
                                                eatery = eatery
                                            ) {
                                                onEateryClick(it)
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
                                        NoEateryFound(modifier = Modifier.align(Alignment.Center)) {
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