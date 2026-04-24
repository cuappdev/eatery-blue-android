package com.cornellappdev.android.eatery.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.components.general.EateryCard
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.PreviewData
import com.cornellappdev.android.eatery.util.popIn
import com.cornellappdev.android.eatery.util.popOut

@Composable
fun EateryHomeSection(
    title: String,
    eateries: List<Eatery>,
    favoritesDecider: (Eatery) -> Boolean,
    onEateryClick: (Eatery) -> Unit,
    onFavoriteClick: (Eatery, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    overflowEatery: Eatery? = null,
    onExpandClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .animateContentSize()
            .fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = eateries.isNotEmpty(),
            enter = popIn(),
            exit = popOut()
        ) {
            Column(
                modifier = Modifier.padding(
                    bottom = 24.dp,
                    top = 6.dp
                )
            ) {
                EateryHomeSectionHeader(
                    title = title,
                    onExpandClick = onExpandClick,
                )

                EaterySectionRow(
                    eateries = eateries.ifEmpty {
                        if (overflowEatery != null) {
                            listOf(overflowEatery)
                        } else {
                            emptyList()
                        }
                    },
                    onEateryClick = onEateryClick,
                    onFavoriteClick = onFavoriteClick,
                    favoritesDecider = favoritesDecider,
                )
            }
        }
    }
}

@Composable
private fun EateryHomeSectionHeader(
    title: String,
    onExpandClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                bottom = 17.dp,
                end = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = EateryBlueTypography.h4,
        )

        if (onExpandClick != null) {
            IconButton(
                onClick = {
                    onExpandClick()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = currentColors.backgroundDefault,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.favorites_title),
                    tint = currentColors.textPrimary
                )
            }
        } else {
            // Ensure that SpaceBetween still spaces correctly.
            Box {}
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EaterySectionRow(
    eateries: List<Eatery>,
    favoritesDecider: (Eatery) -> Boolean,
    onEateryClick: (Eatery) -> Unit,
    onFavoriteClick: (Eatery, Boolean) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.width(4.dp))
        }
        items(
            items = eateries,
            key = { eatery -> eatery.hashCode() }) { eatery ->
            EateryCard(
                eatery = eatery,
                isFavorite = favoritesDecider(eatery),
                modifier = Modifier
                    .fillParentMaxWidth(0.85f)
                    .animateItem(),
                onFavoriteClick = {
                    onFavoriteClick(eatery, it)
                }) {
                onEateryClick(it)
            }
        }

        item {
            Spacer(Modifier.width(16.dp))
        }
    }
}

@DualModePreview
@Composable
private fun EateryHomeSectionPreview() {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(currentColors.backgroundDefault)) {
        EateryHomeSection(
            title = "Eateries With Expand",
            eateries = listOf(PreviewData.mockEatery(), PreviewData.mockEatery()),
            onEateryClick = {},
            onFavoriteClick = { _, _ -> },
            onExpandClick = { },
            favoritesDecider = { false }
        )
        EateryHomeSection(
            title = "Eateries Without Expand",
            eateries = listOf(PreviewData.mockEatery(), PreviewData.mockEatery()),
            onEateryClick = {},
            onFavoriteClick = { _, _ -> },
            favoritesDecider = { false }
        )
    }
}

