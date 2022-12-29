package com.cornellappdev.android.eateryblue.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.cornellappdev.android.eateryblue.ui.components.general.Filter
import com.cornellappdev.android.eateryblue.ui.components.general.FilterRow
import com.cornellappdev.android.eateryblue.ui.components.general.PaymentMethodsBottomSheet
import com.cornellappdev.android.eateryblue.ui.components.general.SearchBar
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.viewmodels.SearchViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryRetrievalState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    onEateryClick: (eatery: Eatery) -> Unit
) {
    val selectedPaymentMethodFilters = remember { mutableStateListOf<Filter>() }
    val focusRequester = remember { FocusRequester() }
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }

    // Automatically brings the search bar into focus when the view is composed
    LaunchedEffect(Unit) {
        if (searchViewModel.firstLaunch) {
            focusRequester.requestFocus()
            searchViewModel.firstLaunch = false
        }

        // TODO check to see if this belonging in the else branch of above is better
        searchViewModel.updateFavorites()
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
            Column {
                SearchBar(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    placeholderText = "Search for grub...",
                    modifier = Modifier
                        .padding(top = 64.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                    onCancelClicked = {
                        searchText = ""
                    },
                    focusRequester = focusRequester,
                    enabled = true
                )

                FilterRow(
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    currentFiltersSelected = searchViewModel.currentFiltersSelected,
                    onPaymentMethodsClicked = {
                        coroutineScope.launch {
                            modalBottomSheetState.show()
                        }
                    },
                    onFilterClicked = { filter ->
                        if (searchViewModel.currentFiltersSelected.contains(filter)) {
                            searchViewModel.removeFilter(filter)
                        } else {
                            searchViewModel.addFilter(filter)
                        }
                    })

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GrayZero)
                )

                if (searchText.isEmpty()) {
                    if (searchViewModel.eateryRetrievalState is EateryRetrievalState.Success) {
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Favorites",
                                style = EateryBlueTypography.h4
                            )
//                            CircularBackgroundIcon(
//                                icon = painterResource(
//                                    id = R.drawable.ic_rightarrow
//                                ),
//                                onTap = { selectSection(EaterySection("Favorite Eateries") { it.isFavorite() }) },
//                                clickable = true,
//                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            items(searchViewModel.favoriteEateries) { eatery ->
                                FavoriteItem(eatery, onEateryClick)
                            }
                        }

                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp, top = 12.dp),
                            text = "Recent Searches",
                            style = EateryBlueTypography.h4
                        )
                    }
                } else {

                }
            }
        }
    )
}

/**
 * Each favorited item within the favorited list
 */
@Composable
fun FavoriteItem(
    eatery: Eatery,
    onEateryClick: (eatery: Eatery) -> Unit
) {
    Column(
        modifier = Modifier
            .width(96.dp)
            .clickable {
                onEateryClick(eatery)
            }
    ) {
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
                }
            )

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
