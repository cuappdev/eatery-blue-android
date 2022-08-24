package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.ui.components.login.ErrorSection
import com.appdev.eateryblueandroid.ui.components.login.LoginButton
import com.appdev.eateryblueandroid.ui.components.login.TextInputs
import com.appdev.eateryblueandroid.ui.components.login.TitleSection
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun LoginScreen(profileViewModel: ProfileViewModel) {
    var netid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val state = profileViewModel.state.collectAsState()
    Box {
        Column(
            modifier = Modifier.padding(top = 30.dp, start = 16.dp, end = 16.dp)
        ) {

            TitleSection(
                profileViewModel::transitionSettings,
                state.value is ProfileViewModel.State.LoggingIn
            )
            TextInputs(
                netid = netid,
                password = password,
                setNetId = { netid = it },
                setPassword = { password = it },
                login = { profileViewModel.initiateLogin(netid, password) },
                passwordFocus = passwordFocus,
                hideKeyboard = { focusManager.clearFocus() }
            )
            state.value.let {
                if (it is ProfileViewModel.State.LoginFailure) {
                    ErrorSection(it.error)
                }
            }
            LoginButton(
                profileViewModel = profileViewModel,
                login = {
                    if (netid.isNotEmpty() && password.isNotEmpty()) {
                        focusManager.clearFocus()
                        profileViewModel.initiateLogin(netid, password)
                    }
                },
                clickable = netid.isNotEmpty() && password.isNotEmpty()
            )
        }
    }
    /*
    Since login systems are down (as of 8/24/22 7:53PM), use this awkwardly placed button to
    test shaking. TODO: Delete as soon as login is back up. */
    Button(onClick = {
        profileViewModel.shakeLogin()
    }) {
        Text(text = "Delete me later... but press me to shake!")
    }

}