package com.appdev.eateryblueandroid.ui.components.profile

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
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.ui.components.core.CircularBackgroundIcon
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun PaymentMethodSelector(
    selectedFilter: MutableState<AccountType>,
    toggleFilter: (filter: AccountType) -> Unit,
    saveFilter: (filter: AccountType) -> Unit,
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
                onTap = { hide() },
                iconWidth = 12.dp,
                iconHeight = 12.dp,
                backgroundSize = 40.dp
            )
        }

        PaymentMethodOption(
            text = "Meal Swipes",
            selected = selectedFilter.value == AccountType.MEALSWIPES,
            onSelect = { toggleFilter(AccountType.MEALSWIPES) }

        )
        HorizontalSeparator()
        PaymentMethodOption(
            text = "Big Red Bucks",
            selected = selectedFilter.value == AccountType.BRBS,
            onSelect = { toggleFilter(AccountType.BRBS) }
        )
        HorizontalSeparator()
        PaymentMethodOption(
            text = "City Bucks",
            selected = selectedFilter.value == AccountType.CITYBUCKS,
            onSelect = { toggleFilter(AccountType.CITYBUCKS) }
        )
        HorizontalSeparator()
        PaymentMethodOption(
            text = "Laundry",
            selected = selectedFilter.value == AccountType.LAUNDRY,
            onSelect = { toggleFilter(AccountType.LAUNDRY) }
        )
        Surface(shape = RoundedCornerShape(24.dp), modifier = Modifier.padding(top = 12.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.eateryBlue))
                    .clickable {
                        saveFilter(selectedFilter.value)
                        hide()
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Show transactions",
                    textStyle = TextStyle.HEADER_H4,
                    color = colorResource(id = R.color.white),
                    modifier = Modifier.padding(top = 13.dp, bottom = 13.dp)
                )
            }
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