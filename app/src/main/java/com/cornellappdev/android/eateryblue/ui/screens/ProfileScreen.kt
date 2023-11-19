package com.cornellappdev.android.eateryblue.ui.screens

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eateryblue.data.models.User
import com.cornellappdev.android.eateryblue.ui.viewmodels.LoggingInStatus
import com.cornellappdev.android.eateryblue.ui.viewmodels.LoginViewModel
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse

@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    autoLogin: Boolean = true,
    onSettingsClicked: () -> Unit,
    onLoginSuccess: (user: User) -> Unit,
) {
    var loginStatus = loginViewModel.loginState.collectAsState().value

    var attemptAutoLogin by remember { mutableStateOf(autoLogin) }
    Log.d("WHATISAUTOLOGIN", attemptAutoLogin.toString())

    when (loginStatus) {
        LoggingInStatus.NotLoggedIn -> {
            Text("SHOW LOGIN SCREEN")
        }

        is LoggingInStatus.LoggingIn -> {
            when (loginStatus.user) {
                EateryApiResponse.Pending -> {
                    Text("SHOW GRAYED OUT BUTTON & LOGIN PAGE WITH LOADING ANIMATION AND TEXT FIELDS GRAYED OUT")
                }

                EateryApiResponse.Error -> {
                    Text("SHOW LOGIN SCREEN AND A TOAST ABOUT ERROR")
                }

                is EateryApiResponse.Success -> {
                    Text("SHOW ACCOUNTS SCREEN")
                }
            }

        }
    }


//    Column(
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxWidth()
//    ) {
//        IconButton(
//            modifier = Modifier
//                .align(Alignment.End)
//                .size(32.dp),
//            onClick = { onSettingsClicked() }) {
//            Icon(
//                modifier = Modifier.size(28.dp),
//                imageVector = Icons.Outlined.Settings,
//                contentDescription = Icons.Outlined.Settings.name,
//                tint = Color.Black
//            )
//        }
//
//        Text(
//            text = "Log in with Eatery",
//            style = EateryBlueTypography.h3,
//            color = EateryBlue
//        )
//
//        Text(
//            text = "See your meal swipes, BRBs, and more",
//            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
//            color = GraySix,
//            modifier = Modifier.padding(top = 7.dp)
//        )

//        LoginPage(
//            autoLogin = attemptAutoLogin,
//            onSuccess = onLoginSuccess, onError = {
//            attemptAutoLogin = false
//        }, onAutoLoginFail = {
//            attemptAutoLogin = false
//        }) {
//            attemptAutoLogin = false
//        }
}

