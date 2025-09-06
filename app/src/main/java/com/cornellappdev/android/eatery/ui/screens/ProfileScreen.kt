package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.login.AccountPage
import com.cornellappdev.android.eatery.ui.components.login.LoginPage
import com.cornellappdev.android.eatery.ui.components.login.LoginPageContent
import com.cornellappdev.android.eatery.ui.components.login.LoginToast
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.viewmodels.LoginViewModel
import com.cornellappdev.android.eatery.util.EateryPreview


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel,
    onSettingsClicked: () -> Unit,
) {
    val state = loginViewModel.state.collectAsState().value
    ProfileScreenContent(
        state,
        loginPage = @Composable {
            val context = LocalContext.current
            LoginPage(
                loginState = state as LoginViewModel.State.Login,
                loginViewModel = loginViewModel,
                onWrongCredentials = {
                    LoginToast(
                        context,
                        "NetID and/or password incorrect",
                        R.drawable.ic_error,
                        R.color.light_red,
                        R.color.red
                    )
                    loginViewModel.onLoginFailed()
                }
            )
        },
        accountPage = @Composable {
            AccountPage(
                accountState = state as LoginViewModel.State.Account,
                loginViewModel = loginViewModel,
                onSettingsClicked = { onSettingsClicked() })
        },
        onSettingsClicked
    )
}

@Composable
private fun ProfileScreenContent(
    state: LoginViewModel.State,
    loginPage: @Composable () -> Unit,
    accountPage: @Composable () -> Unit,
    onSettingsClicked: () -> Unit
) {
    Column(modifier = Modifier.background(Color.White)) {
        when (state) {
            is LoginViewModel.State.Login -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 7.dp)
                        .then(Modifier.statusBarsPadding())
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
                            tint = Color.Black
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
                            text = "Log in with Eatery",
                            color = EateryBlue,
                            style = EateryBlueTypography.h3
                        )
                    }
                }
                loginPage()
            }

            is LoginViewModel.State.Account -> {
                accountPage()
            }
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() = EateryPreview {
    val state = LoginViewModel.State.Login(
        netid = "aaa00",
        password = "myVeryLongPassword",
        failureMessage = null,
        loading = false
    )
    ProfileScreenContent(
        state = state,
        loginPage = {
            LoginPageContent(
                loginState = state,
                onWrongCredentials = {},
                onNetIdTyped = {},
                onPasswordTyped = {},
                onLoginPressed = {},
                getUser = null
            )
        },
        accountPage = { },
        onSettingsClicked = { }
    )
}