package com.cornellappdev.android.eatery.ui.components.details

import android.util.Log
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

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
    val rowState = rememberLazyListState()
    val rowCoroutine = rememberCoroutineScope()
    val selectedEvent = nextEvent?.menu?.find { category ->
        highlightCategory(
            category,
            listState,
            nextEvent,
            fullMenuList
        )
    }
    val selectedIndex: Int =
        if (selectedEvent != null) nextEvent.menu.indexOf(selectedEvent) else -1

    // Whenever the selected index changes, scroll to the new item.
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != -1)
            rowCoroutine.launch {
                rowState.animateScrollToItem(selectedIndex)
            }
    }

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 48.dp, bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text(
                    modifier = Modifier
                        .height(26.dp)
                        .widthIn(0.dp, 280.dp)
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    text = eatery.name ?: "Loading...",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black,
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
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Icon(
                        imageVector = if (eateryDetailViewModel.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        tint = if (eateryDetailViewModel.isFavorite) Yellow else GrayFive,
                        contentDescription = null
                    )
                }
            }

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
                                fullMenuList
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
    fullMenuList: MutableList<String>
): Boolean {
    val firstVisibleState = remember { derivedStateOf { listState.firstVisibleItemIndex } }
    // Note: - 5 here assumes that there are 5 UI elements above the menu, which is true currently.
    //  If that changes, this must be tweaked.
    val firstMenuItemIndex = firstVisibleState.value - 5

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
