package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayThree
import com.cornellappdev.android.eatery.util.EateryPreview


@Composable
fun EateryMealTabs(selectedMealIndex: Int, onSelectMeal: (Int) -> Unit, meals: List<String>) {
    SecondaryTabRow(
        selectedTabIndex = selectedMealIndex,
        indicator = @Composable {
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(
                    selectedTabIndex = selectedMealIndex
                ), height = 1.dp, color = Color.Black
            )
        }
    ) {
        meals.mapIndexed { index, meal ->
            Tab(
                selected = index == selectedMealIndex,
                onClick = {
                    onSelectMeal(index)
                },
            ) {
                Text(
                    meal,
                    style = EateryBlueTypography.button.copy(
                        color =
                            if (index == selectedMealIndex) {
                                Color.Black
                            } else {
                                GrayThree
                            }
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun EateryMealTabsPreview() = EateryPreview {
    var selectedMealIndex by remember { mutableIntStateOf(0) }
    EateryMealTabs(
        selectedMealIndex,
        { selectedMealIndex = it },
        listOf("Breakfast", "Lunch", "Dinner")
    )
}