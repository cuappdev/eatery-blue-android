package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eatery.data.models.AccountBalances
import com.cornellappdev.android.eatery.data.models.Transaction
import com.cornellappdev.android.eatery.data.models.TransactionAccountType
import com.cornellappdev.android.eatery.ui.components.login.AccountPage
import com.cornellappdev.android.eatery.ui.components.login.LoginPage
import com.cornellappdev.android.eatery.ui.viewmodels.LoginViewModel
import com.cornellappdev.android.eatery.util.EateryPreview


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onSettingsClicked: () -> Unit,
    webViewEnabled: Boolean,
    onBackClick: () -> Unit
) {
    val state = loginViewModel.state.collectAsState().value
    val filteredTransactions =
        loginViewModel.filteredTransactionsFlow.collectAsState(initial = emptyList()).value
    ProfileScreenContent(
        isLoginState = state is LoginViewModel.State.Login,
        accountTypeBalance = state.getBalances(),
        loading = state is LoginViewModel.State.Login && state.loading,
        onLoginPressed = loginViewModel::onLoginPressed,
        onSuccess = loginViewModel::onLoginWebViewSuccess,
        webViewEnabled = webViewEnabled,
        onBackClick = onBackClick,
        onModalHidden = loginViewModel::onLoginExited,
        onSettingsClicked = onSettingsClicked,
        accountFilter = if (state is LoginViewModel.State.Account) state.accountFilter else TransactionAccountType.BRBS,
        filteredTransactions = filteredTransactions,
        onQueryChanged = loginViewModel::setQuery,
        updateAccountFilter = loginViewModel::updateAccountFilter
    )
}

@Composable
private fun ProfileScreenContent(
    isLoginState: Boolean,
    accountTypeBalance: AccountBalances,
    loading: Boolean,
    onLoginPressed: () -> Unit,
    onSuccess: (String) -> Unit,
    webViewEnabled: Boolean,
    onBackClick: () -> Unit,
    onModalHidden: () -> Unit,
    accountFilter: TransactionAccountType,
    onSettingsClicked: () -> Unit,
    filteredTransactions: List<Transaction>,
    onQueryChanged: (String) -> Unit,
    updateAccountFilter: (TransactionAccountType) -> Unit
) {
    if (isLoginState) {
        LoginPage(
            loading = loading,
            onLoginPressed = onLoginPressed,
            onSuccess = onSuccess,
            webViewEnabled = webViewEnabled,
            onBackClick = onBackClick,
            onModalHidden = onModalHidden
        )
    } else {
        AccountPage(
            accountFilter = accountFilter,
            accountTypeBalance = accountTypeBalance,
            onSettingsClicked = onSettingsClicked,
            filteredTransactions = filteredTransactions,
            onQueryChanged = onQueryChanged,
            updateAccountFilter = updateAccountFilter
        )
    }
}

@Preview
@Composable
private fun ProfileLoginScreenPreview() = EateryPreview {
    LoginViewModel.State.Login(
        netID = "aaa00",
        password = "myVeryLongPassword",
        failure = null,
        loading = false
    )
    ProfileScreenContent(
        isLoginState = false,
        accountTypeBalance = AccountBalances(
            brbBalance = 1234.56,
            cityBucksBalance = 78.90,
            laundryBalance = 12.34,
            mealSwipes = 30
        ),
        loading = false,
        onLoginPressed = {},
        onSuccess = {},
        webViewEnabled = false,
        onBackClick = {},
        onModalHidden = {},
        accountFilter = TransactionAccountType.BRBS,
        onSettingsClicked = {},
        filteredTransactions = emptyList(),
        onQueryChanged = {},
        updateAccountFilter = {},
    )
}