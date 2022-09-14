package com.appdev.eateryblueandroid.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.models.Transaction
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.util.Constants.mealPlanTypes
import com.appdev.eateryblueandroid.util.formatLocation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        if (accountType == AccountType.MEALSWIPES || mealPlanTypes.contains(accountType)) "%.0f".format(
            amount
        )
        else "$%.2f".format(amount)

    Row {
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(text)
                }
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.gray05)
                    )
                ) {
                    if (accountType == AccountType.MEALSWIPES || mealPlanTypes.contains(accountType))
                        append(if (amount > 1) " swipes" else " swipe")
                }
            }
        )
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