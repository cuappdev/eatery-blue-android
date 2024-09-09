package com.cornellappdev.android.eatery.ui.screens

import android.Manifest
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.general.EateryCard
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterRow
import com.cornellappdev.android.eatery.ui.components.general.NoEateryFound
import com.cornellappdev.android.eatery.ui.components.general.PaymentMethodsBottomSheet
import com.cornellappdev.android.eatery.ui.components.general.PermissionRequestDialog
import com.cornellappdev.android.eatery.ui.components.general.SearchBar
import com.cornellappdev.android.eatery.ui.components.home.EateryHomeSection
import com.cornellappdev.android.eatery.ui.components.home.MainLoadingItem
import com.cornellappdev.android.eatery.ui.components.home.MainLoadingItem.Companion.CreateMainLoadingItem
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.viewmodels.HomeViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.util.LocationHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class,
)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    showBottomBar: MutableState<Boolean>,
    onSearchClick: () -> Unit,
    onEateryClick: (eatery: Eatery) -> Unit,
    onFavoriteExpand: () -> Unit,
    onNearestExpand: () -> Unit
) {
    val context = LocalContext.current
    val onFavoriteClick: (Eatery, Boolean) -> Unit = { eatery, favorite ->
        if (favorite) {
            homeViewModel.addFavorite(eatery.id)
        } else {
            homeViewModel.removeFavorite(eatery.id)
        }
    }

    val notificationPermissionState =
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

    LaunchedEffect(notificationPermissionState.allPermissionsGranted) {
        LocationHandler.instantiate(context)
    }

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

    // Code-ugly workaround to make favorites disappearing animation look good.
    // lastFavorite will, whenever the favorites is deleted, persist the last favorite for a bit.
    var lastFavorite: Eatery? by remember { mutableStateOf(null) }
    if (favorites.isNotEmpty()) {
        lastFavorite = favorites[0]
    }
    // A sneaky 6 dp tween.
    val tweenHeight by animateFloatAsState(
        targetValue = if (favorites.isEmpty()) 6f else 0f,
        animationSpec = tween(600, delayMillis = 350),
        label = "Sneaky tween"
    )

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

    Box(modifier = Modifier.background(Color.White)) {
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
                                // Eateries found; display new screen. O/W, reset filter screen.
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
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // If should show "fake" last favorite persisting for a bit.
                                    val showFake = favorites.isEmpty() && lastFavorite != null

                                    EateryHomeSection(
                                        title = "Favorites",
                                        eateries = favorites,
                                        overflowEatery = if (showFake) lastFavorite else null,
                                        onEateryClick = onEateryClick,
                                        onFavoriteClick = onFavoriteClick,
                                        onExpandClick = onFavoriteExpand,
                                        favoritesDecider = { true }
                                    )
                                }

                                item {
                                    // Sneaky spacer to make padding work right for favorites.
                                    Spacer(modifier = Modifier.height(tweenHeight.dp))

                                    EateryHomeSection(
                                        title = "Nearest to You",
                                        eateries = nearestEateries,
                                        onEateryClick = onEateryClick,
                                        onFavoriteClick = onFavoriteClick,
                                        onExpandClick = onNearestExpand,
                                        favoritesDecider = { favorites.contains(it) }
                                    )
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
