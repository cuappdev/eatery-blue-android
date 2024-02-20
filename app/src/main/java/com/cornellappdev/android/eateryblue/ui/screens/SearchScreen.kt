package com.cornellappdev.android.eateryblue.ui.screens

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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.ui.components.general.EateryCard
import com.cornellappdev.android.eateryblue.ui.components.general.Filter
import com.cornellappdev.android.eateryblue.ui.components.general.FilterRow
import com.cornellappdev.android.eateryblue.ui.components.general.PaymentMethodsBottomSheet
import com.cornellappdev.android.eateryblue.ui.components.general.SearchBar
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.viewmodels.SearchViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    onEateryClick: (eatery: Eatery) -> Unit,
    onFavoriteClick: () -> Unit
) {
    val selectedPaymentMethodFilters = remember { mutableStateListOf<Filter>() }
    val focusRequester = remember { FocusRequester() }
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()

    val query by searchViewModel.searchFlow.collectAsState()
    val favorites = searchViewModel.favoriteEateries.collectAsState().value
    val recentSearches =
        searchViewModel.recentSearches.collectAsState().value.reversed().take(10).distinct()
    val filters = searchViewModel.filtersFlow.collectAsState().value
    val eateryApiResponse = searchViewModel.searchResultEateries.collectAsState().value


    // Automatically brings the search bar into focus when the view is composed
    LaunchedEffect(null) {
        if (query.isEmpty())
            focusRequester.requestFocus()
    }

    // Here a DisposableEffect is launched when the bottom sheet opens.
    // When it disappears it's from the view hierarchy, which will cause
    // onDispose to be called, adding/resetting the payment filters.
    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                // Handles the case where filters reset as well (by adding an empty list).
                searchViewModel.addPaymentMethodFilters(selectedPaymentMethodFilters)
            }
        }
    }

    ModalBottomSheetLayout(sheetState = modalBottomSheetState, sheetShape = RoundedCornerShape(
        bottomStart = 0.dp, bottomEnd = 0.dp, topStart = 12.dp, topEnd = 12.dp
    ), sheetElevation = 8.dp, sheetContent = {
        PaymentMethodsBottomSheet(selectedFilters = selectedPaymentMethodFilters, hide = {
            coroutineScope.launch {
                modalBottomSheetState.hide()
            }
        })
    }, content = { ->
        val listState = rememberLazyListState()

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            stickyHeader {

                Column(modifier = Modifier.background(Color.White)) {
                    SearchBar(
                        searchText = query,
                        onSearchTextChange = {
                            searchViewModel.queryEateries(it)
                        },
                        placeholderText = "Search for grub...",
                        modifier = Modifier.padding(
                            top = 64.dp,
                            bottom = 6.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        onCancelClicked = {},
                        focusRequester = focusRequester,
                        enabled = true
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
                                searchViewModel.removeFilter(filter)
                            } else {
                                searchViewModel.addFilter(filter)
                            }
                        })

                    Spacer(modifier = Modifier.height(6.dp))
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(GrayZero)
                    )
                }
            }

            item {
                if (query.isEmpty()) {
                    if (eateryApiResponse is EateryApiResponse.Success) {
                        Row(
                            modifier = Modifier
                                .padding(
                                    start = 16.dp, end = 16.dp, bottom = 12.dp, top = 12.dp
                                )
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Favorites", style = EateryBlueTypography.h4
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
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            items(items = favorites, key = { eatery ->
                                eatery.id!!
                            }) { eatery ->
                                FavoriteItem(eatery, onEateryClick)
                            }
                        }

                        Text(
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                            text = "Recent Searches",
                            style = EateryBlueTypography.h4
                        )

                        recentSearches.forEach { eateryId ->
                            val eateryResponse = searchViewModel.openEatery(eateryId).value
                            if (eateryResponse is EateryApiResponse.Success) {
                                Box(
                                    Modifier.padding(
                                        horizontal = 16.dp, vertical = 12.dp
                                    )
                                ) {
                                    val eatery = eateryResponse.data
                                    EateryCard(
                                        eatery = eatery,
                                        isFavorite = favorites.any { favoriteEatery ->
                                            favoriteEatery.id == eatery.id
                                        },
                                        onFavoriteClick = {
                                            if (it) {
                                                searchViewModel.addFavorite(eatery.id)
                                            } else {
                                                searchViewModel.removeFavorite(eatery.id)
                                            }
                                        }) {
                                        searchViewModel.addRecentSearch(it.id)
                                        onEateryClick(it)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (eateryApiResponse is EateryApiResponse.Success) {
                        val eateries = eateryApiResponse.data
                        eateries.forEach { eatery ->
                            Box(
                                Modifier.padding(
                                    horizontal = 16.dp, vertical = 12.dp
                                )
                            ) {
                                EateryCard(eatery = eatery,
                                    isFavorite = favorites.any { favoriteEatery ->
                                        favoriteEatery.id == eatery.id
                                    },
                                    onFavoriteClick = {
                                        if (it) {
                                            searchViewModel.addFavorite(eatery.id)
                                        } else {
                                            searchViewModel.removeFavorite(eatery.id)
                                        }
                                    }) {
                                    searchViewModel.addRecentSearch(it.id)
                                    onEateryClick(it)
                                }
                            }
                        }
                    }

                }
            }


        }
    })
}

/**
 * Each favorited item within the favorited list
 */
@Composable
fun FavoriteItem(
    eatery: Eatery, onEateryClick: (eatery: Eatery) -> Unit
) {
    Column(modifier = Modifier
        .width(96.dp)
        .clickable {
            onEateryClick(eatery)
        }) {
        // Use box to overlay the star over the eatery
        Box {
            GlideImage(imageModel = { eatery.imageUrl ?: "" },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(10.dp)),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                ),
                component = rememberImageComponent {
                    +ShimmerPlugin(
                        baseColor = Color.White,
                        highlightColor = GrayZero,
                        durationMillis = 350,
                        dropOff = 0.65f,
                        tilt = 20f
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
                        contentDescription = "Eatery Image",
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
