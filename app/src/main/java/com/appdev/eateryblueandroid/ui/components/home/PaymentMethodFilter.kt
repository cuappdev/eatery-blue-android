package com.appdev.eateryblueandroid.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.CircularBackgroundIcon
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun PaymentMethodFilter(
    selectedFilter: MutableState<List<String>>,
    toggleFilter: (selected: String) -> Unit,
    saveFilter: (filter: List<String>) -> Unit,
    hide: () -> Unit
) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Payment Methods",
                textStyle = TextStyle.HEADER_H3,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            CircularBackgroundIcon(
                icon = painterResource(id = R.drawable.ic_x),
                clickable = true,
                onTap = {
                    saveFilter(selectedFilter.value.filter { it != "Payment Options" })
                    hide()
                },
                iconWidth = 12.dp,
                iconHeight = 12.dp,
                backgroundSize = 40.dp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column(
                modifier = Modifier.width(90.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularBackgroundIcon(
                    icon = painterResource(id = R.drawable.ic_payment_swipes),
                    iconTint = colorResource(id = if (selectedFilter.value.contains("Meal swipes")) R.color.white else R.color.gray05),
                    backgroundTint = colorResource(id = if (selectedFilter.value.contains("Meal swipes")) R.color.eateryBlue else R.color.gray00),
                    onTap = { toggleFilter("Meal swipes") },
                    clickable = true,
                    iconWidth = 36.dp,
                    iconHeight = 36.dp,
                    backgroundSize = 64.dp
                )
                Text(
                    text = "Meal Swipes",
                    textStyle = TextStyle.LABEL_SEMIBOLD,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
            Column(
                modifier = Modifier.width(90.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularBackgroundIcon(
                    icon = painterResource(id = R.drawable.ic_payment_brbs),
                    iconTint = colorResource(id = if (selectedFilter.value.contains("BRBs")) R.color.white else R.color.gray05),
                    backgroundTint = colorResource(id = if (selectedFilter.value.contains("BRBs")) R.color.red else R.color.gray00),
                    onTap = { toggleFilter("BRBs") },
                    clickable = true,
                    iconWidth = 36.dp,
                    iconHeight = 36.dp,
                    backgroundSize = 64.dp
                )
                Text(
                    text = "BRBs",
                    textStyle = TextStyle.LABEL_SEMIBOLD,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
            Column(
                modifier = Modifier.width(90.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularBackgroundIcon(
                    icon = painterResource(id = R.drawable.ic_payment_cash),
                    iconTint = colorResource(id = if (selectedFilter.value.contains("Cash or credit")) R.color.white else R.color.gray05),
                    backgroundTint = colorResource(id = if (selectedFilter.value.contains("Cash or credit")) R.color.green else R.color.gray00),
                    onTap = { toggleFilter("Cash or credit") },
                    clickable = true,
                    iconWidth = 36.dp,
                    iconHeight = 36.dp,
                    backgroundSize = 64.dp
                )
                Text(
                    text = "Cash or credit",
                    textStyle = TextStyle.LABEL_SEMIBOLD,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
        }
        Surface(shape = RoundedCornerShape(24.dp), modifier = Modifier.padding(top = 12.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.eateryBlue))
                    .clickable {
                        saveFilter(selectedFilter.value.filter { it != "Payment Options" })
                        hide()
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Show results",
                    textStyle = TextStyle.HEADER_H4,
                    color = colorResource(id = R.color.white),
                    modifier = Modifier.padding(top = 13.dp, bottom = 13.dp)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clickable {
                    val paymentOptions = listOf("Meal swipes", "BRBs", "Cash or credit")
                    saveFilter(selectedFilter.value
                        .filter { !paymentOptions.contains(it) }
                        .filter { it != "Payment Options" }
                            + paymentOptions)
                    hide()
                }
        ) {
            Text(
                text = "Reset",
                textStyle = TextStyle.BODY_SEMIBOLD,
                color = colorResource(id = R.color.black)
            )
        }
    }
}

@Composable
fun PaymentMethodOption(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 21.dp, bottom = 21.dp)
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { onSelect() }
    ) {
        Text(text = text, textStyle = TextStyle.HEADER_H4)
        if (selected) {
            CircularBackgroundIcon(
                icon = painterResource(R.drawable.ic_check),
                clickable = false,
                iconWidth = 13.dp,
                iconHeight = 13.dp,
                iconTint = colorResource(id = R.color.white),
                backgroundTint = colorResource(id = R.color.black),
                backgroundSize = 24.dp
            )
        } else {
            Column(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = colorResource(id = R.color.black),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .height(24.dp)
                    .width(24.dp)
            ) {}
        }
    }
}

@Composable
fun HorizontalSeparator() {
    return Column(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(colorResource(R.color.gray00))
    ) {

    }
}