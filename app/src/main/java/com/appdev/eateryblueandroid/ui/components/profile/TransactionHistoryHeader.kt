package com.appdev.eateryblueandroid.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.ui.components.core.CircularBackgroundIcon
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun TransactionHistoryHeader(
    account: AccountType,
    showAccountSelector: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = accountToString(account),
            textStyle = TextStyle.HEADER_H3
        )
        CircularBackgroundIcon(
            icon = painterResource(id = R.drawable.ic_chevron_down),
            iconWidth = 15.dp,
            iconHeight = 15.dp,
            clickable = true,
            onTap = showAccountSelector
        )
    }
}

fun accountToString(account: AccountType): String {
    if (account == AccountType.MEALPLAN) {
        return "Meal Swipes"
    } else if (account == AccountType.CITYBUCKS) {
        return "City Bucks"
    } else if (account == AccountType.LAUNDRY) {
        return "Laundry"
    } else if (account == AccountType.BRBS) {
        return "Big Red Bucks"
    } else {
        return "Unknown"
    }
}