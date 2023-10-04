package com.cornellappdev.android.eateryblue.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.ui.components.general.EateryCard
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayTwo
import com.cornellappdev.android.eateryblue.ui.viewmodels.FavoritesViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryRetrievalState
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun FavoritesScreen(
    favoriteViewModel: FavoritesViewModel = hiltViewModel(),
    onEateryClick: (eatery: Eatery) -> Unit
) {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    LaunchedEffect(Unit) {
        favoriteViewModel.queryFavoriteEateries()
    }

    Column(
        modifier = Modifier
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Favorites",
            color = EateryBlue,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp)
        )

        // TODO add filtering

        when (favoriteViewModel.eateryRetrievalState) {
            is EateryRetrievalState.Pending -> {
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

            is EateryRetrievalState.Error -> {
                // TODO Add No Internet/Oopsie display
            }

            is EateryRetrievalState.Success -> {
                if (favoriteViewModel.favoriteEateries.isEmpty()) {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = favoriteViewModel.favoriteEateries.distinct(),
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
