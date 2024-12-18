package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.models.MenuCategory
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.util.AppStorePopupRepository
import com.cornellappdev.android.eatery.util.appStorePopupRepository
import kotlinx.coroutines.launch

@Composable
fun EateryDetailsStickyHeader(
    nextEvent: Event?,
    eatery: Eatery,
    filterText: String,
    fullMenuList: MutableList<String>,
    listState: LazyListState,
    startIndex: Int,
    onItemClick: (Int) -> Unit,
    appStorePopupRepository: AppStorePopupRepository = appStorePopupRepository(),
) {
    val rowState = rememberLazyListState()
    val rowCoroutine = rememberCoroutineScope()
    val selectedEvent = nextEvent?.menu?.find { category ->
        highlightCategory(
            category,
            listState,
            nextEvent,
            fullMenuList,
            startIndex
        )
    }
    val selectedIndex: Int =
        if (selectedEvent != null) nextEvent.menu.indexOf(selectedEvent) else -1

    // Whenever the selected index changes, scroll to the new item.
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != -1)
            rowCoroutine.launch {
                if (selectedIndex >= 4) {
                    // They've scrolled decently far down and have interacted with this menu, we can
                    // request a review
                    appStorePopupRepository.requestRatingPopup()
                }
                rowState.animateScrollToItem(selectedIndex)
            }
    }

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
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
                            val categoryIndex = fullMenuList.indexOf(category.category)
                            val isHighlighted = highlightCategory(
                                category,
                                listState,
                                nextEvent,
                                fullMenuList,
                                startIndex
                            )
                            CategoryItem(
                                category.category ?: "Category",
                                isHighlighted,
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

        Divider(
            color = GrayZero,
            thickness = 1.dp
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryItem(
    name: String,
    isHighlighted: Boolean,
    onItemClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isHighlighted) Color.Black else Color.White,
        label = "Background Color"
    )
    val textColor by animateColorAsState(
        if (isHighlighted) Color.White else GrayFive,
        label = "Text Color"
    )

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
            text = name,
            color = textColor,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        )
    }
}

/**
 * Returns true if the given menu category should be highlighted (i.e. it is scrolled to).
 */
@Composable
fun highlightCategory(
    category: MenuCategory,
    listState: LazyListState,
    nextEvent: Event?,
    fullMenuList: MutableList<String>,
    startIndex: Int
): Boolean {
    val firstVisibleState = remember { derivedStateOf { listState.firstVisibleItemIndex } }
    // Note: - 5 here assumes that there are 5 UI elements above the menu, which is true currently.
    //  If that changes, this must be tweaked.
    val firstMenuItemIndex = firstVisibleState.value - startIndex

    if (firstMenuItemIndex >= 0 && firstMenuItemIndex < fullMenuList.size) {
        val item = fullMenuList[firstMenuItemIndex]
        val isCategoryName = nextEvent?.menu?.any { it.category == item } ?: false

        if (isCategoryName) {
            return category.category == item
        } else {
            for (i in firstMenuItemIndex - 1 downTo 0) {
                val previousItem = fullMenuList[i]
                if (nextEvent?.menu?.any { it.category == previousItem } == true) {
                    return category.category == previousItem
                }
            }
        }
    }
    return false
}
