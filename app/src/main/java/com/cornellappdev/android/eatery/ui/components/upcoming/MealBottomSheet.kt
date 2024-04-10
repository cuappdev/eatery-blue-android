package com.cornellappdev.android.eatery.ui.components.upcoming

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.general.MealFilter
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayZero

/**
 * The pop up that shows up when users want to pick a different meal as the filter in the upcoming
 * menu screen.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MealBottomSheet(
    selectedMeal: MealFilter,
    onSubmit: (MealFilter) -> Unit,
    onReset: () -> Unit,
    hide: () -> Unit,
    sheetState: ModalBottomSheetState
) {
    val currSelectedMeal = remember { mutableStateOf(selectedMeal) }
    if (!sheetState.isVisible) currSelectedMeal.value = selectedMeal
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Menus",
                style = EateryBlueTypography.h4,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            IconButton(
                onClick = {
                    currSelectedMeal.value = selectedMeal
                    hide()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(color = GrayZero, shape = CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
            }
        }

    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
            .clickable {
                currSelectedMeal.value = MealFilter.BREAKFAST
            }
            .padding(top = 8.dp, bottom = 12.dp)
    ) {
        Column {
            Text(
                text = "Breakfast",
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.h5
            )
            Text(
                text = "7:30 AM-10:30 AM",
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.caption
            )
        }
        IconButton(
            onClick = {
                currSelectedMeal.value = MealFilter.BREAKFAST
            },
            modifier = Modifier
                .align(CenterVertically)
                .padding(end = 16.dp, start = 100.dp),

            ) {
            if (currSelectedMeal.value == MealFilter.BREAKFAST) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_selected),
                    contentDescription = "Breakfast",
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_unselected),
                    contentDescription = "Breakfast",
                )
            }
        }
    }
    Spacer(
        modifier = Modifier
            .padding(start = 12.dp, end = 16.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(GrayZero, CircleShape)
    )
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                currSelectedMeal.value = MealFilter.LUNCH
            }
            .padding(top = 8.dp, bottom = 12.dp)
    ) {
        Column {
            Text(
                text = "Lunch",
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.h5
            )
            Text(
                text = "10:30 AM-4:00 PM",
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.caption
            )
        }
        IconButton(
            onClick = {
                currSelectedMeal.value = MealFilter.LUNCH
            },
            modifier = Modifier
                .align(CenterVertically)
                .padding(end = 16.dp, start = 100.dp),

            ) {
            if (currSelectedMeal.value == MealFilter.LUNCH) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_selected),
                    contentDescription = "Lunch",
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_unselected),
                    contentDescription = "Lunch",
                )
            }
        }

    }
    Spacer(
        modifier = Modifier
            .padding(start = 12.dp, end = 16.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(GrayZero, CircleShape)
    )
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                currSelectedMeal.value = MealFilter.DINNER
            }
            .padding(top = 8.dp, bottom = 12.dp)
    ) {
        Column {
            Text(
                text = "Dinner",
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.h5
            )
            Text(
                text = "5:00 PM-8:30 PM",
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.caption
            )
        }
        IconButton(
            onClick = {
                currSelectedMeal.value = MealFilter.DINNER
            },
            modifier = Modifier
                .align(CenterVertically)
                .padding(end = 16.dp, start = 100.dp),
        ) {
            if (currSelectedMeal.value == MealFilter.DINNER) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_selected),
                    contentDescription = "Dinner",
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_unselected),
                    contentDescription = "Dinner",
                )
            }
        }
    }
    Spacer(
        modifier = Modifier
            .padding(start = 12.dp, end = 16.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(GrayZero, CircleShape)
    )
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                currSelectedMeal.value = MealFilter.LATE_DINNER
            }
            .padding(top = 8.dp, bottom = 10.dp)
    ) {
        Column {
            Text(
                text = "Late Dinner",
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.h5
            )
            Text(
                text = "8:30 PM-10:30 PM",
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.caption
            )
        }
        IconButton(
            onClick = {
                currSelectedMeal.value = MealFilter.LATE_DINNER
            },
            modifier = Modifier
                .align(CenterVertically)
                .padding(end = 16.dp, start = 100.dp),
        ) {
            if (currSelectedMeal.value == MealFilter.LATE_DINNER) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_selected),
                    contentDescription = "Late Dinner",
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_unselected),
                    contentDescription = "Late Dinner",
                )
            }
        }
    }
    Button(
        onClick = {
            onSubmit(currSelectedMeal.value)
            hide()
        },
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .padding(top = 10.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = EateryBlue,
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Show Menu",
            style = EateryBlueTypography.h5,
            modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
        )
    }

    TextButton(
        onClick = {

            hide()
        },
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Reset",
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            color = Color.Black
        )
    }
}
