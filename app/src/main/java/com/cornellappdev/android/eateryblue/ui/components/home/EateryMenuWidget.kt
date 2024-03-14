package com.cornellappdev.android.eateryblue.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.Event
import com.cornellappdev.android.eateryblue.ui.components.general.SearchBar
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import java.time.format.DateTimeFormatter

/**
 * Displays the menu of a particular event based on categories like Grill or Soup
 * At the top of the widget, the meal description (e.g. Lunch or Dinner) and duration
 * of opening is displayed.
 */
@Composable
fun EateryMenuWidget(
    event: Event,
    hoursOnClick: () -> Unit
) {
    rememberCoroutineScope()

    var filterText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = event.description ?: "Full Menu",
                style = EateryBlueTypography.h4,
            )
            if (event.startTime != null && event.endTime != null) {
                Text(
                    text = "${event.startTime.format(DateTimeFormatter.ofPattern("K:mm a"))} - ${
                        event.endTime.format(
                            DateTimeFormatter.ofPattern("K:mm a")
                        )
                    }", style = EateryBlueTypography.subtitle2, color = GrayFive
                )
            }

        }
        IconButton(
            onClick = {
                hoursOnClick()
            },
            modifier = Modifier
                .padding(all = 8.dp)
                .background(color = GrayZero, shape = CircleShape)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_calendar),
                contentDescription = "Expand menu",
                modifier = Modifier.size(26.dp)
            )
        }
    }

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        SearchBar(searchText = filterText,
            onSearchTextChange = { filterText = it },
            placeholderText = "Search the menu...",
            modifier = Modifier.padding(horizontal = 16.dp),
            onCancelClicked = {
                filterText = ""
            })
        Spacer(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(GrayZero, CircleShape)
        )

        event.menu?.forEachIndexed { categoryIndex, category ->
            val filteredItems =
                category.items?.filter {
                    it.name?.contains(filterText, true)
                        ?: false
                }
            if (filteredItems.isNullOrEmpty())
                return@forEachIndexed
            Text(
                text = category.category ?: "Category",
                style = EateryBlueTypography.h5,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            filteredItems.forEachIndexed { index, menuItem ->
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                        )
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                    ) {
                        Text(
                            text = menuItem.name ?: "Item Name",
                            style = EateryBlueTypography.button,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (category.items.lastIndex != index) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(GrayZero, CircleShape)
                            )
                        }
                    }

                }
            }
            if (categoryIndex != event.menu!!.lastIndex) {
                Divider(
                    color = GrayZero,
                    modifier = Modifier
                        .height(10.dp)
                )
            }
        }
    }

    Spacer(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(GrayZero, CircleShape)
    )

    //todo this composable is never referenced, get rid of this??
//    @Composable
//    fun ReportButtonEateryDetails() {
//        Surface(
//            shape = RoundedCornerShape(17.dp),
//            modifier = Modifier
//                .height(50.dp)
//                .padding(vertical = 8.dp),
//            color = GrayZero,
//            contentColor = Color.Black
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(ButtonDefaults.ContentPadding)
//            ) {
//                Icon(imageVector = Icons.Default.Report, Icons.Default.Report.name)
//                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
//                Text(
//                    text = "Report an Issue",
//                    style = EateryBlueTypography.button,
//                    color = Color.Black,
//                )
//            }
//        }
//    }
}