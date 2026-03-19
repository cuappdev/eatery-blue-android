package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.data.models.AccountBalances
import com.cornellappdev.android.eatery.data.models.TransactionAccountType
import com.cornellappdev.android.eatery.ui.components.general.NetworkErrorToast
import com.cornellappdev.android.eatery.ui.components.login.AccountPage
import com.cornellappdev.android.eatery.ui.components.login.LoginPage
import com.cornellappdev.android.eatery.ui.viewmodels.LoginViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.DisplayTransaction


@OptIn(ExperimentalAnimationApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onSettingsClicked: () -> Unit,
    webViewEnabled: Boolean,
    onBackClick: () -> Unit
) {
    val uiState = loginViewModel.uiState.collectAsStateWithLifecycle().value

    // todo - replace toasts with actual error state
    if (uiState.isLoginState) {
        NetworkErrorToast(
            error = uiState.error,
            onErrorShown = loginViewModel::clearError
        )
    }

    ProfileScreenContent(
        isLoginState = uiState.isLoginState,
        accountTypeBalance = uiState.accountTypeBalance,
        loading = uiState.isLoginState && uiState.loading,
        onLoginPressed = loginViewModel::onLoginPressed,
        onSuccess = loginViewModel::onLoginWebViewSuccess,
        webViewEnabled = webViewEnabled,
        onBackClick = onBackClick,
        onModalHidden = loginViewModel::onLoginExited,
        onSettingsClicked = onSettingsClicked,
        accountFilter = uiState.accountFilter,
        filterText = uiState.filterText,
        filteredTransactions = uiState.filteredTransactions,
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
    filterText: String,
    onSettingsClicked: () -> Unit,
    filteredTransactions: List<DisplayTransaction>,
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
            filterText = filterText,
            onQueryChanged = onQueryChanged,
            updateAccountFilter = updateAccountFilter
        )
    }
}