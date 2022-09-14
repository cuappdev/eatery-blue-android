package com.appdev.eateryblueandroid.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Account
import com.appdev.eateryblueandroid.models.AccountType
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.util.Constants.mealPlanNameMap
import com.appdev.eateryblueandroid.util.Constants.mealPlanTypes
import com.appdev.eateryblueandroid.util.Constants.semesterlyMealPlans

@Composable
fun AccountSummaries(accounts: List<Account>?) {
    val swipes = accounts?.find { mealPlanTypes.contains(it.type) }?.balance
    // Always want to display BRB balance, even if the account does not exist
    val brbs = accounts?.find { it.type == AccountType.BRBS }?.balance ?: 0.0
    val citybucks = accounts?.find { it.type == AccountType.CITYBUCKS }?.balance
    val laundry = accounts?.find { it.type == AccountType.LAUNDRY }?.balance
    val mealPlanAccount: Account? = accounts?.find { mealPlanTypes.contains(it.type) }
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        if (mealPlanAccount != null) {
            Text(
                text = mealPlanNameMap[mealPlanAccount.type] + " Meal Plan",
                textStyle = TextStyle.HEADER_H3,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        if (mealPlanAccount != null) {
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
                    if (mealPlanAccount.type != AccountType.UNLIMITED) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("%.0f".format(swipes))
                                }

                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorResource(R.color.gray05)
                                    )
                                ) {
                                    append(
                                        " remaining this " + if (semesterlyMealPlans.contains(
                                                mealPlanAccount.type
                                            )
                                        ) "semester" else "week"
                                    )
                                }
                            }
                        )
                    } else {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Unlimited ")
                                }

                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorResource(R.color.gray05)
                                    )
                                ) {
                                    append("swipes")
                                }
                            }
                        )
                    }
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