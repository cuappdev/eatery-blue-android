package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun EateryFilters(
    alreadySelected: List<String>,
    filterState: LazyListState,
    showBottomSheet : () -> Unit,
    onFiltersChange: (
        updated: List<String>
    ) -> Unit,
) {
    var selected by remember { mutableStateOf(listOf<String>()) }
    val campusLocations = listOf("North", "West", "Central")
    val paymentOptions = listOf("Meal swipes", "BRBs", "Cash or credit")
    val options = mutableListOf(
        "Under 10 minutes",
        "Payment Options",
        "Favorites",
    )
    selected = alreadySelected
    options.addAll(campusLocations)

    val paymentOptionsText = if (selected.containsAll(paymentOptions)) {
        "Payment Options"
    } else {
        val selectedPayments = selected.filter { paymentOptions.contains(it) }
        selectedPayments.joinToString(", ")
    }

    LazyRow(contentPadding = PaddingValues(8.dp, 0.dp), state = filterState) {
        items(options) { item ->
            val filterText = if (item == "Payment Options") paymentOptionsText else item
            val isSelected =
                if (item == "Payment Options" && filterText != "Payment Options")
                    true
                else
                    selected.contains(item)
            EateryFilter(
                text = filterText,
                isSelected = isSelected,
                chevronOn = (item == "Payment Options")
            ) {
                if (item == "Payment Options")
                    showBottomSheet()
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
fun EateryFilter(
    text: String,
    isSelected: Boolean,
    chevronOn: Boolean = false,
    onClicked: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(start = 8.dp)
            .height(34.dp),
        onClick = {
            onClicked()
        },
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        contentPadding = PaddingValues(
            start = 10.dp,
            end = 10.dp,
            top = 8.dp,
            bottom = 8.dp
        ),
        colors = buttonColors(backgroundColor = (colorResource(id = if (isSelected) R.color.black else R.color.gray00))),
    ) {
        Text(
            text = text,
            textStyle = TextStyle.BODY_MEDIUM,
            color = if (isSelected) Color.White else Color.Black
        )
        if (chevronOn) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_down),
                contentDescription = null,
                tint = colorResource(id = if (isSelected) R.color.white else R.color.black),
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(8.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}
