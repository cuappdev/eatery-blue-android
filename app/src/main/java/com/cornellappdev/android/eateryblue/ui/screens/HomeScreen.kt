package com.cornellappdev.android.eateryblue.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.cornellappdev.android.eateryblue.ui.components.general.EateryCard
import com.cornellappdev.android.eateryblue.ui.components.general.Filter
import com.cornellappdev.android.eateryblue.ui.components.general.FilterRow
import com.cornellappdev.android.eateryblue.ui.components.general.NoEateryFound
import com.cornellappdev.android.eateryblue.ui.components.general.PaymentMethodsBottomSheet
import com.cornellappdev.android.eateryblue.ui.components.general.PermissionRequestDialog
import com.cornellappdev.android.eateryblue.ui.components.general.SearchBar
import com.cornellappdev.android.eateryblue.ui.components.home.MainLoadingItem
import com.cornellappdev.android.eateryblue.ui.components.home.MainLoadingItem.Companion.CreateMainLoadingItem
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.viewmodels.HomeViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eateryblue.util.popIn
import com.cornellappdev.android.eateryblue.util.popOut
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
    onEateryClick: (eatery: Eatery) -> Unit,
    onFavoriteClick: () -> Unit
) {
    val selectedPaymentMethodFilters = remember { mutableStateListOf<Filter>() }
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    val shimmer = rememberShimmer(ShimmerBounds.View)

    val eateriesApiResponse = homeViewModel.eateryFlow.collectAsState().value
    val filters = homeViewModel.filtersFlow.collectAsState().value
    val nearestEateries = homeViewModel.nearestEateries.collectAsState().value
    val favorites = homeViewModel.favoriteEateries.collectAsState().value

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

    // This is the announcements framework. Replace image or popup with necessary materials. Do NOT delete unused imports.
//    Box(
//        modifier = Modifier
//            .fillMaxWidth(1f)
//            .background(Color.Transparent, RoundedCornerShape(20.dp))
//    ) {
//        if (!homeViewModel.bigPopUp) {
//            Popup(alignment = Alignment.BottomEnd) {
//
//                Box(
//                    Modifier
//                        .padding(16.dp)
//                        .width(50.dp)
//                        .height(50.dp)
//                        .background(Color.White, RoundedCornerShape(10.dp))
//                        .clip(RoundedCornerShape(10.dp))
//                        .clickable { homeViewModel.setPopUp(true) }
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_appdev),
//                        tint = Color.Red,
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .alpha(.6f),
//                        contentDescription = "popup logo"
//                    )
//                }
//            }
//        } else {
//            Popup(alignment = Alignment.Center, properties = PopupProperties(focusable = true)) {
//                Box(
//                    Modifier
//                        .fillMaxWidth(.8f)
//                        .fillMaxHeight(.4f)
//                        .background(Color.White, RoundedCornerShape(20.dp))
//                        .clip(RoundedCornerShape(20.dp))
//                        .focusable(true)
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.recruitment_popup_2),
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxSize(),
//                        contentDescription = null
//                    )
//                    IconButton(
//                        onClick = {
//                            homeViewModel.setPopUp(false)
//                        },
//                        modifier = Modifier
//                            .size(40.dp)
//                            .background(color = Color.Transparent, shape = CircleShape)
//                            .align(Alignment.TopEnd)
//                            .alpha(.4f)
//                    ) {
//                        Icon(
//                            Icons.Default.Close,
//                            contentDescription = Icons.Default.Close.name,
//                            Modifier
//                                .size(30.dp)
//                                .background(Color.White, CircleShape)
//                                .clip(CircleShape)
//                        )
//                    }
//                }
//            }
//        }


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

                // Whole page is meant to be scrollable, hence the use of a LazyColumn here.
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
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
                                        AnimatedVisibility(
                                            visible = eateriesApiResponse is EateryApiResponse.Success
                                        ) {
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

                    when (eateriesApiResponse) {
                        is EateryApiResponse.Pending -> {
                            items(MainLoadingItem.mainItems) { item ->
                                CreateMainLoadingItem(item, shimmer)
                            }
                        }

                        is EateryApiResponse.Error -> {
                            // TODO Add No Internet/Oopsie display
                        }

                        is EateryApiResponse.Success -> {
                            val eateries = eateriesApiResponse.data

                            item {
                                SearchBar(
                                    searchText = "",
                                    onSearchTextChange = { },
                                    placeholderText = "Search for grub...",
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 12.dp, bottom = 6.dp)
                                        .clickable {
                                            onSearchClick()
                                        },
                                    onCancelClicked = {},
                                    enabled = false
                                )

                                FilterRow(
                                    currentFiltersSelected = filters,
                                    onPaymentMethodsClicked = {
                                        coroutineScope.launch {
                                            modalBottomSheetState.show()
                                        }
                                    },
                                    onFilterClicked = { filter ->
                                        if (filters.contains(filter)) {
                                            homeViewModel.removeFilter(filter)
                                        } else {
                                            homeViewModel.addFilter(filter)
                                        }
                                    })
                            }

                            if (filters.isNotEmpty()) {
                                if (eateries.isNotEmpty()) {
                                    items(eateries) { eatery ->
                                        Box(
                                            Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 12.dp
                                            )
                                        ) {
                                            EateryCard(
                                                eatery = eatery,
                                                isFavorite = favorites.any { favoriteEatery ->
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
                                } else {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillParentMaxHeight(0.7f)
                                                .fillMaxWidth()
                                        ) {
                                            NoEateryFound(modifier = Modifier.align(Alignment.Center)) {
                                                homeViewModel.resetFilters()
                                            }
                                        }
                                    }
                                }
                            } else {
                                item {
                                    AnimatedVisibility(
                                        visible = favorites.isNotEmpty(),
                                        enter = popIn(),
                                        exit = popOut()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(
                                                bottom = 12.dp,
                                                top = 12.dp
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        start = 16.dp,
                                                        bottom = 17.dp,
                                                        end = 16.dp
                                                    ),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Favorite Eateries",
                                                    style = EateryBlueTypography.h4,
                                                )

                                                IconButton(
                                                    onClick = {
                                                        onFavoriteClick()
                                                    },
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .background(
                                                            color = GrayZero,
                                                            shape = CircleShape
                                                        )
                                                ) {
                                                    Icon(
                                                        Icons.Default.ArrowForward,
                                                        contentDescription = "Favorites",
                                                        tint = Color.Black
                                                    )
                                                }
                                            }

                                            LazyRow(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                item {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                }
                                                items(
                                                    favorites,
                                                    key = { eatery -> eatery.hashCode() }) { eatery ->
                                                    EateryCard(
                                                        eatery = eatery,
                                                        isFavorite = true,
                                                        modifier = Modifier
                                                            .fillParentMaxWidth(
                                                                0.85f
                                                            )
                                                            .animateItemPlacement(),
                                                        onFavoriteClick = {
                                                            if (!it) {
                                                                homeViewModel.removeFavorite(
                                                                    eatery.id
                                                                )
                                                            }
                                                        }) {
                                                        onEateryClick(it)
                                                    }
                                                }

                                                item {
                                                    Spacer(Modifier.width(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Column(
                                        modifier = Modifier.padding(
                                            bottom = 24.dp,
                                            top = 12.dp
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    start = 16.dp,
                                                    bottom = 17.dp,
                                                    end = 16.dp
                                                ),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(
                                                text = "Nearest to You",
                                                style = EateryBlueTypography.h4,
                                            )
                                        }
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            item {
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            items(nearestEateries) { eatery ->
                                                EateryCard(
                                                    eatery = eatery,
                                                    isFavorite = favorites.any { favoriteEatery ->
                                                        favoriteEatery.id == eatery.id
                                                    },
                                                    modifier = Modifier.fillParentMaxWidth(0.85f),
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

                                            item {
                                                Spacer(Modifier.width(16.dp))
                                            }
                                        }
                                    }
                                }

                                item {
                                    val swipeEateries =
                                        eateries.filter { eatery ->
                                            eatery.paymentAcceptsMealSwipes == true
                                        }

                                    Column(
                                        modifier = Modifier.padding(
                                            bottom = 24.dp
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    start = 16.dp,
                                                    bottom = 17.dp,
                                                    end = 16.dp
                                                ),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(
                                                text = "Swipe for a Bite",
                                                style = EateryBlueTypography.h4,
                                            )

                                        }
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            item {
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            items(swipeEateries) { eatery ->
                                                EateryCard(
                                                    eatery = eatery,
                                                    isFavorite = favorites.any { favoriteEatery ->
                                                        favoriteEatery.id == eatery.id
                                                    },
                                                    modifier = Modifier.fillParentMaxWidth(0.85f),
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

                                            item {
                                                Spacer(Modifier.width(16.dp))
                                            }
                                        }
                                    }
                                }

                                item {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, bottom = 12.dp),
                                        text = "All Eateries",
                                        style = EateryBlueTypography.h4,
                                    )
                                }

                                itemsIndexed(
                                    eateries
                                ) { index, eatery ->
                                    Box(
                                        Modifier.padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            // Handles the padding between items
                                            top = if (index != 0) 12.dp else 0.dp
                                        )
                                    ) {
                                        EateryCard(
                                            eatery = eatery,
                                            isFavorite = favorites.any { favoriteEatery ->
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
