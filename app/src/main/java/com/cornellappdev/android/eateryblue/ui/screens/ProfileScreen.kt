package com.cornellappdev.android.eateryblue.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eateryblue.ui.components.login.AccountPage
import com.cornellappdev.android.eateryblue.ui.components.login.LoginPage
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.viewmodels.LoginViewModel


@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onSettingsClicked: () -> Unit,
) {
    val state = loginViewModel.state.collectAsState().value
    Column(
    ) {
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
                            text = "Log In With Eatery",
                            color = EateryBlue,
                            style = EateryBlueTypography.h3
                        )
                    }
                }
                LoginPage(
                    loginState = state,
                    loginViewModel = loginViewModel,
                    onWrongCredentials = { loginViewModel.onLoginFailed() }
                )

            }

            is LoginViewModel.State.Account -> {
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
                AccountPage(accountState = state, loginViewModel = loginViewModel)
            }
        }

    }
}
