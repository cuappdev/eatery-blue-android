package com.cornellappdev.android.eateryblue.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.ui.components.general.FilterRowUpcoming
import com.cornellappdev.android.eateryblue.ui.components.general.NoEateryFound
import com.cornellappdev.android.eateryblue.ui.components.upcoming.MenuCard
import com.cornellappdev.android.eateryblue.ui.components.upcoming.UpcomingLoadingItem
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.viewmodels.UpcomingViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryRetrievalState
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val isFirstVisible =
        remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val shimmer = rememberShimmer(ShimmerBounds.View)
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
                Text(text = "Sun", style = EateryBlueTypography.caption)
                Text(text = "Mon", style = EateryBlueTypography.caption)
                Text(text = "Tues", style = EateryBlueTypography.caption)
                Text(text = "Wed", style = EateryBlueTypography.caption)
                Text(text = "Thur", style = EateryBlueTypography.caption)
                Text(text = "Fri", style = EateryBlueTypography.caption)
                Text(text = "Sat", style = EateryBlueTypography.caption)
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
                //Calender number
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
}