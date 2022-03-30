package com.appdev.eateryblueandroid.ui.components.core.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.ui.components.core.Image
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle


@Composable
fun SearchFavoriteList(
    eateries: List<Eatery>,
    selectEatery: (eatery: Eatery) -> Unit,
) {
    var favoriteEatery = mutableListOf<Eatery>()
    eateries.forEach { eatery ->
        if (eatery.isFavorite()) {
            favoriteEatery.add(eatery)
        }
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp, 0.dp)
    ) {
        items(favoriteEatery) { eatery ->
            if (eatery.isFavorite()) {
                FavoriteItem(eatery, selectEatery)
            }
        }
    }
}

@Composable
fun FavoriteItem(
    eatery: Eatery,
    selectEatery: (eatery: Eatery) -> Unit = {}
) {
    val interactionSource = MutableInteractionSource()
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.width(96.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp, 96.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp, 92.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .clickable { selectEatery(eatery) }
                        .fillMaxSize()
                ) {
                    Image(
                        url = eatery.imageUrl ?: "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = 12.dp, top = 12.dp),
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ic_starwhite_bg),
                    contentDescription = null
                )
            }
        }
        eatery.name?.let {
            Text(
                text = it,
                textStyle = TextStyle.BODY_SEMIBOLD,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}