package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cornellappdev.android.eatery.data.models.AccountBalances
import com.cornellappdev.android.eatery.data.models.TransactionAccountType
import com.cornellappdev.android.eatery.ui.components.general.NetworkErrorToast
import com.cornellappdev.android.eatery.ui.components.login.AccountPage
import com.cornellappdev.android.eatery.ui.components.login.LoginPage
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.ui.viewmodels.LoginViewModel
import com.cornellappdev.android.eatery.ui.viewmodels.state.DisplayTransaction
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onSettingsClicked: () -> Unit,
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
        loading = uiState.isLoading,
        onLoginPressed = loginViewModel::onLoginPressed,
        onSuccess = loginViewModel::onLoginWebViewSuccess,
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
    onBackClick: () -> Unit,
    onModalHidden: () -> Unit,
    accountFilter: TransactionAccountType,
    filterText: String,
    onSettingsClicked: () -> Unit,
    filteredTransactions: List<DisplayTransaction>,
    onQueryChanged: (String) -> Unit,
    updateAccountFilter: (TransactionAccountType) -> Unit
) {
    Box(
        modifier = Modifier
            .background(color = currentColors.backgroundDefault)
            .fillMaxSize()
    ) {
        if (isLoginState) {
            LoginPage(
                isLoading = loading,
                onLoginPressed = onLoginPressed,
                onSuccess = onSuccess,
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
}

@DualModePreview
@Composable
private fun ProfileScreenLoginPreview() = EateryPreview {
    ProfileScreenContent(
        isLoginState = true,
        accountTypeBalance = AccountBalances(),
        loading = false,
        onLoginPressed = {},
        onSuccess = {},
        onBackClick = {},
        onModalHidden = {},
        accountFilter = TransactionAccountType.BRBS,
        filterText = "",
        onSettingsClicked = {},
        filteredTransactions = emptyList(),
        onQueryChanged = {},
        updateAccountFilter = {}
    )
}

@DualModePreview
@Composable
private fun ProfileScreenAccountPreview() = EateryPreview {
    ProfileScreenContent(
        isLoginState = false,
        accountTypeBalance = AccountBalances(
            brbBalance = 120.35,
            cityBucksBalance = 42.0,
            laundryBalance = 8.0,
            mealSwipes = 10
        ),
        loading = false,
        onLoginPressed = {},
        onSuccess = {},
        onBackClick = {},
        onModalHidden = {},
        accountFilter = TransactionAccountType.BRBS,
        filterText = "",
        onSettingsClicked = {},
        filteredTransactions = listOf(
            DisplayTransaction(
                id = "1",
                amount = -12.75,
                accountType = TransactionAccountType.BRBS,
                location = "Okenshields",
                formattedDate = "Apr 14"
            )
        ),
        onQueryChanged = {},
        updateAccountFilter = {}
    )
}

