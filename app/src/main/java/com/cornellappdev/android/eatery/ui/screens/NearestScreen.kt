package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.general.EateryCard
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayTwo
import com.cornellappdev.android.eatery.ui.viewmodels.NearestViewModel
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

/**
 * The Nearest to You screen that shows eateries sorted by walk times.
 */
@Composable
fun NearestScreen(
    nearestViewModel: NearestViewModel = hiltViewModel(),
    onEateryClick: (eatery: Eatery) -> Unit
) {
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val nearestEateries = nearestViewModel.nearestEateries.collectAsState().value
    val favorites = nearestViewModel.favoriteEateries.collectAsState().value

    Column(
        modifier = Modifier
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Nearest to You",
            color = EateryBlue,
            style = EateryBlueTypography.h2,
            modifier = Modifier.padding(top = 7.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // TODO add filtering
        if (nearestEateries.isEmpty()) {
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
                    items = nearestEateries,
                    key = { eatery ->
                        eatery.id!!
                    }) { eatery ->
                    EateryCard(
                        eatery = eatery,
                        isFavorite = favorites.contains(eatery),
                        onFavoriteClick = {
                            nearestViewModel.setFavorite(eatery.id, it)
                        }) {
                        onEateryClick(it)
                    }
                }

                item {
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}
