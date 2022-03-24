package com.appdev.eateryblueandroid.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.ui.components.login.ErrorSection
import com.appdev.eateryblueandroid.ui.components.login.LoginButton
import com.appdev.eateryblueandroid.ui.components.login.TextInputs
import com.appdev.eateryblueandroid.ui.components.login.TitleSection
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.appdev.eateryblueandroid.util.appContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

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
            state.value.let { it ->
                if (it is ProfileViewModel.State.LoginFailure) {
                    ErrorSection(it.error)
                }
            }
            LoginButton(
                profileViewModel = profileViewModel,
                login = {
                    if (netid.isNotEmpty() && password.isNotEmpty())
                        profileViewModel.initiateLogin(netid, password)
                }
            )
        }
    }
}