package com.cornellappdev.android.eatery.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.components.login.AccountPage
import com.cornellappdev.android.eatery.ui.components.login.LoginPage
import com.cornellappdev.android.eatery.ui.components.login.LoginPageContent
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
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
                webViewEnabled = webViewEnabled
            )
        },
        accountPage = @Composable {
            AccountPage(
                accountState = state as LoginViewModel.State.Account,
                loginViewModel = loginViewModel,
                onSettingsClicked = { onSettingsClicked() })
        },
        onBackClick = onBackClick
    )
}

@Composable
private fun ProfileScreenContent(
    state: LoginViewModel.State,
    loginPage: @Composable () -> Unit,
    accountPage: @Composable () -> Unit,
    onBackClick: () -> Unit
) {
    when (state) {
        is LoginViewModel.State.Login -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 7.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                    .then(Modifier.statusBarsPadding())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 2.dp)
                        .height(34.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onBackClick() },
                        modifier = Modifier.size(24.dp, 24.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_left_chevron),
                            contentDescription = "Back Arrow"
                        )
                    }
                }
                Text(
                    text = "Log into Eatery",
                    color = EateryBlue,
                    style = EateryBlueTypography.h3
                )
                loginPage()
            }
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
                onLoginPressed = {},
                getUser = null,
                webViewEnabled = false
            )
        },
        accountPage = { },
        onBackClick = { }
    )
}