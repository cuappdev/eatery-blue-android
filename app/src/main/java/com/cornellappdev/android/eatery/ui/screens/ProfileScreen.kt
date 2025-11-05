package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.android.eatery.data.models.AccountType
import com.cornellappdev.android.eatery.data.models.Transaction
import com.cornellappdev.android.eatery.ui.components.login.AccountPage
import com.cornellappdev.android.eatery.ui.components.login.LoginPage
import com.cornellappdev.android.eatery.ui.viewmodels.LoginViewModel
import com.cornellappdev.android.eatery.util.EateryPreview


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel,
    onSettingsClicked: () -> Unit,
    webViewEnabled: Boolean,
    onBackClick: () -> Unit
) {
    val state = loginViewModel.state.collectAsState().value
    ProfileScreenContent(
        isLoginState = state is LoginViewModel.State.Login,
        accountTypeBalance = state.getBalanceMap(),
        loading = state is LoginViewModel.State.Login && state.loading,
        onLoginPressed = loginViewModel::onLoginPressed,
        onSuccess = loginViewModel::onLoginWebViewSuccess,
        webViewEnabled = webViewEnabled,
        onBackClick = onBackClick,
        onModalHidden = loginViewModel::onLoginExited,
        accountFilter = if (state is LoginViewModel.State.Account) state.accountFilter else AccountType.BRBS,
        onSettingsClicked = onSettingsClicked,
        getTransactionsOfType = loginViewModel::getTransactionsOfType,
        updateAccountFilter = loginViewModel::updateAccountFilter
    )
}

@Composable
private fun ProfileScreenContent(
    isLoginState: Boolean,
    accountTypeBalance: Map<AccountType, Double?>,
    loading: Boolean,
    onLoginPressed: () -> Unit,
    onSuccess: (String) -> Unit,
    webViewEnabled: Boolean,
    onBackClick: () -> Unit,
    onModalHidden: () -> Unit,
    accountFilter: AccountType,
    onSettingsClicked: () -> Unit,
    getTransactionsOfType: (AccountType, String) -> List<Transaction>,
    updateAccountFilter: (AccountType) -> Unit
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
            getTransactionsOfType = getTransactionsOfType,
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
        failureMessage = null,
        loading = false
    )
    ProfileScreenContent(
        isLoginState = false,
        accountTypeBalance = mapOf(
            AccountType.BRBS to 1234.56,
            AccountType.CITYBUCKS to 78.90,
            AccountType.LAUNDRY to 12.34,
            AccountType.MEALSWIPES to 4.20
        ),
        loading = false,
        onLoginPressed = {},
        onSuccess = {},
        webViewEnabled = false,
        onBackClick = {},
        onModalHidden = {},
        accountFilter = AccountType.BRBS,
        onSettingsClicked = {},
        getTransactionsOfType = { _, _ -> emptyList() },
        updateAccountFilter = {},
    )
}