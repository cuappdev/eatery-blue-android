package com.cornellappdev.android.eatery.ui.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview


@Composable
fun EateryMealTabs(selectedMealIndex: Int, onSelectMeal: (Int) -> Unit, meals: List<String>) {
    if (meals.isEmpty()) return
    val safeSelectedIndex = selectedMealIndex.coerceIn(0, meals.lastIndex)

    PrimaryTabRow(
        selectedTabIndex = safeSelectedIndex,
        containerColor = currentColors.backgroundDefault,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(safeSelectedIndex),
                color = currentColors.textPrimary,
                height = 1.dp,
            )
        },
        divider = @Composable { HorizontalDivider(color = currentColors.borderDefault) }
    ) {
        meals.mapIndexed { index, meal ->
            Tab(
                selected = index == safeSelectedIndex,
                onClick = {
                    onSelectMeal(index)
                },
                modifier = Modifier.background(color = currentColors.backgroundDefault)
            ) {
                Text(
                    meal,
                    style = EateryBlueTypography.button.copy(
                        color = if (index == safeSelectedIndex) currentColors.textPrimary
                        else currentColors.textSecondary
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                )
            }
        }
    }
}

@DualModePreview
@Composable
fun EateryMealTabsPreview() = EateryPreview {
    EateryMealTabs(0, {}, listOf("Breakfast", "Lunch", "Dinner"))
}
