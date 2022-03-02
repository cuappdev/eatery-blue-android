package com.appdev.eateryblueandroid.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Account
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun AccountSummaries(accounts: List<Account>?) {
    val swipes = accounts?.find { it.type == AccountType.MEALPLAN }?.balance
    // Always want to display BRB balance, even if the account does not exist
    val brbs = accounts?.find { it.type == AccountType.BRBS }?.balance ?: 0.0
    val citybucks = accounts?.find { it.type == AccountType.CITYBUCKS }?.balance
    val laundry = accounts?.find { it.type == AccountType.LAUNDRY }?.balance
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Meal Plan",
            textStyle = TextStyle.HEADER_H3,
            modifier = Modifier.padding(top = 12.dp)
        )
        if (swipes != null) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Meal Swipes",
                    textStyle = TextStyle.BODY_SEMIBOLD
                )
                Row {
                    Text(text = "$%.0f".format(swipes), textStyle = TextStyle.BODY_SEMIBOLD)
                    Text(
                        text = " remaining this semester",
                        textStyle = TextStyle.BODY_SEMIBOLD,
                        color = colorResource(id = R.color.gray05)
                    )
                }
            }
            LineSeparator()
        }
        Row(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Big Red Bucks",
                textStyle = TextStyle.BODY_SEMIBOLD
            )
            Text(text = "$%.2f".format(brbs), textStyle = TextStyle.BODY_SEMIBOLD)
        }
        LineSeparator()
        if (citybucks != null) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "City Bucks",
                    textStyle = TextStyle.BODY_SEMIBOLD
                )
                Text(text = "$%.2f".format(citybucks), textStyle = TextStyle.BODY_SEMIBOLD)
            }
            LineSeparator()
        }
        if (laundry != null) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Laundry",
                    textStyle = TextStyle.BODY_SEMIBOLD
                )
                Text(text = "$%.2f".format(laundry), textStyle = TextStyle.BODY_SEMIBOLD)
            }
        }
    }
}

@Composable
fun LineSeparator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(colorResource(id = R.color.gray01))
    ) {
    }
}