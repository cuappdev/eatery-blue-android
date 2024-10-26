package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
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
import com.cornellappdev.android.eatery.ui.components.details.FavoritesToggleClicked
import com.cornellappdev.android.eatery.ui.components.details.FavoritesToggleUnclicked
import com.cornellappdev.android.eatery.ui.components.general.Filter
import com.cornellappdev.android.eatery.ui.components.general.FilterRow
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayTwo
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.viewmodels.FavoritesViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.HomeViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoritesScreen(
    favoriteViewModel: FavoritesViewModel = hiltViewModel(),
    onEateryClick: (eatery: Eatery) -> Unit,
    onSearchClick: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val favoriteEateriesApiResponse = favoriteViewModel.favoriteEateries.collectAsState().value
    val filters = homeViewModel.filtersFlow.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    Column(
        modifier = Modifier
            .padding(top = 36.dp, start = 10.dp, end = 10.dp)
            .fillMaxSize()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_left_chevron),
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp)
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp)
            )
        }
        Text(
            text = "Favorites",
            color = EateryBlue,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp, start = 6.dp, end = 6.dp)
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
                        item{
                            FavoritesHeader(
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
//                        items(
//                            items = favoriteEateries,
//                            key = { eatery ->
//                                eatery.id!!
//                            }) { eatery ->
//                            EateryCard(
//                                eatery = eatery,
//                                isFavorite = true,
//                                onFavoriteClick = {
//                                    if (!it) {
//                                        favoriteViewModel.removeFavorite(eatery.id)
//                                    }
//                                }) {
//                                onEateryClick(it)
//                            }
//                        }

                        items(10){
                            FavoritesCard()
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
private fun FavoritesHeader(
    filters: List<Filter>,
    onFilterClicked: (Filter) -> Unit
) {

    Row(
        horizontalArrangement = (Arrangement.spacedBy(8.dp))
    ){
        FavoritesToggleUnclicked({}, "Eateries")
        FavoritesToggleClicked({}, "Items")
    }
    Spacer(modifier = Modifier.height(12.dp))
    FilterRow(
        currentFiltersSelected = filters,
        onFilterClicked = onFilterClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesCard(){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(Dp.Hairline, GrayZero), RoundedCornerShape(8)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors( // Use CardDefaults to set background color
            containerColor = Color.White // Change this to any color you want
        ),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = {}
    ){
        Column(
            Modifier.padding(12.dp).fillMaxWidth()
        ){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start =8.dp, end = 8.dp)
            ){
                Text("Item Name", fontSize = 20.sp, style = EateryBlueTypography.button)
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_star_filled),
                    contentDescription = "favorited",
                    modifier = Modifier
                        .size(20.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start =8.dp, end = 8.dp)
            ){
                Text("Availability", fontSize = 12.sp, color = Color(0xFF63C774),style = EateryBlueTypography.button)
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_down_chevron),
                    contentDescription = "expand",
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ItemsScreen(){
    LazyColumn{
        items(10){
            FavoritesCard()
        }
    }
}