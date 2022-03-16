package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun EateryFilters(alreadySelected: List<String>, onFiltersChange: (updated: List<String>) -> Unit) {
    var selected by remember { mutableStateOf(listOf<String>()) }
    val campusLocations = listOf("North", "West", "Central")
    val options = mutableListOf(
        "Under 10 minutes",
        "Payment Methods",
        "Favorites",
    )
    selected = alreadySelected
    options.addAll(campusLocations)

    LazyRow(contentPadding = PaddingValues(10.dp, 0.dp)) {
        items(options) { item ->
            EateryFilter(item, selected.contains(item)) {
                when {
                    selected.contains(item) -> selected = selected.filter { it != item }
                    campusLocations.contains(item) -> {
                        selected = selected.filter { !campusLocations.contains(it) }
                        selected = selected + item
                    }
                    else -> selected = selected + item
                }
                onFiltersChange(selected)
            }
        }
    }
}

@Composable
fun EateryFilter(text: String, isSelected: Boolean, onClicked: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(start = 6.dp, end = 6.dp)
            .clickable {
                onClicked()
            }
    ) {
        Row(
            modifier = Modifier
                .background(colorResource(id = if (isSelected) R.color.black else R.color.gray00))
                .padding(9.dp, 5.dp)
        ) {
            Text(
                text = text,
                textStyle = TextStyle.BODY_MEDIUM,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}