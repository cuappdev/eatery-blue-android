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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.general.MealFilter
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview

/**
 * The pop-up that shows up when users want to pick a different meal as the filter in the upcoming
 * menu screen.
 */
@Composable
fun MealBottomSheet(
    isVisible: Boolean,
    selectedMeal: MealFilter,
    onSubmit: (MealFilter) -> Unit,
    hide: () -> Unit
) {
    val currSelectedMeal = remember { mutableStateOf(selectedMeal) }
    if (!isVisible) currSelectedMeal.value = selectedMeal
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.menus_title),
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
                    .background(color = currentColors.backgroundDefault, shape = CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.close),
                    tint = currentColors.textPrimary
                )
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
                text = MealFilter.BREAKFAST.displayName,
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.h5
            )
            Text(
                text = stringResource(R.string.meal_time_breakfast),
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
                    contentDescription = stringResource(R.string.a11y_meal_selected_breakfast),
                    tint = Color.Unspecified
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_unselected),
                    contentDescription = stringResource(R.string.a11y_meal_select_breakfast),
                    tint = Color.Unspecified
                )
            }
        }
    }
    Spacer(
        modifier = Modifier
            .padding(start = 12.dp, end = 16.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(currentColors.backgroundDefault, CircleShape)
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                currSelectedMeal.value = MealFilter.LUNCH
            }
            .padding(top = 8.dp, bottom = 12.dp)
    ) {
        Column {
            Text(
                text = MealFilter.LUNCH.displayName,
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.h5
            )
            Text(
                text = stringResource(R.string.meal_time_lunch),
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
                    contentDescription = stringResource(R.string.a11y_meal_selected_lunch),
                    tint = Color.Unspecified
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_unselected),
                    contentDescription = stringResource(R.string.a11y_meal_select_lunch),
                    tint = Color.Unspecified
                )
            }
        }

    }
    Spacer(
        modifier = Modifier
            .padding(start = 12.dp, end = 16.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(currentColors.backgroundDefault, CircleShape)
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                currSelectedMeal.value = MealFilter.DINNER
            }
            .padding(top = 8.dp, bottom = 12.dp)
    ) {
        Column {
            Text(
                text = MealFilter.DINNER.displayName,
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.h5
            )
            Text(
                text = stringResource(R.string.meal_time_dinner),
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
                    contentDescription = stringResource(R.string.a11y_meal_selected_dinner),
                    tint = Color.Unspecified
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_unselected),
                    contentDescription = stringResource(R.string.a11y_meal_select_dinner),
                    tint = Color.Unspecified
                )
            }
        }
    }
    Spacer(
        modifier = Modifier
            .padding(start = 12.dp, end = 16.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(currentColors.backgroundDefault, CircleShape)
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                currSelectedMeal.value = MealFilter.LATE_DINNER
            }
            .padding(top = 8.dp, bottom = 10.dp)
    ) {
        Column {
            Text(
                text = MealFilter.LATE_DINNER.displayName,
                modifier = Modifier.padding(start = 16.dp),
                style = EateryBlueTypography.h5
            )
            Text(
                text = stringResource(R.string.meal_time_late_dinner),
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
                    contentDescription = stringResource(R.string.a11y_meal_selected_late_dinner),
                    tint = Color.Unspecified
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_unselected),
                    contentDescription = stringResource(R.string.a11y_meal_select_late_dinner),
                    tint = Color.Unspecified
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
            containerColor = currentColors.accentPrimary,
            contentColor = currentColors.backgroundDefault
        )
    ) {
        Text(
            text = stringResource(R.string.show_menu),
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
            text = stringResource(R.string.reset),
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            color = currentColors.textPrimary
        )
    }
}

@DualModePreview
@Composable
private fun MealBottomSheetPreview() = EateryPreview {
    MealBottomSheet(
        isVisible = true,
        selectedMeal = MealFilter.LUNCH,
        onSubmit = {},
        hide = {}
    )
}

