package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.details.ActiveToggle
import com.cornellappdev.android.eatery.ui.components.details.InactiveToggle
import com.cornellappdev.android.eatery.ui.components.general.EateryCard
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterRow
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayTwo
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.theme.Yellow
import com.cornellappdev.android.eatery.ui.viewmodels.FavoritesViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.HomeViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.ToggleViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.util.EateryPreview
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoritesScreen(
    favoriteViewModel: FavoritesViewModel = hiltViewModel(),
    onEateryClick: (eatery: Eatery) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    toggleViewModel: ToggleViewModel = hiltViewModel()
) {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val favoriteEateriesApiResponse = favoriteViewModel.favoriteEateries.collectAsState().value
    val filters = homeViewModel.filtersFlow.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val toggle = toggleViewModel.isToggled.collectAsState()

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

        // TODO add filtering
        when (favoriteEateriesApiResponse) {
            is EateryApiResponse.Pending -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(15) {
                        EateryBlob(
                            modifier = Modifier.shimmer(shimmer),
                            height = 216.dp,
                            fillMaxWidth = true
                        )
                    }
                }
            }


            is EateryApiResponse.Error -> {
                // TODO Add No Internet/Oopsie display
            }

            is EateryApiResponse.Success -> {
                val favoriteEateries = favoriteEateriesApiResponse.data
                if (favoriteEateries.isEmpty()) {
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
                                text = "You currently have no favorite eateries!",
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                ),
                                color = Color.Black,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        item {
                            Row(
                                horizontalArrangement = (Arrangement.spacedBy(8.dp))
                            ) {
                                //toggle.value = true means that the active toggle should be the Eatery button
                                if (toggle.value) {
                                    ActiveToggle(onClick = { }, label = "Eateries")
                                    InactiveToggle(
                                        onClick = { toggleViewModel.toggle() },
                                        label = "Items"
                                    )
                                } else {
                                    InactiveToggle(
                                        onClick = { toggleViewModel.toggle() },
                                        label = "Eateries"
                                    )
                                    ActiveToggle(onClick = { }, label = "Items")
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            FilterHeader(
                                filters = filters,
                                onFilterClicked = { filter ->
                                    if (filters.contains(filter)) {
                                        homeViewModel.removeFilter(filter)
                                    } else {
                                        homeViewModel.addFilter(filter)
                                    }
                                }
                            )
                        }
                        if (toggle.value) {
                            items(
                                items = favoriteEateries,
                                key = { eatery ->
                                    eatery.id!!
                                }) { eatery ->
                                EateryCard(
                                    eatery = eatery,
                                    isFavorite = true,
                                    onFavoriteClick = {
                                        if (!it) {
                                            favoriteViewModel.removeFavorite(eatery.id)
                                        }
                                    }) {
                                    onEateryClick(it)
                                }
                            }
                        } else {
                            items(10) {
                                ItemFavoritesCard()
                            }
                        }

                        item {
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EateryBlob(
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = false,
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

@Composable
fun FilterHeader(
    filters: List<Filter>,
    onFilterClicked: (Filter) -> Unit
) {
    FilterRow(
        currentFiltersSelected = filters,
        onFilterClicked = onFilterClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemFavoritesCard() {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(Dp.Hairline, GrayZero), RoundedCornerShape(8)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Item Name", fontSize = 20.sp, style = EateryBlueTypography.button)
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_star_filled),
                    tint = Yellow,
                    contentDescription = "favorited",
                    modifier = Modifier
                        .size(20.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    "Availability",
                    fontSize = 12.sp,
                    color = Color(0xFF63C774),
                    style = EateryBlueTypography.button
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_down_chevron),
                    contentDescription = "expand",
                    modifier = Modifier.clickable(onClick = {isExpanded = !isExpanded })
                )
            }
            if (!isExpanded) {
                HorizontalDivider(thickness = Dp.Hairline)
                ItemInformation("Lunch", listOf("Morrison", "Appel"))
            }
        }
    }
}


@Composable
fun ItemInformation(meal: String, eateryName: List<String>) {
    Column(
        modifier = Modifier.padding(top = 8.dp)
    ){
        Text(meal, fontSize = 20.sp, style = EateryBlueTypography.button)
        eateryName.forEach { eatery ->
            Text(eatery, style = EateryBlueTypography.caption, color = Color(0xff7D8288))
        }
    }

}

@Preview
@Composable
private fun FavoritesCardPreview() = EateryPreview {
    ItemFavoritesCard()
}
