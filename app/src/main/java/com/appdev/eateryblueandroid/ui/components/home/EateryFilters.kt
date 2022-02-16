package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun EateryFilters() {
    val options = listOf(
        "Under 10 minutes",
        "Payment Methods",
        "Favorites",
        "North",
        "West",
        "Central"
    )

    LazyRow(contentPadding = PaddingValues(10.dp, 0.dp)) {
        items(options) { item ->
            EateryFilter(item)
        }
    }
}
@Preview
@Composable
fun PreviewEateryFilters() {
    EateryFilters()
}

@Composable
fun EateryFilter(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(start = 6.dp, end = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .background(colorResource(id = R.color.gray00))
                .padding(9.dp, 5.dp)
        ) {
            Text(
                text = text,
                textStyle = TextStyle.BODY_MEDIUM
            )
        }
    }
}
@Preview
@Composable
fun PreviewEateryFiltersText() {
    EateryFilter("Hello")
}