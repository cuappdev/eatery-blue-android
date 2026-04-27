package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.ui.theme.currentColors
import kotlinx.coroutines.launch

@Composable
fun EateryDetailsStickyHeader(
    nextEvent: Event?,
    filterText: String,
    fullMenuList: MutableList<String>,
    listState: LazyListState,
    startIndex: Int,
    onItemClick: (Int) -> Unit,
    onRequestRatingPopup: () -> Unit = {},
) {
    val rowState = rememberLazyListState()
    val rowCoroutine = rememberCoroutineScope()

    val highlightedCategoryName by remember(nextEvent, fullMenuList, startIndex) {
        derivedStateOf {
            val menu = nextEvent?.menu ?: return@derivedStateOf null
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val canScrollForward = listState.canScrollForward

            if (!canScrollForward && visibleItems.isNotEmpty()) {
                // At the bottom of the list: highlight the last visible category so pills
                // advance past whichever category is at the top of the viewport.
                visibleItems.reversed().firstNotNullOfOrNull { info ->
                    val idx = info.index - startIndex
                    fullMenuList.getOrNull(idx)?.takeIf { name ->
                        menu.any { it.name == name }
                    }
                }
            } else {
                val firstMenuItemIndex = listState.firstVisibleItemIndex - startIndex
                if (firstMenuItemIndex >= 0 && firstMenuItemIndex < fullMenuList.size) {
                    val item = fullMenuList[firstMenuItemIndex]
                    val isCategoryName = menu.any { it.name == item }
                    if (isCategoryName) {
                        item
                    } else {
                        (firstMenuItemIndex - 1 downTo 0).firstNotNullOfOrNull { i ->
                            fullMenuList.getOrNull(i)?.takeIf { name ->
                                menu.any { it.name == name }
                            }
                        }
                    }
                } else null
            }
        }
    }

    val selectedIndex =
        nextEvent?.menu?.indexOfFirst { it.name == highlightedCategoryName } ?: -1

    // Whenever the selected index changes, scroll the pill row to the new item.
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != -1)
            rowCoroutine.launch {
                if (selectedIndex >= 4) {
                    // They've scrolled decently far down — good time to request a review.
                    onRequestRatingPopup()
                }
                rowState.animateScrollToItem(selectedIndex)
            }
    }

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(currentColors.backgroundDefault)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            val filteredItemsList = nextEvent?.menu?.mapNotNull { category ->
                category.items?.filter {
                    it.name?.contains(filterText, true) ?: false
                }
            } ?: emptyList()

            if (filteredItemsList.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    state = rowState
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
                            val categoryIndex = fullMenuList.indexOf(category.name)
                            CategoryItem(
                                name = category.name ?: "Category",
                                isHighlighted = category.name == highlightedCategoryName,
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
            Spacer(modifier = Modifier.height(6.dp))
        }

        HorizontalDivider(
            color = currentColors.backgroundDefault,
            thickness = 1.dp
        )
    }
}

@Composable
fun CategoryItem(
    name: String,
    isHighlighted: Boolean,
    onItemClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isHighlighted) currentColors.textPrimary else currentColors.backgroundDefault,
        label = "Background Color"
    )
    val textColor by animateColorAsState(
        if (isHighlighted) currentColors.backgroundDefault else currentColors.textPrimary,
        label = "Text Color"
    )

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(100),
        color = backgroundColor
    ) {
        Text(
            modifier = Modifier
                .height(180.dp)
                .padding(vertical = 8.dp, horizontal = 10.dp),
            text = name,
            color = textColor,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        )
    }
}
