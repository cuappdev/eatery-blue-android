package com.cornellappdev.android.eateryblue.ui.components.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero


@Composable
fun FilterRow(
    currentFiltersSelected: List<Filter>,
    onPaymentMethodsClicked: () -> Unit,
    onFilterClicked: (Filter) -> Unit
) {
    val paymentMethodFilters = currentFiltersSelected.filter { filter ->
        filter in setOf(Filter.BRB, Filter.CASH, Filter.SWIPES)
    }

    val paymentMethodFilterText: String =
        if (paymentMethodFilters.isEmpty()) "Payment Methods" else paymentMethodFilters.joinToString {
            it.text
        }

    val buttonPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Spacer(modifier = Modifier.width(10.dp))
        }

        item {
            Button(
                onClick = {
                    onFilterClicked(Filter.UNDER_10)
                },
                contentPadding = buttonPadding,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentFiltersSelected.contains(
                            Filter.UNDER_10
                        )
                    ) Color.Black else GrayZero,
                    contentColor = if (currentFiltersSelected.contains(
                            Filter.UNDER_10
                        )
                    ) Color.White else Color.Black
                )
            ) {
                Text(
                    Filter.UNDER_10.text,
                )
            }
        }

        item {
            Button(
                onClick = {
                    onPaymentMethodsClicked()
                },
                contentPadding = buttonPadding,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (paymentMethodFilters.isNotEmpty()) Color.Black else GrayZero,
                    contentColor = if (paymentMethodFilters.isNotEmpty()) Color.White else Color.Black
                )
            ) {
                Text(
                    paymentMethodFilterText,
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
        }

        item {
            Button(
                onClick = {
                    onFilterClicked(Filter.FAVORITES)
                },
                contentPadding = buttonPadding,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentFiltersSelected.contains(
                            Filter.FAVORITES
                        )
                    ) Color.Black else GrayZero,
                    contentColor = if (currentFiltersSelected.contains(
                            Filter.FAVORITES
                        )
                    ) Color.White else Color.Black
                )
            ) {
                Text(
                    Filter.FAVORITES.text,
                )
            }
        }

        item {
            Button(
                onClick = {
                    onFilterClicked(Filter.NORTH)
                },
                contentPadding = buttonPadding,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentFiltersSelected.contains(
                            Filter.NORTH
                        )
                    ) Color.Black else GrayZero,
                    contentColor = if (currentFiltersSelected.contains(
                            Filter.NORTH
                        )
                    ) Color.White else Color.Black
                )
            ) {
                Text(
                    Filter.NORTH.text,
                )
            }
        }

        item {
            Button(
                onClick = {
                    onFilterClicked(Filter.WEST)
                },
                contentPadding = buttonPadding,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentFiltersSelected.contains(
                            Filter.WEST
                        )
                    ) Color.Black else GrayZero,
                    contentColor = if (currentFiltersSelected.contains(
                            Filter.WEST
                        )
                    ) Color.White else Color.Black
                )
            ) {
                Text(
                    Filter.WEST.text,
                )
            }
        }

        item {
            Button(
                onClick = {
                    onFilterClicked(Filter.CENTRAL)
                },
                contentPadding = buttonPadding,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentFiltersSelected.contains(
                            Filter.CENTRAL
                        )
                    ) Color.Black else GrayZero,
                    contentColor = if (currentFiltersSelected.contains(
                            Filter.CENTRAL
                        )
                    ) Color.White else Color.Black
                )
            ) {
                Text(
                    Filter.CENTRAL.text,
                )
            }
        }

        item {
            Spacer(Modifier.width(16.dp))
        }
    }
}

@Composable
fun FilterRowUpcoming(
    currentFiltersSelected: List<Filter>,
    onMealsClicked: () -> Unit,
    onFilterClicked: (Filter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val mealFilters = currentFiltersSelected.filter { filter ->
        filter in setOf(Filter.BREAKFAST, Filter.LUNCH, Filter.DINNER)
    }
    val mealsFilterText: String =
        if (mealFilters.isEmpty()) "Breakfast" else mealFilters.joinToString {
            it.text
        }

    val buttonPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)

    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Button(
                onClick = {
                    onMealsClicked()
                },
                contentPadding = buttonPadding,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (mealFilters.isNotEmpty()) Color.Black else GrayZero,
                    contentColor = if (mealFilters.isNotEmpty()) Color.White else Color.Black
                )
            ) {
                Text(
                    mealsFilterText,
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
        }


        item {
            Button(
                onClick = {
                    onFilterClicked(Filter.NORTH)
                },
                contentPadding = buttonPadding,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentFiltersSelected.contains(
                            Filter.NORTH
                        )
                    ) Color.Black
                    else GrayZero,
                    contentColor = if (currentFiltersSelected.contains(
                            Filter.NORTH
                        )
                    ) Color.White else Color.Black
                )
            ) {
                Text(
                    Filter.NORTH.text,
                )
            }
        }

        item {
            Button(
                onClick = {
                    onFilterClicked(Filter.WEST)
                },
                contentPadding = buttonPadding,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentFiltersSelected.contains(
                            Filter.WEST
                        )
                    ) Color.Black else GrayZero,
                    contentColor = if (currentFiltersSelected.contains(
                            Filter.WEST
                        )
                    ) Color.White else Color.Black
                )
            ) {
                Text(
                    Filter.WEST.text,
                )
            }
        }

        item {
            Button(
                onClick = {
                    onFilterClicked(Filter.CENTRAL)
                },
                contentPadding = buttonPadding,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentFiltersSelected.contains(
                            Filter.CENTRAL
                        )
                    ) Color.Black else GrayZero,
                    contentColor = if (currentFiltersSelected.contains(
                            Filter.CENTRAL
                        )
                    ) Color.White else Color.Black
                )
            ) {
                Text(
                    Filter.CENTRAL.text,
                )
            }
        }

        item {
            Spacer(Modifier.width(16.dp))
        }
    }
}
