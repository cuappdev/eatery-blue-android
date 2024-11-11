package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.theme.colorInterp


@Composable
fun FilterRow(
    filters: List<Filter>,
    currentFiltersSelected: List<Filter>,
    onFilterClicked: (Filter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(filters) { filter ->
            FilterButton(
                onFilterClicked = {
                    onFilterClicked(filter)
                }, selected = filter in currentFiltersSelected,
                text = filter.text
            )
        }
    }
}

@Composable
fun CompareFilterRow(
    currentFiltersSelected: List<Filter>,
    onPaymentMethodsClicked: () -> Unit,
    onFilterClicked: (Filter) -> Unit,
    showPaymentFilter: Boolean = true
) {
    val paymentMethodFilters = currentFiltersSelected.filter { filter ->
        filter in setOf(Filter.FromEatery.BRB, Filter.FromEatery.Cash, Filter.FromEatery.Swipes)
    }

    val paymentMethodFilterText: String =
        if (paymentMethodFilters.isEmpty()) "Payment Methods" else paymentMethodFilters.joinToString {
            it.text
        }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Spacer(modifier = Modifier.width(10.dp))
        }

        item {
            FilterButton(
                onFilterClicked = {
                    onFilterClicked(Filter.CustomFilter.Selected)
                },
                selected = currentFiltersSelected.contains(Filter.CustomFilter.Selected),
                text = Filter.CustomFilter.Selected.text
            )
        }
        item {
            FilterButton(
                onFilterClicked = {
                    onFilterClicked(Filter.FromEatery.North)
                },
                selected = currentFiltersSelected.contains(Filter.FromEatery.North),
                text = Filter.FromEatery.North.text
            )
        }

        item {
            FilterButton(
                onFilterClicked = {
                    onFilterClicked(Filter.FromEatery.West)
                },
                selected = currentFiltersSelected.contains(Filter.FromEatery.West),
                text = Filter.FromEatery.West.text
            )
        }

        item {
            FilterButton(
                onFilterClicked = {
                    onFilterClicked(Filter.FromEatery.Central)
                },
                selected = currentFiltersSelected.contains(Filter.FromEatery.Central),
                text = Filter.FromEatery.Central.text
            )
        }

        item {
            FilterButton(
                onFilterClicked = {
                    onFilterClicked(Filter.FromEatery.Under10)
                },
                selected = currentFiltersSelected.contains(Filter.FromEatery.Under10),
                text = Filter.FromEatery.Under10.text
            )
        }

        if (showPaymentFilter) {
            item {
                FilterButton(
                    onFilterClicked = onPaymentMethodsClicked,
                    selected = paymentMethodFilters.isNotEmpty(),
                    text = paymentMethodFilterText,
                    icon = Icons.Default.ExpandMore
                )
            }
        }

        item {
            FilterButton(
                onFilterClicked = {
                    onFilterClicked(Filter.RequiresFavoriteEateries.Favorites)
                },
                selected = currentFiltersSelected.contains(Filter.RequiresFavoriteEateries.Favorites),
                text = Filter.RequiresFavoriteEateries.Favorites.text
            )
        }

        item {
            Spacer(Modifier.width(16.dp))
        }
    }
}

/**
 * One filter button.
 */
@Composable
fun FilterButton(
    onFilterClicked: () -> Unit,
    selected: Boolean,
    text: String,
    icon: ImageVector? = null
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 0f else 1f,
        label = "Button Color",
        animationSpec = tween(150)
    )
    val background = colorInterp(progress, Color.Black, GrayZero)
    val contentColor = colorInterp(progress, Color.White, Color.Black)

    Button(
        onClick = onFilterClicked,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = background,
            contentColor = contentColor
        )
    ) {
        Text(text)
        if (icon != null) {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Icon(
                Icons.Default.ExpandMore,
                contentDescription = "Favorite",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }
}

@Composable
fun FilterRowUpcoming(
    mealFilter: MealFilter,
    selectedFilters: List<Filter>,
    onMealsClicked: () -> Unit,
    onFilterClicked: (Filter) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterButton(
                onFilterClicked = onMealsClicked,
                selected = true,
                text = when (mealFilter) {
                    MealFilter.LATE_DINNER -> "Late Dinner"
                    else -> mealFilter.text.first()
                },
                icon = Icons.Default.ExpandMore
            )
        }

        item {
            FilterButton(
                onFilterClicked = { onFilterClicked(Filter.FromEatery.North) },
                selected = selectedFilters.contains(Filter.FromEatery.North),
                text = Filter.FromEatery.North.text
            )
        }

        item {
            FilterButton(
                onFilterClicked = { onFilterClicked(Filter.FromEatery.West) },
                selected = selectedFilters.contains(Filter.FromEatery.West),
                text = Filter.FromEatery.West.text
            )
        }

        item {
            FilterButton(
                onFilterClicked = { onFilterClicked(Filter.FromEatery.Central) },
                selected = selectedFilters.contains(Filter.FromEatery.Central),
                text = Filter.FromEatery.Central.text
            )
        }

        item {
            Spacer(Modifier.width(16.dp))
        }
    }
}
