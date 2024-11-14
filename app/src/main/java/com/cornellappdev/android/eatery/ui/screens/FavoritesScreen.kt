package com.cornellappdev.android.eatery.ui.screens

import ItemFavoritesCard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.details.ToggleRow
import com.cornellappdev.android.eatery.ui.components.general.EateryCard
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterRow
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayTwo
import com.cornellappdev.android.eatery.ui.viewmodels.FavoritesScreenViewState
import com.cornellappdev.android.eatery.ui.viewmodels.FavoritesViewModel
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun FavoritesScreen(
    favoriteViewModel: FavoritesViewModel = hiltViewModel(),
    onEateryClick: (eatery: Eatery) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val favoritesScreenViewState =
        favoriteViewModel.favoritesScreenViewState.collectAsState().value
    var toggle by remember { mutableStateOf(true) }


    Column(
        modifier = Modifier
            .padding(top = 36.dp, start = 10.dp, end = 10.dp)
            .fillMaxSize()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { onBackClick() }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_left_chevron),
                    contentDescription = "Back"
                )
            }
            IconButton(
                onClick = { onSearchClick() }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                )
            }

        }
        Text(
            text = "Favorites",
            color = EateryBlue,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(start = 6.dp, end = 6.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (favoritesScreenViewState) {
            is FavoritesScreenViewState.Loading -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(15) {
                        EateryBlob(
                            modifier = Modifier.shimmer(shimmer),
                            height = 216.dp,
                        )
                    }
                }
            }

            is FavoritesScreenViewState.Error -> {
                // TODO we should have a better no internet display
                EateriesEmptyState("Failed to obtain Eatery data")
            }

            is FavoritesScreenViewState.Loaded -> {
                val favoriteEateries = favoritesScreenViewState.eateries
                MainScrollableContent(
                    toggle,
                    setToggle = { toggle = it },
                    favoritesScreenViewState,
                    favoriteEateries,
                    onEateryClick,
                    toggleEateryFilter = favoriteViewModel::toggleEateryFilter,
                    toggleItemFilter = favoriteViewModel::toggleItemFilter,
                    removeFavorite = favoriteViewModel::removeFavorite,
                    removeFavoriteMenuItem = favoriteViewModel::removeFavoriteMenuItem,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ColumnScope.MainScrollableContent(
    toggle: Boolean,
    setToggle: (Boolean) -> Unit,
    favoritesScreenViewState: FavoritesScreenViewState.Loaded,
    favoriteEateries: List<Eatery>,
    onEateryClick: (eatery: Eatery) -> Unit,
    toggleEateryFilter: (filter: Filter.FromEateryFilter) -> Unit,
    toggleItemFilter: (filter: Filter) -> Unit,
    removeFavorite: (eateryId: Int?) -> Unit,
    removeFavoriteMenuItem: (menuItem: String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ToggleRow(toggle, setToggle)
            Spacer(modifier = Modifier.height(12.dp))

            FilterRow(
                currentFiltersSelected = if (toggle) favoritesScreenViewState.selectedEateryFilters
                else favoritesScreenViewState.selectedItemFilters,
                onFilterClicked = { filter ->
                    if (toggle) {
                        (filter as? Filter.FromEateryFilter)?.let {
                            toggleEateryFilter(it)
                        }
                    } else {
                        toggleItemFilter(filter)
                    }
                },
                filters = if (toggle) {
                    favoritesScreenViewState.eateryFilters
                } else {
                    favoritesScreenViewState.itemFilters
                }
            )
        }

        if (toggle) {
            item {
                AnimatedVisibility(favoriteEateries.isEmpty()) {
                    EateriesEmptyState(message = "You currently have no favorite eateries!")
                }
            }

            items(
                items = favoriteEateries,
                key = { eatery ->
                    eatery.id!!
                }) { eatery ->

                EateryCard(
                    eatery = eatery,
                    isFavorite = true,
                    modifier = Modifier.animateItemPlacement(),
                    onFavoriteClick = {
                        if (!it) {
                            removeFavorite(eatery.id)
                        }
                    }) {
                    onEateryClick(it)
                }
            }
        } else {
            item {
                AnimatedVisibility(favoritesScreenViewState.favoriteCards.isEmpty()) {
                    EateriesEmptyState("You currently have no favorite menu items!")
                }
            }
            items(favoritesScreenViewState.favoriteCards) { itemFavoritesCardViewState ->
                ItemFavoritesCard(
                    itemFavoritesCardViewState,
                    modifier = Modifier.animateItemPlacement(),
                    onFavoriteClick = {
                        removeFavoriteMenuItem(
                            itemFavoritesCardViewState.itemName
                        )
                    }
                )
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun EateriesEmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.7f)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_eaterylogo),
                contentDescription = null,
                modifier = Modifier
                    .height(72.dp)
                    .width(72.dp),
                tint = GrayTwo,
            )

            Text(
                text = message,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                ),
                color = Color.Black,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun EateryBlob(
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = true,
    height: Dp = 186.dp
) {
    Surface(
        modifier = Modifier
            .padding(end = 12.dp)
            .then(modifier)
            .clip(
                RoundedCornerShape(
                    CornerSize(8.dp),
                )
            )
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier.width(295.dp))
            .height(height),
        color = GrayTwo
    ) {}
}
