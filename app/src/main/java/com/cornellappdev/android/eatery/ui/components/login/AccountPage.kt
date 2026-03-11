package com.cornellappdev.android.eatery.ui.components.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.AccountBalances
import com.cornellappdev.android.eatery.data.models.Transaction
import com.cornellappdev.android.eatery.data.models.TransactionAccountType
import com.cornellappdev.android.eatery.data.models.TransactionType
import com.cornellappdev.android.eatery.ui.components.general.SearchBar
import com.cornellappdev.android.eatery.ui.components.home.BottomSheetContent
import com.cornellappdev.android.eatery.ui.theme.Black
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GrayFive
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.ui.theme.Green
import com.cornellappdev.android.eatery.ui.theme.Red
import com.cornellappdev.android.eatery.ui.viewmodels.TransactionWithFormattedDate
import com.cornellappdev.android.eatery.util.EateryPreview
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun AccountPage(
    accountFilter: TransactionAccountType,
    accountTypeBalance: AccountBalances,
    onSettingsClicked: () -> Unit,
    filteredTransactions: List<TransactionWithFormattedDate>,
    filterText: String,
    onQueryChanged: (String) -> Unit,
    updateAccountFilter: (TransactionAccountType) -> Unit
) {
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
                    AccountTypesSelector(
                        selectedPaymentMethod = listOf(
                            TransactionAccountType.MEAL_SWIPES,
                            TransactionAccountType.BRBS,
                            TransactionAccountType.CITY_BUCKS,
                            TransactionAccountType.LAUNDRY
                        ),
                        accountFilter = accountFilter,
                        hide = {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                        },
                        onSubmit = updateAccountFilter
                    )
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
        AccountPageContent(
            onSettingsClicked,
            accountTypeBalance,
            accountFilter,
            showBottomSheet = modalBottomSheetState::show,
            filterText,
            setFilterText = onQueryChanged,
            filteredTransactions,
            setSheetContent = { sheetContent = it },
        )

    }
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
private fun AccountPageContent(
    onSettingsClicked: () -> Unit,
    accountTypeBalance: AccountBalances,
    accountFilter: TransactionAccountType,
    showBottomSheet: suspend () -> Unit,
    filterText: String,
    setFilterText: (String) -> Unit,
    filteredTransactions: List<TransactionWithFormattedDate>,
    setSheetContent: (BottomSheetContent) -> Unit
) {
    val innerListState = rememberLazyListState()
    val isFirstVisible by remember { derivedStateOf { innerListState.firstVisibleItemIndex > 1 } }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AccountPageHeader(isFirstVisible, onSettingsClicked)
        LazyColumn(state = innerListState) {
            item {
                (Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Column {
                        Text(
                            text = "Meal Plan",
                            style = EateryBlueTypography.h4,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        accountTypeBalance.mealSwipes?.let {
                            AccountBalanceRow(
                                accountName = "Meal Swipes",
                                swipes = it
                            )
                        }
                        Divider(color = GrayZero, thickness = 1.dp)
                        accountTypeBalance.brbBalance?.let {
                            AccountBalanceRow(
                                accountName = "Big Red Bucks",
                                balance = it
                            )
                        }
                        Divider(color = GrayZero, thickness = 1.dp)
                        accountTypeBalance.cityBucksBalance?.let {
                            AccountBalanceRow(
                                accountName = "City Bucks",
                                balance = it
                            )
                        }
                        Divider(color = GrayZero, thickness = 1.dp)
                        accountTypeBalance.laundryBalance?.let {
                            AccountBalanceRow(
                                accountName = "Laundry",
                                balance = it
                            )
                        }
                    }
                })
            }

            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(GrayZero)
                )
            }

            stickyHeader {
                TransactionsHeader(
                    accountFilter,
                    setSheetContent,
                    showBottomSheet,
                    filterText,
                    setFilterText
                )
            }
            items(
                items = filteredTransactions,
                key = { it.transaction.date + it.transaction.location + it.transaction.amount }) {
                TransactionRow(
                    transaction = it.transaction,
                    formattedDate = it.formattedDate,
                    isMealSwipes = accountFilter == TransactionAccountType.MEAL_SWIPES
                )
            }
        }
    }
}

@Preview
@Composable
private fun AccountPagePreview() = EateryPreview {
    AccountPageContent(
        onSettingsClicked = {},
        accountTypeBalance = AccountBalances(
            brbBalance = 25.50,
            cityBucksBalance = 10.75,
            laundryBalance = 5.00,
            mealSwipes = 42
        ),
        accountFilter = TransactionAccountType.BRBS,
        showBottomSheet = {},
        filterText = "",
        setFilterText = {},
        filteredTransactions = listOf(
            TransactionWithFormattedDate(
                transaction = Transaction(
                    date = "2023-10-01T12:30:00.000Z",
                    location = "Cafe Jennie",
                    amount = 5.25,
                    transactionType = TransactionType.SPEND
                ),
                formattedDate = "12:30 PM · Sunday, October 1"
            ),
            TransactionWithFormattedDate(
                transaction = Transaction(
                    date = "2023-10-02T14:00:00.000Z",
                    location = "Morrison Dining",
                    amount = 15.00,
                    transactionType = TransactionType.DEPOSIT
                ),
                formattedDate = "2:00 PM · Monday, October 2"
            )
        ),
        setSheetContent = {}
    )
}

