package com.cornellappdev.android.eatery.ui.components.login

import androidx.annotation.StringRes
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.data.models.AccountBalances
import com.cornellappdev.android.eatery.data.models.TransactionAccountType
import com.cornellappdev.android.eatery.ui.components.general.SearchBar
import com.cornellappdev.android.eatery.ui.components.home.BottomSheetContent
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.state.DisplayTransaction
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import kotlin.math.abs

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun AccountPage(
    accountFilter: TransactionAccountType,
    accountTypeBalance: AccountBalances,
    onSettingsClicked: () -> Unit,
    filteredTransactions: List<DisplayTransaction>,
    filterText: String,
    onQueryChanged: (String) -> Unit,
    updateAccountFilter: (TransactionAccountType) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var sheetContent by remember { mutableStateOf(BottomSheetContent.ACCOUNT_TYPE) }
    AccountPageContent(
        onSettingsClicked,
        accountTypeBalance,
        accountFilter,
        showBottomSheet = { showBottomSheet = true },
        filterText,
        setFilterText = onQueryChanged,
        filteredTransactions,
        setSheetContent = { sheetContent = it },
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = modalBottomSheetState,
            containerColor = currentColors.backgroundDefault,
            contentColor = currentColors.textPrimary,
            shape = RoundedCornerShape(
                bottomStart = 0.dp,
                bottomEnd = 0.dp,
                topStart = 12.dp,
                topEnd = 12.dp
            )
        ) {
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
                        hide = { showBottomSheet = false },
                        onSubmit = updateAccountFilter
                    )
                }

                else -> {}
            }
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
private fun AccountPageContent(
    onSettingsClicked: () -> Unit,
    accountTypeBalance: AccountBalances,
    accountFilter: TransactionAccountType,
    showBottomSheet: () -> Unit,
    filterText: String,
    setFilterText: (String) -> Unit,
    filteredTransactions: List<DisplayTransaction>,
    setSheetContent: (BottomSheetContent) -> Unit
) {
    val innerListState = rememberLazyListState()
    val isFirstVisible by remember { derivedStateOf { innerListState.firstVisibleItemIndex > 1 } }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(currentColors.backgroundDefault)
    ) {
        AccountPageHeader(isFirstVisible, onSettingsClicked)
        LazyColumn(state = innerListState) {
            item {
                (Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Column {
                        Text(
                            text = stringResource(R.string.account_meal_plan),
                            style = EateryBlueTypography.h4,
                            modifier = Modifier.padding(top = 16.dp),
                            color = currentColors.textPrimary
                        )
                        accountTypeBalance.mealSwipes?.let {
                            AccountBalanceRow(
                                accountName = stringResource(TransactionAccountType.MEAL_SWIPES.displayNameRes()),
                                swipes = it
                            )
                        }
                        HorizontalDivider(color = currentColors.accentPrimary, thickness = 1.dp)
                        accountTypeBalance.brbBalance?.let {
                            AccountBalanceRow(
                                accountName = stringResource(TransactionAccountType.BRBS.displayNameRes()),
                                balance = it
                            )
                        }
                        HorizontalDivider(color = currentColors.accentPrimary, thickness = 1.dp)
                        accountTypeBalance.cityBucksBalance?.let {
                            AccountBalanceRow(
                                accountName = stringResource(TransactionAccountType.CITY_BUCKS.displayNameRes()),
                                balance = it
                            )
                        }
                        HorizontalDivider(color = currentColors.accentPrimary, thickness = 1.dp)
                        accountTypeBalance.laundryBalance?.let {
                            AccountBalanceRow(
                                accountName = stringResource(TransactionAccountType.LAUNDRY.displayNameRes()),
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
                        .background(currentColors.accentPrimary)
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
                key = { it.id }) {
                TransactionRow(
                    transaction = it,
                    isMealSwipes = accountFilter == TransactionAccountType.MEAL_SWIPES
                )
            }
        }
    }
}

@Composable
private fun TransactionsHeader(
    accountFilter: TransactionAccountType,
    setSheetContent: (BottomSheetContent) -> Unit,
    showBottomSheet: () -> Unit,
    filterText: String,
    setFilterText: ((String) -> Unit)
) {
    Column(
        modifier = Modifier
            .background(color = currentColors.backgroundDefault)
    ) {
        Row(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(accountFilter.displayNameRes()),
                    style = EateryBlueTypography.h4,
                    color = currentColors.textPrimary
                )
            }
            IconButton(
                onClick = {
                    setSheetContent(BottomSheetContent.ACCOUNT_TYPE)
                    showBottomSheet()
                },
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                    .background(color = currentColors.backgroundDefault, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.a11y_change_account_type),
                    modifier = Modifier
                        .size(26.dp)
                )
            }
        }
        SearchBar(
            searchText = filterText,
            onSearchTextChange = setFilterText,
            modifier = Modifier.padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
            placeholderText = stringResource(R.string.account_search_transactions_placeholder),
            onCancelClicked = { setFilterText("") }
        )
        HorizontalDivider(
            color = currentColors.accentPrimary,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = stringResource(R.string.account_past_30_days),
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            style = EateryBlueTypography.h5,
            color = currentColors.textPrimary
        )
        HorizontalDivider(
            color = currentColors.accentPrimary,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
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
            .background(currentColors.backgroundSecondary)
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
                        .padding(top = 12.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.account_title),
                        color = currentColors.oppTextPrimary,
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
                            contentDescription = stringResource(R.string.a11y_settings),
                            tint = currentColors.oppTextPrimary
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            contentDescription = stringResource(R.string.a11y_settings),
                            tint = currentColors.oppTextPrimary
                        )
                    }
                    Text(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 24.dp
                        ),
                        text = stringResource(R.string.account_title),
                        color = currentColors.oppTextPrimary,
                        style = EateryBlueTypography.h2
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: DisplayTransaction, isMealSwipes: Boolean) {
    val amount = transaction.amount
    Row(
        modifier = Modifier
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.location,
                style = EateryBlueTypography.button,
                color = currentColors.textPrimary
            )
            Text(
                text = transaction.formattedDate,
                style = EateryBlueTypography.subtitle2,
                color = currentColors.textPrimary
            )
        }
        // TODO - when we get transaction type, update formatting as appropriate
        // e.g., for deposits, "+$%.2f".format(amount) to Green
        val (amtString, amtColor) = if (isMealSwipes) {
            val numSwipes = abs(amount).toInt()
            "-$numSwipes swipe" + (if (numSwipes > 1) "s" else "") to currentColors.error
        } else if (abs(amount) < 0.001) {
            "$0.00" to currentColors.textPrimary
        } else {
            "-$%.2f".format(abs(amount)) to currentColors.error
        }
        Text(
            text = amtString,
            modifier = Modifier.weight(0.2f),
            color = amtColor,
            textAlign = TextAlign.Right,
            style = EateryBlueTypography.button,
        )

    }
    HorizontalDivider(color = currentColors.accentPrimary, thickness = 1.dp)
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
        modifier = Modifier
            .height(50.dp)
            .background(currentColors.backgroundDefault),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = accountName,
            style = EateryBlueTypography.button,
            color = currentColors.textPrimary
        )
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Right,
            text = text,
            style = EateryBlueTypography.button,
            color = currentColors.textPrimary
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
            .background(currentColors.backgroundDefault)
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.payment_methods_title),
                style = EateryBlueTypography.h4,
                modifier = Modifier.padding(bottom = 12.dp),
                color = currentColors.textPrimary
            )

            IconButton(
                onClick = hide,
                modifier = Modifier
                    .size(40.dp)
                    .background(color = currentColors.backgroundDefault, shape = CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.close),
                    tint = currentColors.textPrimary
                )
            }
        }
        Column(modifier = Modifier.background(currentColors.backgroundDefault))
        {
            selectedPaymentMethod.forEachIndexed { index, account ->
                val accountIsSelected = selected == account
                val accountLabel = stringResource(account.displayNameRes())
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
                        color = currentColors.textPrimary,
                        text = accountLabel,
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
                            contentDescription = if (accountIsSelected) {
                                stringResource(R.string.a11y_account_selected, accountLabel)
                            } else {
                                stringResource(R.string.a11y_account_select, accountLabel)
                            },
                            tint = Color.Unspecified
                        )
                    }
                }
                if (index != selectedPaymentMethod.lastIndex) {
                    HorizontalDivider(
                        color = currentColors.accentPrimary,
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
                    containerColor = currentColors.accentPrimary,
                    contentColor = currentColors.textPrimary
                )
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 6.dp),
                    text = stringResource(R.string.account_show_transactions),
                    style = EateryBlueTypography.h5,
                    color = currentColors.textPrimary
                )
            }
        }
    }
}

@StringRes
private fun TransactionAccountType.displayNameRes(): Int = when (this) {
    TransactionAccountType.MEAL_SWIPES -> R.string.account_type_meal_swipes
    TransactionAccountType.BRBS -> R.string.account_type_big_red_bucks
    TransactionAccountType.LAUNDRY -> R.string.account_type_laundry
    TransactionAccountType.CITY_BUCKS -> R.string.account_type_city_bucks
}

@DualModePreview
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
            DisplayTransaction(
                id = "2023-10-01T12:30:00.000Z|Cafe Jennie|5.25|BRBS",
                amount = 5.25,
                accountType = TransactionAccountType.BRBS,
                location = "Cafe Jennie",
                formattedDate = "12:30 PM · Sunday, October 1"
            ),
            DisplayTransaction(
                id = "2023-10-02T14:00:00.000Z|Morrison Dining|15.0|BRBS",
                amount = 15.00,
                accountType = TransactionAccountType.BRBS,
                location = "Morrison Dining",
                formattedDate = "2:00 PM · Monday, October 2"
            )
        ),
        setSheetContent = {}
    )
}

