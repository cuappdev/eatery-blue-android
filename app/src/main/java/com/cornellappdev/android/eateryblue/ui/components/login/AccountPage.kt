package com.cornellappdev.android.eateryblue.ui.components.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.AccountType
import com.cornellappdev.android.eateryblue.ui.components.general.SearchBar
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.viewmodels.LoginViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AccountPage(
    accountState: LoginViewModel.State.Account,
    loginViewModel: LoginViewModel,
) {
    var filterText by remember { mutableStateOf("") }
    Column(modifier = Modifier.zIndex(1f)) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Account",
                style = EateryBlueTypography.h3,
                color = EateryBlue
            )
            Column(
                modifier = Modifier.height(241.dp)
            ) {
                Text(
                    text = "Meal Plan",
                    style = EateryBlueTypography.h4
                )
                MealPlanRow(
                    accountName = "Meal Swipes",
                    accountType = AccountType.UNLIMITED,
                    loginViewModel = loginViewModel
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GrayZero, CircleShape)
                )
                MealPlanRow(
                    accountName = "Big Red Bucks",
                    accountType = AccountType.BRBS,
                    loginViewModel = loginViewModel
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GrayZero, CircleShape)
                )
                MealPlanRow(
                    accountName = "City Bucks",
                    accountType = AccountType.CITYBUCKS,
                    loginViewModel = loginViewModel
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GrayZero, CircleShape)
                )
                MealPlanRow(
                    accountName = "Laundry",
                    accountType = AccountType.LAUNDRY,
                    loginViewModel = loginViewModel
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(GrayZero)
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Big Red Bucks",
                        style = EateryBlueTypography.h4,

                        )
                }
                IconButton(
                    onClick = {
                    },
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                        .background(color = GrayZero, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_brbs),
                        contentDescription = "Cange Account Type",
                        modifier = Modifier
                            .size(26.dp)
                    )
                }
            }
            SearchBar(
                searchText = filterText,
                onSearchTextChange = { filterText = it },
                modifier = Modifier.padding(bottom = 12.dp),
                placeholderText = "Search the menu...",
                onCancelClicked = {
                    filterText = ""
                }
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(GrayZero, CircleShape)
            )
            Text(
                text = "Past 30 Days",
                modifier = Modifier.padding(vertical = 12.dp),
                style = EateryBlueTypography.h5
            )
            LazyColumn {
                items(
                    loginViewModel.getTransactionsOfType(
                        accountState.accountFilter,
                        filterText
                    )!!
                ) { it ->
                    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                    val outputFormatter = DateTimeFormatter.ofPattern("h:mm a EEEE, MMMM d")
                    val dateTime = LocalDateTime.parse(it.date, inputFormatter)

                    Row(
                        modifier = Modifier.height(64.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${it.location}", style = EateryBlueTypography.button)
                            Text(
                                text = outputFormatter.format(dateTime),
                                style = EateryBlueTypography.subtitle2
                            )
                        }
                        Text(
                            modifier = Modifier.weight(0.2f),
                            textAlign = TextAlign.Right,
                            text = "$${"%.2f".format(it.amount)}",
                            style = EateryBlueTypography.button
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(GrayZero, CircleShape)
                    )
                }
            }

        }
    }
}

@Composable
fun MealPlanRow(accountName: String, accountType: AccountType, loginViewModel: LoginViewModel) {

    Row(
        modifier = Modifier.height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = accountName,
            style = EateryBlueTypography.button,
        )
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Right,
            text = "$${
                "%.2f".format(loginViewModel.checkAccount(accountType)!!.balance)
            }",
            style = EateryBlueTypography.button,
        )
    }
}
