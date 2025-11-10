package com.cornellappdev.android.eatery.ui.screens

import android.Manifest
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cornellappdev.android.eatery.BuildConfig
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.comparemenus.CompareMenusBotSheet
import com.cornellappdev.android.eatery.ui.components.comparemenus.CompareMenusFAB
import com.cornellappdev.android.eatery.ui.components.general.EateryCard
import com.cornellappdev.android.eatery.ui.components.general.EateryCardStyle
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterRow
import com.cornellappdev.android.eatery.ui.components.general.NoEateryFound
import com.cornellappdev.android.eatery.ui.components.general.PaymentMethodsBottomSheet
import com.cornellappdev.android.eatery.ui.components.general.PermissionRequestDialog
import com.cornellappdev.android.eatery.ui.components.general.SearchBar
import com.cornellappdev.android.eatery.ui.components.home.BottomSheetContent
import com.cornellappdev.android.eatery.ui.components.home.EateryHomeSection
import com.cornellappdev.android.eatery.ui.components.home.MainLoadingItem
import com.cornellappdev.android.eatery.ui.components.home.MainLoadingItem.Companion.CreateMainLoadingItem
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.viewmodels.HomeViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.LocationHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalPermissionsApi::class,
)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    showBottomBar: MutableState<Boolean>,
    onSearchClick: () -> Unit,
    onEateryClick: (eatery: Eatery) -> Unit,
    onFavoriteExpand: () -> Unit,
    onCompareMenusClick: (selectedEateriesIds: List<Int>) -> Unit,
    onNotificationsClick: () -> Unit
) {
    HomeScreenContent(
        favorites = homeViewModel.favoriteEateries.collectAsState().value,
        nearestEateries = homeViewModel.eateriesByDistance.collectAsState().value,
        eateriesApiResponse = homeViewModel.eateryFlow.collectAsState().value,
        selectedFilters = homeViewModel.filtersFlow.collectAsState().value,
        homeScreenFilters = homeViewModel.homeScreenFilters,
        showBottomBar = showBottomBar,
        permissionReqDialogFirstTimeShown = FirstTimeShown.firstTimeShown,
        onSearchClick = onSearchClick,
        onEateryClick = onEateryClick,
        onFavoriteExpand = onFavoriteExpand,
        onCompareMenusClick = onCompareMenusClick,
        onNotificationsClick = onNotificationsClick,
        addPaymentMethodFilters = homeViewModel::addPaymentMethodFilters,
        addFavorite = homeViewModel::addFavorite,
        removeFavorite = homeViewModel::removeFavorite,
        onToggleFilterPressed = homeViewModel::onToggleFilterPressed,
        onResetFiltersClicked = homeViewModel::onResetFiltersClicked,
        pingEateries = homeViewModel::pingEateries,
        getNotificationFlowCompleted = homeViewModel::getNotificationFlowCompleted,
        setNotificationFlowCompleted = homeViewModel::setNotificationFlowCompleted
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenContent(
    favorites: List<Eatery>,
    nearestEateries: List<Eatery>,
    eateriesApiResponse: EateryApiResponse<List<Eatery>>,
    selectedFilters: List<Filter>,
    homeScreenFilters: List<Filter>,
    showBottomBar: MutableState<Boolean>,
    permissionReqDialogFirstTimeShown: Boolean,
    onSearchClick: () -> Unit,
    onEateryClick: (eatery: Eatery) -> Unit,
    onFavoriteExpand: () -> Unit,
    onCompareMenusClick: (selectedEateriesIds: List<Int>) -> Unit,
    onNotificationsClick: () -> Unit,
    addPaymentMethodFilters: (List<Filter>) -> Unit,
    addFavorite: (Int?) -> Unit,
    removeFavorite: (Int?) -> Unit,
    onToggleFilterPressed: (Filter) -> Unit,
    onResetFiltersClicked: () -> Unit,
    pingEateries: () -> Unit,
    getNotificationFlowCompleted: () -> Boolean,
    setNotificationFlowCompleted: (Boolean) -> Unit
) {
    val context = LocalContext.current
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
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    // Here a DisposableEffect is launched when the bottom sheet opens.
    // When it disappears it's from the view hierarchy, which will cause
    // onDispose to be called, adding/resetting the payment filters.
    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                // Handles the case where filters reset as well (by adding an empty list).
                addPaymentMethodFilters(selectedPaymentMethodFilters)
            }
        }
    }

    var showFAB by remember {
        mutableStateOf(true)
    }

    val scaffoldState = rememberScaffoldState()
    var sheetContent by remember { mutableStateOf(BottomSheetContent.PAYMENT_METHODS_AVAILABLE) }

    LaunchedEffect(modalBottomSheetState.currentValue) {
        if (modalBottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
            showFAB = true
        }
    }

    val compareMenusScale by animateFloatAsState(
        targetValue = if (showFAB) 1.0f else 0.0f,
        label = "fab_scale"
    )

    //false => list view, true => grid view
    var isGridView: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            if (eateriesApiResponse is EateryApiResponse.Success && eateriesApiResponse.data.size >= 2) {
                CompareMenusFAB(
                    modifier = Modifier.scale(compareMenusScale),
                ) {
                    if (!showFAB) {
                        return@CompareMenusFAB
                    }

                    showFAB = false
                    coroutineScope.launch {
                        sheetContent = BottomSheetContent.COMPARE_MENUS
                        modalBottomSheetState.show()
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(paddingValues)
            ) {
                ModalBottomSheetLayout(
                    sheetState = modalBottomSheetState,
                    sheetShape = RoundedCornerShape(
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp,
                        topStart = 12.dp,
                        topEnd = 12.dp
                    ),
                    sheetElevation = 8.dp,
                    sheetContent = SheetContent(
                        sheetContent,
                        selectedPaymentMethodFilters,
                        coroutineScope,
                        modalBottomSheetState,
                        onCompareMenusClick
                    ),
                    content = {
                        HomeScrollableMainContent(
                            onSearchClick = onSearchClick,
                            onEateryClick = onEateryClick,
                            onFavoriteExpand = onFavoriteExpand,
                            eateriesApiResponse = eateriesApiResponse,
                            favorites = favorites,
                            nearestEateries = nearestEateries,
                            selectedFilters = selectedFilters,
                            onFavoriteClick = { eatery, favorite ->
                                if (favorite) {
                                    addFavorite(eatery.id)
                                } else {
                                    removeFavorite(eatery.id)
                                }
                            },
                            onFilterClicked = onToggleFilterPressed,
                            onResetFilters = onResetFiltersClicked,
                            filters = homeScreenFilters,
                            isGridView = isGridView,
                            onListClick = { isGridView = false },
                            onGridClick = { isGridView = true },
                            onNotificationsClick = onNotificationsClick,
                            onReload = pingEateries
                        )
                    }
                )

                if (permissionReqDialogFirstTimeShown) {
                    PermissionRequestDialog(
                        showBottomBar = showBottomBar,
                        notificationFlowStatus = getNotificationFlowCompleted(),
                        updateNotificationFlowStatus = setNotificationFlowCompleted
                    )
                }
            }
        })
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun SheetContent(
    sheetContent: BottomSheetContent,
    selectedPaymentMethodFilters: SnapshotStateList<Filter>,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    onCompareMenusClick: (List<Int>) -> Unit
): @Composable ColumnScope.() -> Unit = {
    when (sheetContent) {
        BottomSheetContent.PAYMENT_METHODS_AVAILABLE -> {
            PaymentMethodsBottomSheet(
                selectedFilters = selectedPaymentMethodFilters,
                hide = {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }
            )
        }

        BottomSheetContent.COMPARE_MENUS -> {
            CompareMenusBotSheet(
                onDismiss = {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                },
                onCompareMenusClick = { selectedEateriesIds ->
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                    onCompareMenusClick(selectedEateriesIds)
                }
            )
        }

        else -> {}
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
private fun HomeScrollableMainContent(
    onSearchClick: () -> Unit,
    onEateryClick: (eatery: Eatery) -> Unit,
    onFavoriteExpand: () -> Unit,
    onFavoriteClick: (Eatery, Boolean) -> Unit,
    onFilterClicked: (Filter) -> Unit,
    onResetFilters: () -> Unit,
    eateriesApiResponse: EateryApiResponse<List<Eatery>>,
    nearestEateries: List<Eatery>,
    favorites: List<Eatery>,
    selectedFilters: List<Filter>,
    filters: List<Filter>,
    isGridView: Boolean,
    onListClick: () -> Unit,
    onGridClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onReload: () -> Unit
) {
    val listState = rememberLazyListState()
    val filterRowState = rememberLazyListState()
    val isFirstVisible =
        remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    val shimmer = rememberShimmer(ShimmerBounds.View)

    var lastFavorite: Eatery? by remember { mutableStateOf(null) }
    if (favorites.isNotEmpty()) {
        lastFavorite = favorites[0]
    }
    when (eateriesApiResponse) {
        is EateryApiResponse.Success -> {
            HomeLazyColumn(
                listState = listState,
                collapsed = isFirstVisible.value,
                loaded = true,
                onSearchClick = onSearchClick,
                onNotificationsClick = onNotificationsClick,
                selectedFilters = selectedFilters,
                onFilterClicked = onFilterClicked,
                filters = filters,
                filterRowState = filterRowState
            ) {
                regularContent(
                    eateriesApiResponse,
                    selectedFilters,
                    favorites,
                    onFavoriteClick,
                    onEateryClick,
                    onResetFilters,
                    lastFavorite,
                    onFavoriteExpand,
                    isGridView,
                    onListClick,
                    onGridClick,
                    nearestEateries
                )
            }
        }

        is EateryApiResponse.Pending -> {
            HomeLazyColumn(
                listState = listState,
                collapsed = isFirstVisible.value,
                loaded = false,
                onSearchClick = onSearchClick,
                onNotificationsClick = onNotificationsClick,
                selectedFilters = selectedFilters,
                onFilterClicked = onFilterClicked,
                filters = filters,
                filterRowState = filterRowState
            ) {
                items(MainLoadingItem.mainItems) { item ->
                    CreateMainLoadingItem(item, shimmer)
                }
            }
        }

        is EateryApiResponse.Error -> {
            Column(modifier = Modifier.fillMaxSize()) {
                HomeStickyHeader(
                    collapsed = isFirstVisible.value,
                    loaded = false,
                    onSearchClick = onSearchClick,
                    onNotificationsClick = onNotificationsClick
                )
                HomeMainHeader(
                    onSearchClick = onSearchClick,
                    selectedFilters = selectedFilters,
                    filters = filters,
                    onFilterClicked = onFilterClicked,
                    filterRowState = filterRowState
                )
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ErrorContent(onTryAgain = onReload)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeLazyColumn(
    listState: LazyListState,
    collapsed: Boolean,
    loaded: Boolean,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    selectedFilters: List<Filter>,
    onFilterClicked: (Filter) -> Unit,
    filters: List<Filter>,
    filterRowState: LazyListState,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        stickyHeader {
            HomeStickyHeader(
                collapsed = collapsed,
                loaded = loaded,
                onSearchClick = onSearchClick,
                onNotificationsClick = onNotificationsClick
            )
        }
        item {
            HomeMainHeader(
                onSearchClick = onSearchClick,
                selectedFilters = selectedFilters,
                onFilterClicked = onFilterClicked,
                filters = filters,
                filterRowState = filterRowState
            )
        }
        content()
    }
}

@Composable
fun ErrorContent(onTryAgain: () -> Unit) {
    Column(
        modifier = Modifier
            .width(293.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = "Error Icon",
            modifier = Modifier.size(72.dp),
            tint = Color.Red
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Hmm, no chow here (yet).",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1B1F23),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "We ran into an issue loading this page. Check your connection or try reloading the page.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF1B1F23),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onTryAgain,
            modifier = Modifier
                .width(109.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(17.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = EateryBlue)
        ) {
            Text(
                text = "Try Again", color = Color.White,
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 1.25.em
            )
        }
    }
}

@Preview
@Composable
private fun PreviewErrorContent() = EateryPreview {
    ErrorContent(onTryAgain = {})
}

@OptIn(ExperimentalMaterialApi::class)
private fun LazyListScope.regularContent(
    eateriesApiResponse: EateryApiResponse.Success<List<Eatery>>,
    selectedFilters: List<Filter>,
    favorites: List<Eatery>,
    onFavoriteClick: (Eatery, Boolean) -> Unit,
    onEateryClick: (Eatery) -> Unit,
    onResetFilters: () -> Unit,
    lastFavorite: Eatery?,
    onFavoriteExpand: () -> Unit,
    isGridView: Boolean,
    onListClick: () -> Unit,
    onGridClick: () -> Unit,
    nearestEateries: List<Eatery>
) {
    val eateries = eateriesApiResponse.data

    if (selectedFilters.isNotEmpty()) {
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
                            onFavoriteClick(eatery, it)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                        onResetFilters()
                    }
                }
            }
        }
    } else {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            val showFake = favorites.isEmpty() && lastFavorite != null

            EateryHomeSection(
                title = "Favorites",
                eateries = favorites,
                overflowEatery = if (showFake) lastFavorite else null,
                onEateryClick = onEateryClick,
                onFavoriteClick = onFavoriteClick,
                onExpandClick = onFavoriteExpand,
                favoritesDecider = { !showFake }
            )
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 12.dp),
                    text = "All Eateries",
                    style = EateryBlueTypography.h4,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .padding(end = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = if (isGridView) R.drawable.ic_list_view_unselected else R.drawable.ic_list_view_selected),
                        contentDescription = "List View",
                        tint = Color.Unspecified,
                        modifier = Modifier.clickable { onListClick() }
                    )
                    Icon(
                        painter = painterResource(id = if (isGridView) R.drawable.ic_grid_view_selected else R.drawable.ic_grid_view_unselected),
                        contentDescription = "Grid View",
                        tint = Color.Unspecified,
                        modifier = Modifier.clickable { onGridClick() }
                    )
                }

            }
        }
        if (isGridView) {
            items(eateries.chunked(2)) { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    row.forEach { eatery ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 12.dp)
                        ) {
                            EateryCard(
                                eatery = eatery,
                                isFavorite = favorites.any { it.id == eatery.id },
                                onFavoriteClick = { isFavorite ->
                                    onFavoriteClick(eatery, isFavorite)
                                },
                                style = EateryCardStyle.GRID_VIEW,
                                selectEatery = { selectedEatery ->
                                    onEateryClick(selectedEatery)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                        }
                    }
                }
            }
        } else {
            itemsIndexed(nearestEateries) { index, eatery ->
                Box(
                    Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = if (index != 0) 12.dp else 0.dp
                    )
                ) {
                    EateryCard(
                        eatery = eatery,
                        isFavorite = favorites.any { favoriteEatery ->
                            favoriteEatery.id == eatery.id
                        },
                        onFavoriteClick = {
                            onFavoriteClick(eatery, it)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        onEateryClick(it)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun HomeStickyHeader(
    collapsed: Boolean,
    loaded: Boolean,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EateryBlue)
            .then(Modifier.statusBarsPadding())
            .padding(bottom = 7.dp),
    ) {
        AnimatedContent(
            targetState = collapsed
        ) { collapsed ->
            if (collapsed) {
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
                        visible = loaded,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_eaterylogo),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Eatery",
                            color = Color.White,
                            style = EateryBlueTypography.h2
                        )
                        if (BuildConfig.ENABLE_NOTIFICATIONS) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_bell),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.clickable {
                                    onNotificationsClick()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeMainHeader(
    onSearchClick: () -> Unit,
    selectedFilters: List<Filter>,
    filters: List<Filter>,
    onFilterClicked: (Filter) -> Unit,
    filterRowState: LazyListState
) {
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
        onCancelClicked = {}, // todo - better cancel behavior
        enabled = false
    )
    FilterRow(
        currentFiltersSelected = selectedFilters,
        onFilterClicked = onFilterClicked,
        filters = filters,
        rowState = filterRowState
    )
}

@Preview
@Composable
private fun HomePreview() = EateryPreview {
    val showBottomBar = remember { mutableStateOf(false) }
    HomeScreenContent(
        favorites = emptyList(),
        nearestEateries = emptyList(),
        eateriesApiResponse = EateryApiResponse.Success(emptyList()),
        selectedFilters = emptyList(),
        homeScreenFilters = listOf(
            Filter.FromEateryFilter.North,
            Filter.FromEateryFilter.West,
            Filter.FromEateryFilter.Central,
            Filter.FromEateryFilter.Swipes,
            Filter.FromEateryFilter.BRB,
            Filter.RequiresFavoriteEateries.Favorites,
            Filter.FromEateryFilter.Under10,
        ),
        showBottomBar = showBottomBar,
        permissionReqDialogFirstTimeShown = false,
        onSearchClick = {},
        onEateryClick = {},
        onFavoriteExpand = {},
        onCompareMenusClick = {},
        onNotificationsClick = {},
        addPaymentMethodFilters = {},
        addFavorite = {},
        removeFavorite = {},
        onToggleFilterPressed = {},
        onResetFiltersClicked = {},
        pingEateries = {},
        getNotificationFlowCompleted = { true },
        setNotificationFlowCompleted = {}
    )
}

/**
 * Keeps track of when app navigates away from HomeScreen so PermissionRequestDialog
 * only occurs when the app FIRST is navigated to the HomeScreen.
 */
object FirstTimeShown {
    var firstTimeShown = true
}

