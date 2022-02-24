package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.login.ErrorSection
import com.appdev.eateryblueandroid.ui.components.login.LoginButton
import com.appdev.eateryblueandroid.ui.components.login.TextInputs
import com.appdev.eateryblueandroid.ui.components.login.TitleSection
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun LoginScreen(
    profileViewModel: ProfileViewModel,
) {
    var netid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("")}
    val passwordFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.padding(top = 30.dp, start = 16.dp, end = 16.dp)
    ) {

        TitleSection()
        TextInputs(
            netid = netid,
            password = password,
            setNetId = {netid = it},
            setPassword = {password = it},
            login = {profileViewModel.initiateLogin(netid, password)},
            passwordFocus = passwordFocus,
            hideKeyboard = {focusManager.clearFocus()}
        )
        ErrorSection()
        LoginButton(
            profileViewModel = profileViewModel,
            login = {profileViewModel.initiateLogin(netid, password)}
        )
    }
}