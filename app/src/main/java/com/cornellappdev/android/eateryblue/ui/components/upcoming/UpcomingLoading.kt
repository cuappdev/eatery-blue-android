package com.cornellappdev.android.eateryblue.ui.components.upcoming

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.ui.components.home.EateryBlob
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayThree
import com.cornellappdev.android.eateryblue.ui.theme.GrayTwo
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer


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

sealed class UpcomingLoadingItem {
    data class EaterySectionLabel(val label: String) : UpcomingLoadingItem()
    object Spacer : UpcomingLoadingItem()
    object EaterySectionList : UpcomingLoadingItem()

    companion object {
        val upcomingItems: List<UpcomingLoadingItem> = listOf(
            listOf(EaterySectionLabel("Best in the biz since 2014...")),
            listOf(EaterySectionList),
            listOf(Spacer),
        ).flatten()

        @OptIn(ExperimentalFoundationApi::class)
        @Composable
        fun CreateUpcomingLoadingItem(item: UpcomingLoadingItem, shimmer: Shimmer) {
            if (item is EaterySectionList) {
                CompositionLocalProvider(
                    LocalOverscrollConfiguration provides null
                ) {
                    Row(
                        modifier = Modifier
                            .shimmer(shimmer)
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 12.dp)
                    ) {
                        EateryBlob(modifier = Modifier.padding(start = 16.dp))
                        EateryBlob()
                        EateryBlob()
                        EateryBlob()
                    }
                }
            } else if (item is Spacer) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Loading Eateries...",
                    style = EateryBlueTypography.h4,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .shimmer(shimmer),
                    color = GrayThree
                )
            } else {
                EateryBlob(
                    modifier = Modifier
                        .shimmer(shimmer)
                        .padding(start = 16.dp, top = 12.dp),
                    height = 216.dp,
                    fillMaxWidth = true
                )
            }
        }
    }
}
