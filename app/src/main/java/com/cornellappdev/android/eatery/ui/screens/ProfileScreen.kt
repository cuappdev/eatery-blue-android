package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.android.eatery.data.models.Account
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
        state,
        loading = state is LoginViewModel.State.Login && state.loading,
        onLoginPressed = loginViewModel::onLoginPressed,
        onSuccess = loginViewModel::onLoginWebViewSuccess,
        webViewEnabled = webViewEnabled,
        onBackClick = onBackClick,
        onModalHidden = loginViewModel::onLoginExited,
        accountFilter = if (state is LoginViewModel.State.Account) state.accountFilter else AccountType.BRBS,
        checkAccount = loginViewModel::checkAccount,
        checkMealPlan = loginViewModel::checkMealPlan,
        onSettingsClicked = onSettingsClicked,
        getTransactionsOfType = loginViewModel::getTransactionsOfType,
        updateAccountFilter = loginViewModel::updateAccountFilter
    )
}

@Composable
private fun ProfileScreenContent(
    state: LoginViewModel.State,
    loading: Boolean,
    onLoginPressed: () -> Unit,
    onSuccess: (String) -> Unit,
    webViewEnabled: Boolean,
    onBackClick: () -> Unit,
    onModalHidden: () -> Unit,
    accountFilter: AccountType,
    checkAccount: (AccountType) -> Account?,
    checkMealPlan: () -> Account?,
    onSettingsClicked: () -> Unit,
    getTransactionsOfType: (AccountType, String) -> List<Transaction>,
    updateAccountFilter: (AccountType) -> Unit
) {
    when (state) {
        is LoginViewModel.State.Login -> {
            LoginPage(
                loading = loading,
                onLoginPressed = onLoginPressed,
                onSuccess = onSuccess,
                webViewEnabled = webViewEnabled,
                onBackClick = onBackClick,
                onModalHidden = onModalHidden
            )
        }

        is LoginViewModel.State.Account -> {
            AccountPage(
                accountFilter = accountFilter,
                checkAccount = checkAccount,
                checkMealPlan = checkMealPlan,
                onSettingsClicked = onSettingsClicked,
                getTransactionsOfType = getTransactionsOfType,
                updateAccountFilter = updateAccountFilter
            )
        }
    }
}

@Preview
@Composable
private fun ProfileLoginScreenPreview() = EateryPreview {
    val state = LoginViewModel.State.Login(
        netID = "aaa00",
        password = "myVeryLongPassword",
        failureMessage = null,
        loading = false
    )
    ProfileScreenContent(
        state = state,
        loading = false,
        onLoginPressed = {},
        onSuccess = {},
        webViewEnabled = false,
        onBackClick = {},
        onModalHidden = {},
        accountFilter = AccountType.BRBS,
        checkAccount = { null },
        checkMealPlan = { null },
        onSettingsClicked = {},
        getTransactionsOfType = { _, _ -> emptyList() },
        updateAccountFilter = {},
    )
}