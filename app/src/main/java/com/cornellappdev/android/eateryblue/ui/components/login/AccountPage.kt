package com.cornellappdev.android.eateryblue.ui.components.login

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun AccountPage(
    accountState: LoginViewModel.State.Account,
    loginViewModel: LoginViewModel,
    onSettingsClicked: () -> Unit
) {
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
        val innerListState = rememberLazyListState()
        val isFirstVisible =
            remember { derivedStateOf { innerListState.firstVisibleItemIndex > 1 } }


        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EateryBlue)
                    .then(Modifier.statusBarsPadding())
                    .padding(bottom = 7.dp),
            ) {
                AnimatedContent(
                    targetState = isFirstVisible.value
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
                                onClick = {
                                    onSettingsClicked()
                                }
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
                                .then(Modifier.statusBarsPadding())
                                .padding(bottom = 7.dp),
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .align(Alignment.End)
                                    .size(32.dp)
                                    .statusBarsPadding(),
                                onClick = { onSettingsClicked() }) {
                                Icon(
                                    modifier = Modifier.size(28.dp),
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = Icons.Outlined.Settings.name,
                                    tint = Color.White
                                )
                            }
                            Column(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 24.dp
                                )
                            ) {
                                Text(
                                    text = "Account",
                                    color = Color.White,
                                    style = EateryBlueTypography.h2
                                )
                            }
                        }
                    }
                }
            }
            LazyColumn(state = innerListState) {
                item {
                    (Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Column(
                        ) {
                            Text(
                                text = "Meal Plan",
                                style = EateryBlueTypography.h4,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            AccountBalanceRow(
                                accountName = "Meal Swipes",
                                accountType = AccountType.MEALSWIPES,
                                loginViewModel = loginViewModel
                            )
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(GrayZero, CircleShape)
                            )
                            AccountBalanceRow(
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
                            AccountBalanceRow(
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
                            AccountBalanceRow(
                                accountName = "Laundry",
                                accountType = AccountType.LAUNDRY,
                                loginViewModel = loginViewModel
                            )
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
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Change Account Type",
                                    modifier = Modifier
                                        .size(26.dp)
                                )
                            }
                        }
                        SearchBar(
                            searchText = filterText,
                            onSearchTextChange = { filterText = it },
                            modifier = Modifier.padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
                            placeholderText = "Search for transactions...",
                            onCancelClicked = {
                                filterText = ""
                            }
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 16.dp)
                                .background(GrayZero, CircleShape)
                        )
                        Text(
                            text = "Past 30 Days",
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                            style = EateryBlueTypography.h5
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 16.dp)
                                .background(GrayZero, CircleShape)
                        )


                    }
                }
                items(
                    loginViewModel.getTransactionsOfType(
                        accountState.accountFilter,
                        filterText
                    )
                ) { it ->
                    val inputFormatter =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                    val outputFormatter = DateTimeFormatter.ofPattern("h:mm a · EEEE, MMMM d")
                    val dateTime = LocalDateTime.parse(it.date, inputFormatter)
                    Row(
                        modifier = Modifier
                            .height(64.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${it.location}", style = EateryBlueTypography.button)
                            Text(
                                text = outputFormatter.format(dateTime),
                                style = EateryBlueTypography.subtitle2
                            )
                        }
                        var amtColor by remember { mutableStateOf(Color.Unspecified) }
                        var amtString by remember { mutableStateOf("$0.00") }
                        when {
                            it.transactionType == 3 -> {
                                amtString = "+$%.2f".format(it.amount)
                                amtColor =
                                    Color(LocalContext.current.resources.getColor(R.color.green))
                            }

                            it.amount?.toInt() == 0 -> {
                                amtString = "$0.00"
                                amtColor = Color.Black
                            }

                            else -> {
                                amtString = "-$%.2f".format(it.amount)
                                amtColor =
                                    Color(LocalContext.current.resources.getColor(R.color.red))
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
fun AccountBalanceRow(
    accountName: String,
    accountType: AccountType,
    loginViewModel: LoginViewModel
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
            text = if (accountType != AccountType.MEALSWIPES) {
                "$" + "%.2f".format(
                    loginViewModel.checkAccount(accountType)?.balance?.toFloat() ?: 0f
                )
            } else {
                "%.0f".format(
                    loginViewModel.checkMealPlan()?.balance ?: 0
                ) + " remaining"
            },
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
                val select = when (selected) {
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
                            tint = Color.Unspecified
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
