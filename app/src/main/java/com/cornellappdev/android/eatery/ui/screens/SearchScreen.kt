package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.general.EateryCard
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterRow
import com.cornellappdev.android.eatery.ui.components.general.NetworkErrorToast
import com.cornellappdev.android.eatery.ui.components.general.PaymentMethodsBottomSheet
import com.cornellappdev.android.eatery.ui.components.general.SearchBar
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.viewmodels.SearchViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.ui.viewmodels.state.NetworkUiError
import com.cornellappdev.android.eatery.util.EateryPreview
import com.cornellappdev.android.eatery.util.PreviewData
import com.cornellappdev.android.eatery.util.popIn
import com.cornellappdev.android.eatery.util.popOut
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    onEateryClick: (eatery: Eatery) -> Unit,
    onFavoriteClick: () -> Unit
) {
    val uiState = searchViewModel.uiState.collectAsStateWithLifecycle().value

    SearchScreenContent(
        query = uiState.query,
        filters = uiState.filters,
        searchResponse = uiState.searchResponse,
        favoriteEateries = uiState.favoriteEateries,
        recentSearches = uiState.recentSearches,
        error = uiState.error,
        searchScreenFilters = searchViewModel.searchScreenFilters,
        onClearError = searchViewModel::clearError,
        onQueryEateries = searchViewModel::queryEateries,
        onAddPaymentMethodFilters = searchViewModel::addPaymentMethodFilters,
        onToggleFilter = searchViewModel::toggleFilter,
        onAddFavorite = searchViewModel::addFavorite,
        onRemoveFavorite = searchViewModel::removeFavorite,
        onAddRecentSearch = searchViewModel::addRecentSearch,
        observeEatery = { eateryId ->
            searchViewModel.observeEatery(eateryId).collectAsStateWithLifecycle().value
        },
        onEateryClick = onEateryClick,
        onFavoriteClick = onFavoriteClick,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun SearchScreenContent(
    query: String,
    filters: List<Filter>,
    searchResponse: EateryApiResponse<List<Eatery>>,
    favoriteEateries: List<Eatery>,
    recentSearches: List<Int>,
    error: NetworkUiError?,
    searchScreenFilters: List<Filter>,
    onClearError: () -> Unit,
    onQueryEateries: (String) -> Unit,
    onAddPaymentMethodFilters: (List<Filter>) -> Unit,
    onToggleFilter: (Filter) -> Unit,
    onAddFavorite: (Int, String) -> Unit,
    onRemoveFavorite: (Int, String) -> Unit,
    onAddRecentSearch: (Int?) -> Unit,
    observeEatery: @Composable (Int) -> EateryApiResponse<Eatery>,
    onEateryClick: (Eatery) -> Unit,
    onFavoriteClick: () -> Unit,
) {
    val selectedPaymentMethodFilters = remember { mutableStateListOf<Filter>() }
    val focusRequester = remember { FocusRequester() }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPaymentMethodSheet by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    NetworkErrorToast(
        error = error,
        onErrorShown = onClearError
    )

    // Automatically brings the search bar into focus when the view is composed
    LaunchedEffect(null) {
        if (query.isEmpty())
            focusRequester.requestFocus()
    }

    // Here a DisposableEffect is launched when the bottom sheet opens.
    // When it disappears it's from the view hierarchy, which will cause
    // onDispose to be called, adding/resetting the payment filters.
    if (showPaymentMethodSheet) {
        DisposableEffect(Unit) {
            onDispose {
                // Handles the case where filters reset as well (by adding an empty list).
                onAddPaymentMethodFilters(selectedPaymentMethodFilters)
            }
        }
    }

    if (showPaymentMethodSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaymentMethodSheet = false },
            sheetState = modalBottomSheetState,
            shape = RoundedCornerShape(
                bottomStart = 0.dp,
                bottomEnd = 0.dp,
                topStart = 12.dp,
                topEnd = 12.dp
            )
        ) {
            PaymentMethodsBottomSheet(selectedFilters = selectedPaymentMethodFilters, hide = {
                coroutineScope.launch {
                    modalBottomSheetState.hide()
                }.invokeOnCompletion {
                    if (!modalBottomSheetState.isVisible) showPaymentMethodSheet = false
                }
            })
        }
    }

    val listState = rememberLazyListState()

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        stickyHeader {
            Column(modifier = Modifier.background(Color.White)) {
                SearchBar(
                    searchText = query,
                    onSearchTextChange = {
                        onQueryEateries(it)
                    },
                    placeholderText = stringResource(R.string.search_placeholder_grub),
                    modifier = Modifier.padding(
                        top = 64.dp,
                        bottom = 6.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    onCancelClicked = {},
                    focusRequester = focusRequester,
                    enabled = true,
                    inputDebounceMillis = 120
                )

                Column(
                    modifier = Modifier
                        .animateContentSize()
                        .fillMaxWidth()
                ) {
                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = popIn(),
                        exit = popOut()
                    ) {
                        FilterRow(
                            currentFiltersSelected = filters,
                            onFilterClicked = { filter ->
                                onToggleFilter(filter)
                            },
                            filters = searchScreenFilters,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GrayZero)
                )
            }
        }

        if (query.isEmpty()) {
            if (searchResponse is EateryApiResponse.Success) {
                item {
                    // FAVORITES
                    Box(
                        modifier = Modifier
                            .animateContentSize()
                            .fillMaxWidth()
                    ) {
                        AnimatedVisibility(
                            visible = favoriteEateries.isNotEmpty(),
                            enter = popIn(),
                            exit = popOut()
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            bottom = 12.dp,
                                            top = 12.dp
                                        )
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.search_favorites),
                                        style = EateryBlueTypography.h4
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
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = stringResource(R.string.search_favorites),
                                            tint = Color.Black
                                        )
                                    }
                                }

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(start = 16.dp)
                                ) {
                                    items(items = favoriteEateries, key = { eatery ->
                                        eatery.id ?: eatery.hashCode()
                                    }) { eatery ->
                                        FavoriteItem(eatery, onEateryClick)
                                    }
                                }
                            }
                        }
                    }
                }
                // RECENT SEARCHES
                item {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                        text = stringResource(R.string.search_recent_searches),
                        style = EateryBlueTypography.h4
                    )
                }
                items(recentSearches) { eateryId ->
                    val eateryResponse = observeEatery(eateryId)
                    if (eateryResponse is EateryApiResponse.Success) {
                        Box(
                            Modifier.padding(
                                horizontal = 16.dp, vertical = 12.dp
                            )
                        ) {
                            val eatery = eateryResponse.data
                            EateryCard(
                                eatery = eatery,
                                isFavorite = favoriteEateries.any { favoriteEatery ->
                                    favoriteEatery.id == eatery.id
                                },
                                onFavoriteClick = {
                                    if (eatery.id != null && eatery.name != null) {
                                        if (it) {
                                            onAddFavorite(
                                                eatery.id,
                                                eatery.name
                                            )
                                        } else {
                                            onRemoveFavorite(
                                                eatery.id,
                                                eatery.name
                                            )
                                        }
                                    }
                                }) {
                                onAddRecentSearch(it.id)
                                onEateryClick(it)
                            }
                        }
                    }
                }
            }
        } else if (searchResponse is EateryApiResponse.Success) {
            // SEARCH QUERY
            val searchEateries = searchResponse.data
            items(searchEateries) { eatery ->
                Box(
                    Modifier.padding(
                        horizontal = 16.dp, vertical = 12.dp
                    )
                ) {
                    EateryCard(
                        eatery = eatery,
                        isFavorite = favoriteEateries.any { favoriteEatery ->
                            favoriteEatery.id == eatery.id
                        },
                        onFavoriteClick = {
                            if (eatery.id != null && eatery.name != null) {
                                if (it) {
                                    onAddFavorite(eatery.id, eatery.name)
                                } else {
                                    onRemoveFavorite(
                                        eatery.id,
                                        eatery.name
                                    )
                                }
                            }
                        }) {
                        onAddRecentSearch(it.id)
                        onEateryClick(it)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() = EateryPreview {
    SearchScreenContent(
        query = "Ok",
        filters = emptyList(),
        searchResponse = EateryApiResponse.Success(
            listOf(
                PreviewData.mockEatery(1).copy(name = "Okenshields"),
                PreviewData.mockEatery(2).copy(name = "Becker House Dining Room")
            )
        ),
        favoriteEateries = listOf(PreviewData.mockEatery(1).copy(name = "Okenshields")),
        recentSearches = emptyList(),
        error = null,
        searchScreenFilters = listOf(
            Filter.FromEateryFilter.North,
            Filter.FromEateryFilter.West,
            Filter.FromEateryFilter.Central
        ),
        onClearError = {},
        onQueryEateries = {},
        onAddPaymentMethodFilters = {},
        onToggleFilter = {},
        onAddFavorite = { _, _ -> },
        onRemoveFavorite = { _, _ -> },
        onAddRecentSearch = {},
        observeEatery = { EateryApiResponse.Pending },
        onEateryClick = {},
        onFavoriteClick = {}
    )
}


/**
 * Each favorited item within the favorited list
 */
@Composable
fun FavoriteItem(
    eatery: Eatery, onEateryClick: (eatery: Eatery) -> Unit
) {
    Column(
        modifier = Modifier
            .width(96.dp)
            .clickable {
                onEateryClick(eatery)
            }) {
        // Use box to overlay the star over the eatery
        Box {
            GlideImage(
                imageModel = { eatery.imageUrl ?: "" },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(10.dp)),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                ),
                component = rememberImageComponent {
                    +ShimmerPlugin(
                        Shimmer.Flash(
                            baseColor = Color.White,
                            highlightColor = GrayZero,
                            duration = 350,
                            dropOff = 0.65f,
                            tilt = 20f
                        )
                    )
                },
                failure = {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.blank_eatery_square),
                        contentDescription = stringResource(R.string.a11y_eatery_image),
                    )
                })

            Image(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = 12.dp, top = 12.dp),
                painter = painterResource(id = R.drawable.ic_starwhite_bg),
                contentDescription = null
            )
        }
        eatery.name?.let {
            Text(
                text = it,
                style = EateryBlueTypography.button,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
        }
    }
}
