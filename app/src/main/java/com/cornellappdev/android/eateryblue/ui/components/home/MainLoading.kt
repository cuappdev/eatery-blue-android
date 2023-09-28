package com.cornellappdev.android.eateryblue.ui.components.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayThree
import com.cornellappdev.android.eateryblue.ui.theme.GrayTwo
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainLoading() {

}

@Composable
fun FilterItem(width: Dp, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .padding(end = 8.dp)
            .then(modifier)
            .width(width)
            .fillMaxHeight(),
        backgroundColor = GrayTwo,
        shape = RoundedCornerShape(8.dp),
        content = {}
    )
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

sealed class MainLoadingItem {
    object SearchBox : MainLoadingItem()
    object FilterOptions : MainLoadingItem()
    data class EaterySectionLabel(val label: String) : MainLoadingItem()
    object Spacer : MainLoadingItem()
    object EaterySectionList : MainLoadingItem()
    object EateryItem : MainLoadingItem()

    companion object {
        val mainItems: List<MainLoadingItem> = listOf(
            listOf(SearchBox),
            listOf(FilterOptions),
            listOf(EaterySectionLabel("Finding flavorful food...")),
            listOf(EaterySectionList),
            listOf(EaterySectionLabel("Checking for chow...")),
            listOf(EaterySectionList),
            listOf(Spacer),
            List(15) { EateryItem }
        ).flatten()

        @OptIn(ExperimentalFoundationApi::class)
        @Composable
        fun CreateMainLoadingItem(item: MainLoadingItem, shimmer: Shimmer) {
            when (item) {
                is SearchBox -> {
                    Surface(
                        modifier = Modifier
                            .shimmer(shimmer)
                            .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
                            .height(38.dp)
                            .fillMaxWidth(),
                        color = GrayTwo,
                        shape = RoundedCornerShape(
                            CornerSize(8.dp)
                        )
                    ) {}
                }

                is FilterOptions -> {
                    Row(
                        modifier = Modifier
                            .shimmer(shimmer)
                            .height(34.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        FilterItem(110.dp, modifier = Modifier.padding(start = 16.dp))
                        FilterItem(160.dp)
                        FilterItem(80.dp)
                        FilterItem(60.dp)
                        FilterItem(100.dp)
                        FilterItem(120.dp, modifier = Modifier.padding(end = 8.dp))

                    }
                }

                is EaterySectionLabel -> {
                    Text(
                        text = item.label,
                        style = EateryBlueTypography.h4,
                        modifier = Modifier
                            .shimmer(shimmer)
                            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp),
                        color = GrayThree
                    )
                }

                is EaterySectionList -> {
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
                }

                is Spacer -> {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Loading Eateries...",
                        style = EateryBlueTypography.h4,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .shimmer(shimmer),
                        color = GrayThree
                    )
                }

                is EateryItem -> {
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
}
