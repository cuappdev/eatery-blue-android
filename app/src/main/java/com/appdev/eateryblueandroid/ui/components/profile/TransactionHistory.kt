package com.appdev.eateryblueandroid.ui.components.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.models.Transaction
import com.appdev.eateryblueandroid.models.TransactionType
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.appdev.eateryblueandroid.util.formatLocation
import java.util.*

@Composable
fun TransactionHistory(transaction: Transaction) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            Text(
                text = formatLocation(transaction.location),
                textStyle = TextStyle.BODY_SEMIBOLD
            )
            Text(
                text = formatDate(nullableDate = transaction.date),
                textStyle = TextStyle.LABEL_MEDIUM,
                color = colorResource(id = R.color.gray05)
            )
        }
        TransactionHistorySpendAmount(
            amount = transaction.amount ?: 0.00,
            accountType = transaction.accountType ?: AccountType.OTHER
        )
    }
}

@Composable
fun TransactionHistorySpendAmount(
    amount: Double,
    accountType: AccountType
) {

    val text =
        if (accountType == AccountType.MEALPLAN) "%.0f".format(amount)
        else "$%.2f".format(amount)

    Row {
        Text(
            text = text,
            textStyle = TextStyle.BODY_SEMIBOLD
        )
        if (accountType == AccountType.MEALPLAN) {
            Text(
                text = if (amount > 1) "swipes" else "swipe",
                color = colorResource(id = R.color.gray05),
                textStyle = TextStyle.BODY_SEMIBOLD,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }
}

internal fun formatDate(nullableDate: LocalDateTime?): String {
    return nullableDate?.let { date ->
        val lastWeek = LocalDateTime.now().minusDays(7)
        if (date.isAfter(lastWeek))
            date.format(DateTimeFormatter.ofPattern("h:mm a '·' EEEE',' MMM dd"))
        else
            date.format(DateTimeFormatter.ofPattern("h:mm a '·' MMM dd ',' y"))
    } ?: ""
}