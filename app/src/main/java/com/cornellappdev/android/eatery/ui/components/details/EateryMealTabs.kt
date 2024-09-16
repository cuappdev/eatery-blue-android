package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayThree
import com.cornellappdev.android.eatery.util.EateryPreview


@Composable
fun EateryMealTabs(selectedMealIndex: Int, onSelectMeal: (String) -> Unit, meals: List<String>) {
    TabRow(selectedTabIndex = selectedMealIndex, indicator = { tabPositions ->
        TabRowDefaults.Indicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedMealIndex]),
            color = Color.Black,
            height = 1.dp,
        )
    }) {
        meals.mapIndexed { index, meal ->
            Tab(
                selected = index == selectedMealIndex,
                onClick = {
                    onSelectMeal(meal)
                },
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            ) {
                Text(
                    meal, style = EateryBlueTypography.button.copy(
                        color =
                        if (index == selectedMealIndex) {
                            Color.Black
                        } else {
                            GrayThree
                        }
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun EateryMealTabsPreview() = EateryPreview {
    EateryMealTabs(0, {}, listOf("Breakfast", "Lunch", "Dinner"))
}