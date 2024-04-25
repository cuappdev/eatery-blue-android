package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.models.MenuCategory
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.theme.Yellow
import com.cornellappdev.android.eatery.ui.viewmodels.EateryDetailViewModel

@Composable
fun EateryDetailsStickyHeader(
    nextEvent: Event?,
    eatery: Eatery,
    filterText: String,
    fullMenuList: MutableList<String>,
    listState: LazyListState,
    eateryDetailViewModel: EateryDetailViewModel = hiltViewModel(),
    onItemClick: (Int) -> Unit
) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 48.dp, bottom = 12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                Text(
                    modifier = Modifier
                        .height(24.dp)
                        .widthIn(0.dp, 280.dp)
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    text = eatery.name ?: "Loading...",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = GrayFive,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                )

                Button(
                    onClick = { eateryDetailViewModel.toggleFavorite() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(40.dp),
                    contentPadding = PaddingValues(6.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                    ),
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
                ) {
                    Icon(
                        imageVector = if (eateryDetailViewModel.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        tint = if (eateryDetailViewModel.isFavorite) Yellow else GrayFive,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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

                    nextEvent?.menu?.forEach { category ->
                        item {
                            val categoryIndex = fullMenuList.indexOf(category.category)
                            CategoryItem(
                                category,
                                listState,
                                nextEvent,
                                fullMenuList
                            ) { onItemClick(categoryIndex) }
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
fun CategoryItem(
    category: MenuCategory,
    listState: LazyListState,
    nextEvent: Event?,
    fullMenuList: MutableList<String>,
    onItemClick: () -> Unit
) {
    val isHighlighted = highlightCategory(
        category,
        listState,
        nextEvent,
        fullMenuList
    )
    val backgroundColor = if (isHighlighted) Color.Black else Color.White
    val textColor = if (isHighlighted) Color.White else GrayFive
    Surface(
        modifier = Modifier.fillMaxHeight(),
        shape = RoundedCornerShape(100),
        color = backgroundColor,
        onClick = onItemClick
    ) {
        Text(
            modifier = Modifier
                .height(180.dp)
                .padding(vertical = 8.dp, horizontal = 10.dp),
            text = category.category ?: "Category",
            color = textColor,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
fun highlightCategory(category: MenuCategory, listState: LazyListState, nextEvent: Event?, fullMenuList: MutableList<String>): Boolean {
        val firstMenuItemIndex = listState.firstVisibleItemIndex - 5

        if (firstMenuItemIndex >= 0 && firstMenuItemIndex < fullMenuList.size) {
            val item = fullMenuList[firstMenuItemIndex]
            val isCategoryName = nextEvent?.menu?.any { category ->
                category.category == item
            } ?: false

            if(isCategoryName) {
                return category.category == item
            } else {
                for (i in firstMenuItemIndex - 1 downTo 0) {
                    val previousItem = fullMenuList[i]
                    if (nextEvent?.menu?.any { category -> category.category == previousItem } == true) {
                        return category.category == previousItem
                    }
                }
            }
        }
    return false
}
