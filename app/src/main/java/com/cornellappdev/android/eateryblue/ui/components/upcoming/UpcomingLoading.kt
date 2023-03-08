package com.cornellappdev.android.eateryblue.ui.components.upcoming

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayThree
import com.cornellappdev.android.eateryblue.ui.theme.GrayTwo
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer


@Composable
fun EateryBlob2(
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp, end = 1f.dp)
                    .clip(
                        RoundedCornerShape(
                            CornerSize(8.dp),
                        )
                    )
                    .height(22.dp)
                    .width(200.dp),
                color = GrayTwo
            ) {
            }
            Surface(
                modifier = Modifier
                    .padding(start = 10.dp, top = 8.dp)
                    .clip(
                        RoundedCornerShape(
                            CornerSize(8.dp),
                        )
                    )
                    .height(18.dp)
                    .width(140.dp),
                color = GrayTwo
            ) {
            }
        }
    }

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
            when (item) {
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
                        Column(
                            modifier = Modifier
                                .shimmer(shimmer)
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp)
                        ) {
                            repeat(5) {
                                EateryBlob2()
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
                else -> {
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EateryBlob2()
}