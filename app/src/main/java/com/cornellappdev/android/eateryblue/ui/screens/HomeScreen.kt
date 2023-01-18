package com.cornellappdev.android.eateryblue.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.ui.components.general.*
import com.cornellappdev.android.eateryblue.ui.components.home.MainLoadingItem
import com.cornellappdev.android.eateryblue.ui.components.home.MainLoadingItem.Companion.CreateMainLoadingItem
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.viewmodels.HomeViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryRetrievalState
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class,
)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    showBottomBar: MutableState<Boolean>,
    onSearchClick: () -> Unit,
    onEateryClick: (eatery: Eatery) -> Unit
) {
    val selectedPaymentMethodFilters = remember { mutableStateListOf<Filter>() }
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    val shimmer = rememberShimmer(ShimmerBounds.View)

    // Refreshes the favorites when this view appears everytime.
    LaunchedEffect(Unit) {
        homeViewModel.updateFavorites()
    }

    // Here a DisposableEffect is launched when the bottom sheet opens. 
    // When it disappears it's from the view hierarchy, which will cause
    // onDispose to be called, adding/resetting the payment filters.
    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                // Handles the case where filters reset as well (by adding an empty list).
                homeViewModel.addPaymentMethodFilters(selectedPaymentMethodFilters)
            }
        }
    }

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
                PaymentMethodsBottomSheet(
                    selectedFilters = selectedPaymentMethodFilters,
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

                // Whole page is meant to be scrollable, hence the use of a LazyColumn here
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
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
                                            text = "Eatery",
                                            color = Color.White,
                                            style = TextStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 20.sp
                                            )
                                        )

                                        IconButton(
                                            modifier = Modifier.align(Alignment.CenterEnd),
                                            onClick = {
                                                onSearchClick()
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Search,
                                                contentDescription = Icons.Default.Search.name,
                                                tint = Color.White
                                            )
                                        }
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 24.dp
                                        )
                                    ) {
                                        AnimatedVisibility(visible = homeViewModel.eateryRetrievalState is EateryRetrievalState.Success) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_eaterylogo),
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                        Text(
                                            text = "Eatery",
                                            color = Color.White,
                                            style = EateryBlueTypography.h2
                                        )
                                    }
                                }
                            }
                        }
                    }

                    when (homeViewModel.eateryRetrievalState) {
                        is EateryRetrievalState.Pending -> {
                            items(MainLoadingItem.mainItems) { item ->
                                CreateMainLoadingItem(item, shimmer)
                            }
                        }
                        is EateryRetrievalState.Error -> {
                            // TODO Add No Internet/Oopsie display
                        }
                        is EateryRetrievalState.Success -> {
                            item {
                                SearchBar(
                                    searchText = "",
                                    onSearchTextChange = { },
                                    placeholderText = "Search for grub...",
                                    modifier = Modifier
                                        .padding(vertical = 12.dp, horizontal = 16.dp)
                                        .clickable {
                                            onSearchClick()
                                        },
                                    onCancelClicked = {},
                                    enabled = false
                                )

                                FilterRow(
                                    modifier = Modifier.padding(start = 16.dp),
                                    currentFiltersSelected = homeViewModel.currentFiltersSelected,
                                    onPaymentMethodsClicked = {
                                        coroutineScope.launch {
                                            modalBottomSheetState.show()
                                        }
                                    },
                                    onFilterClicked = { filter ->
                                        if (homeViewModel.currentFiltersSelected.contains(filter)) {
                                            homeViewModel.removeFilter(filter)
                                        } else {
                                            homeViewModel.addFilter(filter)
                                        }
                                    })
                            }

                            if (homeViewModel.currentFiltersSelected.isNotEmpty()) {
                                if (homeViewModel.filteredResults.isNotEmpty()) {
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
                                            homeViewModel.filteredResults.forEach { eatery ->
                                                EateryCard(
                                                    eatery = eatery,
                                                    isFavorite = homeViewModel.favoriteEateries.any { favoriteEatery ->
                                                        favoriteEatery.id == eatery.id
                                                    },
                                                    onFavoriteClick = {
                                                        if (it) {
                                                            homeViewModel.addFavorite(eatery.id)
                                                        } else {
                                                            homeViewModel.removeFavorite(eatery.id)
                                                        }
                                                    }) {
                                                    onEateryClick(it)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    item {
                                        // TODO test if this is relatively centered, occurs when filtering results in no eateries
                                        Box(modifier = Modifier.fillParentMaxHeight(0.7f)) {
                                            NoEateryFound(modifier = Modifier.align(Alignment.Center)) {
                                                homeViewModel.resetFilters()
                                            }
                                        }
                                    }
                                }
                            } else {
                                // TODO Regular home screen here
                            }
                        }
                    }
                }
            })

        if (FirstTimeShown.firstTimeShown) {
            PermissionRequestDialog(
                showBottomBar = showBottomBar,
                notificationFlowStatus = homeViewModel.getNotificationFlowCompleted(),
                updateNotificationFlowStatus = {
                    homeViewModel.setNotificationFlowCompleted(it)
                }
            )
        }
    }
}

/**
 * Keeps track of when app navigates away from HomeScreen so PermissionRequestDialog
 * only occurs when the app FIRST is navigated to the HomeScreen.
 */
object FirstTimeShown {
    var firstTimeShown = true
}
