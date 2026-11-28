package com.cornellappdev.android.eatery.ui.components.home

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun FilterItem(width: Dp, modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier
            .padding(end = 8.dp)
            .then(modifier)
            .width(width)
            .fillMaxHeight(),
        color = currentColors.backgroundDefault92,
        shape = RoundedCornerShape(8.dp),
        content = {}
    )
}

@Composable
fun EateryBlob(
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = false,
    height: Dp = 186.dp,
    color: Color = currentColors.backgroundDefault92,
    endPadding: Dp = 12.dp
) {
    Surface(
        modifier = Modifier
            .padding(end = endPadding)
            .then(modifier)
            .clip(
                RoundedCornerShape(
                    CornerSize(8.dp),
                )
            )
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier.width(295.dp))
            .height(height),
        color = color
    ) {}
}

@DualModePreview
@Composable
private fun EateryBlobPreview() = EateryPreview {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EateryBlob()
        EateryBlob(
            fillMaxWidth = true,
            height = 216.dp,
            color = currentColors.accentPrimary,
            endPadding = 0.dp
        )
    }
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
                        color = currentColors.backgroundDefault92,
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
                        color = currentColors.backgroundDefault10
                    )
                }

                is EaterySectionList -> {
                    CompositionLocalProvider(
                        LocalOverscrollFactory provides null
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
                        color = currentColors.backgroundDefault10
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
