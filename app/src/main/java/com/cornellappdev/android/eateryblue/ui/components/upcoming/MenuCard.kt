package com.cornellappdev.android.eateryblue.ui.components.upcoming

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.models.Event
import com.cornellappdev.android.eateryblue.data.models.MenuItem
import com.cornellappdev.android.eateryblue.ui.components.general.MealFilter
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.theme.Green
import com.cornellappdev.android.eateryblue.ui.theme.Orange
import com.cornellappdev.android.eateryblue.ui.viewmodels.UpcomingViewModel
import java.time.format.DateTimeFormatter


/**
 * Represents the card for each eatery that is shown in the list
 * in the upcoming menu screen
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MenuCard(
    eatery: Eatery,
    upcomingViewModel: UpcomingViewModel,
    day: Int,
    selectEatery: (eatery: Eatery) -> Unit = {},
) {
    var openDropdown by remember { mutableStateOf(false) }
    val mealFilter = upcomingViewModel.mealFilterFlow.collectAsState().value
    val event = eatery.getSelectedDayMeal(mealFilter, day)

    Card(
        elevation = 5.dp,
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.White,
    ) {
        Column(modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 5.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = eatery.name ?: "",
                    style = EateryBlueTypography.h5,
                )
                IconButton(
                    onClick = {
                        openDropdown = !openDropdown
                    },
                    modifier = Modifier
                        .padding(top = 10.dp, end = 20.dp)
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (!openDropdown) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                        contentDescription = "Expand menu",
                        tint = Color.Black
                    )
                }
                Row(modifier = Modifier.padding(top = 24.dp)) {
                    if (event!![0].startTime != null && event[0].endTime != null) {
                        val closed = eatery.isClosed()
                        val meal = eatery.getCurrentEvent()

                        val mealName = meal?.description
                        val differentMeal = !mealFilter.text.contains(meal?.description)

                        val openText = when {
                            closed -> "Closed"
                            differentMeal -> "Open for $mealName"
                            else -> "Open"
                        }

                        val openColor = when {
                            closed -> Red
                            differentMeal -> Orange
                            else -> Green
                        }

                        if (day == 0) {
                            Text(
                                text = openText,
                                style = EateryBlueTypography.subtitle2,
                                color = openColor
                            )
                        }
                        Text(
                            modifier = Modifier.padding(
                                start = if (day == 0) 8.dp else 0.dp,
                                bottom = 8.dp
                            ),
                            text = "${
                                event[0].startTime?.format(
                                    DateTimeFormatter.ofPattern(
                                        "K:mm a"
                                    )
                                )
                            } - ${
                                event[0].endTime?.format(
                                    DateTimeFormatter.ofPattern("K:mm a")
                                )
                            }",
                            style = EateryBlueTypography.subtitle2,
                            color = GrayFive
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .animateContentSize(tween(250))
                    .fillMaxWidth()
            ) {
                if (openDropdown) {
                    Spacer(
                        modifier = Modifier
                            .padding(end = 12.dp, bottom = 8.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(GrayZero, CircleShape)
                    )
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        EateryDetails(
                            eatery = eatery,
                            selectEatery = { selectEatery(eatery) },
                            meal = mealFilter,
                            day = day,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EateryDetails(
    eatery: Eatery,
    meal: MealFilter,
    day: Int,
    selectEatery: (eatery: Eatery) -> Unit = {}
) {
    Column {
        eatery.getSelectedDayMeal(meal, day)!!.forEach { event ->
            EateryEventMenu(event = event)
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            onClick = {
                selectEatery(eatery)
            },
            backgroundColor = GrayZero,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
        ) {

            Row(
                modifier = Modifier.padding(
                    end = 12.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_eatery),
                    contentDescription = null,
                    tint = Color.Black
                )
                Text(
                    text = "View Eatery Details",
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )

            }
        }
    }

}

@Composable
fun EateryEventMenu(event: Event) {

    Row(
        modifier = Modifier
            .padding(end = 16.dp)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            event.menu!!.forEach { category ->
                Text(
                    text = category.category ?: "",
                    style = EateryBlueTypography.h5,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                category.items!!.forEach { item ->
                    MenuItemDisplay(item = item)
                }
            }
        }
    }
}

@Composable
fun MenuItemDisplay(item: MenuItem) {
    Text(
        text = item.name!!,
        style = EateryBlueTypography.caption,
        fontWeight = FontWeight.Normal
    )
}
