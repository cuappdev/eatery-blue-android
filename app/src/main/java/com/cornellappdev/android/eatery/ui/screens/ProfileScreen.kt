package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cornellappdev.android.eatery.ui.components.login.AccountPage
import com.cornellappdev.android.eatery.ui.components.login.LoginPage
import com.cornellappdev.android.eatery.ui.components.login.LoginPageContent
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
        loginPage = @Composable {
            LoginPage(
                loginState = state as LoginViewModel.State.Login,
                loginViewModel = loginViewModel,
                webViewEnabled = webViewEnabled,
                onBackClick = onBackClick
            )
        },
        accountPage = @Composable {
            AccountPage(
                accountState = state as LoginViewModel.State.Account,
                loginViewModel = loginViewModel,
                onSettingsClicked = { onSettingsClicked() })
        }
    )
}

@Composable
private fun ProfileScreenContent(
    state: LoginViewModel.State,
    loginPage: @Composable () -> Unit,
    accountPage: @Composable () -> Unit,
) {
    when (state) {
        is LoginViewModel.State.Login -> {
            loginPage()
        }

        is LoginViewModel.State.Account -> {
            accountPage()
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
        loginPage = {
            LoginPageContent(
                loading = false,
                onLoginPressed = {},
                onSuccess = {},
                webViewEnabled = false,
                onBackClick = {},
                onModalHidden = {}
            )
        },
        accountPage = { }
    )
}