@Composable
private fun TransactionsHeader(
    accountFilter: TransactionAccountType,
    setSheetContent: (BottomSheetContent) -> Unit,
    showBottomSheet: suspend () -> Unit,
    filterText: String,
    setFilterText: ((String) -> Unit)
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (accountFilter) {
                        TransactionAccountType.MEAL_SWIPES -> "Meal Swipes"
                        TransactionAccountType.BRBS -> "Big Red Bucks"
                        TransactionAccountType.LAUNDRY -> "Laundry"
                        TransactionAccountType.CITY_BUCKS -> "City Bucks"
                    },
                    style = EateryBlueTypography.h4
                )
            }
            IconButton(
                onClick = {
                    setSheetContent(BottomSheetContent.ACCOUNT_TYPE)
                    coroutineScope.launch {
                        showBottomSheet()
                    }
                },
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                    .background(color = GrayZero, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Change Account Type",
                    modifier = Modifier
                        .size(26.dp)
                )
            }
        }
        SearchBar(
            searchText = filterText,
            onSearchTextChange = setFilterText,
            modifier = Modifier.padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
            placeholderText = "Search for transactions...",
            onCancelClicked = { setFilterText("") }
        )
        Divider(color = GrayZero, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
        Text(
            text = "Past 30 Days",
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            style = EateryBlueTypography.h5
        )
        Divider(color = GrayZero, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun AccountPageHeader(
    isFirstVisible: Boolean,
    onSettingsClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EateryBlue)
            .then(Modifier.statusBarsPadding())
            .padding(bottom = 7.dp),
    ) {
        AnimatedContent(
            targetState = isFirstVisible
        ) { isFirstVisible ->
            if (isFirstVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = EateryBlue)
                        .padding(top = 12.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = "Account",
                        color = Color.White,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    )

                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = onSettingsClicked
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = Icons.Outlined.Settings.name,
                            tint = Color.White
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = EateryBlue)
                        .padding(bottom = 7.dp),
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.End)
                            .size(32.dp),
                        onClick = { onSettingsClicked() }) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = Icons.Outlined.Settings.name,
                            tint = Color.White
                        )
                    }
                    Text(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 24.dp
                        ),
                        text = "Account",
                        color = Color.White,
                        style = EateryBlueTypography.h2
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction, formattedDate: String, isMealSwipes: Boolean) {
    Row(
        modifier = Modifier
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = transaction.location, style = EateryBlueTypography.button)
            Text(
                text = formattedDate,
                style = EateryBlueTypography.subtitle2,
                color = GrayFive
            )
        }
        val (amtString, amtColor) = when {
            transaction.transactionType == TransactionType.DEPOSIT -> {
                "+$%.2f".format(transaction.amount) to Green
            }

            transaction.amount.epsilonEqual(0.0) -> {
                "$0.00" to Black
            }

            else -> {
                val amt = if (isMealSwipes) {
                    val numSwipes = transaction.amount.toInt()
                    "-$numSwipes swipe" + (if (numSwipes > 1) "s" else "")
                } else {
                    "-$%.2f".format(transaction.amount)
                }
                amt to Red
            }
        }
        Text(
            text = amtString,
            modifier = Modifier.weight(0.2f),
            color = amtColor,
            textAlign = TextAlign.Right,
            style = EateryBlueTypography.button,
        )

    }
    Divider(color = GrayZero, thickness = 1.dp)
}


private fun Double.epsilonEqual(other: Double): Boolean {
    val epsilon = 0.00001
    return abs(this - other) < epsilon
}


@Composable
fun AccountBalanceRow(
    accountName: String,
    balance: Double,
) {
    AccountRow(accountName, "$" + "%.2f".format(balance))
}

@Composable
fun AccountBalanceRow(
    accountName: String,
    swipes: Int
) {
    AccountRow(accountName, "$swipes remaining")
}

@Composable
private fun AccountRow(
    accountName: String,
    text: String
) {
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
            text = text,
            style = EateryBlueTypography.button,
        )
    }
}


@Composable
fun AccountTypesSelector(
    selectedPaymentMethod: List<TransactionAccountType>,
    accountFilter: TransactionAccountType,
    hide: () -> Unit,
    onSubmit: (TransactionAccountType) -> Unit
) {
    var selected by remember { mutableStateOf(accountFilter) }
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
                onClick = hide,
                modifier = Modifier
                    .size(40.dp)
                    .background(color = GrayZero, shape = CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Black)
            }
        }
        Column {
            selectedPaymentMethod.forEachIndexed { index, account ->
                val accountIsSelected = selected == account
                Row(
                    modifier = Modifier
                        .height(63.dp)
                        .fillMaxWidth()
                        .selectable(
                            selected = accountIsSelected,
                            onClick = { selected = account }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = when (account) {
                            TransactionAccountType.MEAL_SWIPES -> "Meal Swipes"
                            TransactionAccountType.BRBS -> "Big Red Bucks"
                            TransactionAccountType.LAUNDRY -> "Laundry"
                            TransactionAccountType.CITY_BUCKS -> "City Bucks"
                        },
                        style = EateryBlueTypography.h5,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    IconToggleButton(
                        checked = accountIsSelected,
                        onCheckedChange = { selected = account },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            imageVector = ImageVector.vectorResource(
                                id = if (accountIsSelected) R.drawable.ic_selected else R.drawable.ic_unselected
                            ),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
                if (index != selectedPaymentMethod.lastIndex) {
                    Divider(
                        color = GrayZero,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
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
