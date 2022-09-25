package com.appdev.eateryblueandroid.ui.components.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.ui.components.login.ErrorSection
import com.appdev.eateryblueandroid.ui.components.login.LoginButton
import com.appdev.eateryblueandroid.ui.components.login.LoginTextInputs
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun LoginPage(
    profileViewModel: ProfileViewModel,
    pagerOffset: Float
) {
    var netid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val state = profileViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            // This is bad but required since HorizontalPager is buggy and quite limited.
            .requiredWidth(LocalConfiguration.current.screenWidthDp.dp - 32.dp)
            .height(LocalConfiguration.current.screenHeightDp.dp - 204.dp)
            .graphicsLayer {
                val pageOffset =
                    -pagerOffset.coerceIn(-1f, 0f)

                val lerp = { startValue: Float, endValue: Float, fraction: Float ->
                    startValue + (fraction * (endValue - startValue))
                }

                val offsetLerp = lerp(
                    0f,
                    240f,
                    pageOffset
                )

                translationX = offsetLerp
            }
            .offset(y = (-16).dp),
        verticalArrangement = Arrangement.Top
    ) {
        LoginTextInputs(
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
                if (netid.isNotEmpty() && password.isNotEmpty()) {
                    focusManager.clearFocus()
                    profileViewModel.initiateLogin(netid, password)
                }
            },
            clickable = netid.isNotEmpty() && password.isNotEmpty()
        )
    }
}