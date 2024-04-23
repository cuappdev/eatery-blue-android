package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.models.MenuCategory
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayZero

@Composable
fun EateryDetailsStickyHeader(
    nextEvent: Event?,
    eatery: Eatery,
    filterText: String,
    onItemClick: (Int) -> Unit
) {
    val targetPosition = remember { mutableStateOf(IntOffset.Zero) }

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 56.dp, bottom = 12.dp)
        ) {
            Text(
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = eatery.name ?: "Loading...",
                color = GrayFive,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            val filteredItemsList = nextEvent?.menu?.mapNotNull { category ->
                category.items?.filter {
                    it.name?.contains(filterText, true) ?: false
                }
            } ?: emptyList()

            if (!filteredItemsList.isNullOrEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(16.dp)
                        )
                    }

                    nextEvent?.menu?.forEachIndexed { index, category: MenuCategory ->
                            item {
                                MenuItem(
                                    category = category,
                                    onItemClick = { onItemClick(index) }
                                )
                            }
                            item {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(10.dp)
                                )
                            }
                    }

                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(6.dp)
                        )
                    }
                }
            }
        }

        Divider(
            color = GrayZero,
            thickness = 1.dp
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MenuItem(
    category: MenuCategory,
    onItemClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxHeight(),
        shape = RoundedCornerShape(100),
        color = Color.White,
        onClick = onItemClick
    ) {
        Text(
            modifier = Modifier
                .height(180.dp)
                .padding(vertical = 8.dp, horizontal = 10.dp),
            text = category.category ?: "Category",
            color = GrayFive,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        )
    }
}