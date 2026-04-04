package com.cornellappdev.android.eatery.ui.components.details

import android.content.res.Configuration
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
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.EateryPreview


@Composable
fun EateryMealTabs(selectedMealIndex: Int, onSelectMeal: (Int) -> Unit, meals: List<String>) {
    TabRow(
        selectedTabIndex = selectedMealIndex,
        containerColor = currentColors.backgroundSurface,
        indicator = { tabPositions ->
        TabRowDefaults.Indicator(
            Modifier.tabIndicatorOffset(
                // We were having lots of users crash here, so avoiding any unsafe accesses for tab positions
                tabPositions.getOrNull(selectedMealIndex) ?: tabPositions.firstOrNull()
                ?: return@TabRow
            ),
            color = currentColors.textPrimary,
            height = 1.dp,
        )
    }) {
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
                            currentColors.textPrimary
                        } else {
                            currentColors.textSecondary
                        }
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun EateryMealTabsPreview() = EateryPreview {
    EateryMealTabs(0, {}, listOf("Breakfast", "Lunch", "Dinner"))
}