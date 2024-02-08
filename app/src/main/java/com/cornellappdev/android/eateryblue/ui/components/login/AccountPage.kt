package com.cornellappdev.android.eateryblue.ui.components.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.data.models.AccountType
import com.cornellappdev.android.eateryblue.ui.components.general.SearchBar
import com.cornellappdev.android.eateryblue.ui.components.home.BottomSheetContent
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountPage(
    accountState: LoginViewModel.State.Account,
    loginViewModel: LoginViewModel,
) {
    val context = LocalContext.current
    var filterText by remember { mutableStateOf("") }
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    var sheetContent by remember { mutableStateOf(BottomSheetContent.ACCOUNT_TYPE) }
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            when (sheetContent) {
                BottomSheetContent.ACCOUNT_TYPE -> {
                    AccountTypesAvailable(selectedPaymentMethod = listOf(
                        AccountType.MEALSWIPES,
                        AccountType.BRBS,
                        AccountType.CITYBUCKS,
                        AccountType.LAUNDRY
                    ),
                        accountState = accountState,
                        hide = {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                        }) {
                        loginViewModel.updateAccountFilter(it)
                    }
                }

                else -> {}
            }
        },
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        sheetElevation = 8.dp
    ) {
        Column(modifier = Modifier.zIndex(1f)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
//                Text(
//                    text = "Account",
//                    style = EateryBlueTypography.h3,
//                    color = EateryBlue
//                )
                Column(
                ) {
                    Text(
                        text = "Meal Plan",
                        style = EateryBlueTypography.h4,
                        modifier = Modifier.padding(top = 16.dp)
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
                            text = when (accountState.accountFilter.name) {
                                "MEALSWIPES" -> "Meal Swipes"
                                "BRBS" -> "Big Red Bucks"
                                "LAUNDRY" -> "Laundry"
                                "CITYBUCKS" -> "City Bucks"
                                else -> "Account Type"
                            },
                            style = EateryBlueTypography.h4,

                            )
                    }
                    IconButton(
                        onClick = {
                            sheetContent = BottomSheetContent.ACCOUNT_TYPE
                            coroutineScope.launch {
                                modalBottomSheetState.show()
                            }
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
                    placeholderText = "Search for transactions...",
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
                        val inputFormatter =
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                        val outputFormatter = DateTimeFormatter.ofPattern("h:mm a · EEEE, MMMM d")
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


@Composable
fun AccountTypesAvailable(
    selectedPaymentMethod: List<AccountType>,
    accountState: LoginViewModel.State.Account,
    hide: () -> Unit,
    onSubmit: (AccountType) -> Unit
) {
    var selected by remember { mutableStateOf(accountState.accountFilter) }
    Column(
        modifier = Modifier
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Payment Methods",
                style = EateryBlueTypography.h4,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            IconButton(
                onClick = {
                    hide()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(color = GrayZero, shape = CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
            }
        }
        Column {
            selectedPaymentMethod.forEachIndexed { index, account ->
                var select = when (selected) {
                    account -> true
                    else -> false
                }
                Row(modifier = Modifier
                    .height(63.dp)
                    .fillMaxWidth()
                    .selectable(
                        selected = (select),
                        onClick = { selected = account }
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = when (account.name) {
                            "MEALSWIPES" -> "Meal Swipes"
                            "BRBS" -> "Big Red Bucks"
                            "LAUNDRY" -> "Laundry"
                            "CITYBUCKS" -> "City Bucks"
                            else -> "Account Type"
                        },
                        style = EateryBlueTypography.h5,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    IconToggleButton(
                        checked = (select),
                        onCheckedChange = { selected = account },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            imageVector = ImageVector.vectorResource(
                                id = if (select) R.drawable.ic_selected else R.drawable.ic_unselected
                            ),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
                if (index != selectedPaymentMethod.size) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(GrayZero, CircleShape)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
            Button(
                onClick = {
                    onSubmit(selected)
                    hide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),

                shape = RoundedCornerShape(100),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = EateryBlue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 6.dp),
                    text = "Show transactions",
                    style = EateryBlueTypography.h5,
                    color = Color.White
                )
            }

        }
    }
}
