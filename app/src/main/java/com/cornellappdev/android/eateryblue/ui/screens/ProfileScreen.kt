package com.cornellappdev.android.eateryblue.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.data.models.User
import com.cornellappdev.android.eateryblue.ui.components.login.LoginPage
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GraySix

@Composable
fun ProfileScreen(
    autoLogin: Boolean = true,
    onSettingsClicked: () -> Unit,
    onLoginSuccess: (user: User) -> Unit
) {
    var attemptAutoLogin by remember { mutableStateOf(autoLogin) }
//    Log.d("profileScreen", attemptAutoLogin.toString())
//
//    AnimatedVisibility(visible = attemptAutoLogin) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            Column(modifier = Modifier.align(Alignment.Center)) {
//                Text(
//                    text = "Attempting to login...",
//                    modifier = Modifier
//                        .align(Alignment.CenterHorizontally)
//                        .padding(bottom = 10.dp),
//                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
//                    color = EateryBlue
//                )
//
//                LinearProgressIndicator(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(15.dp)
//                        .padding(horizontal = 30.dp),
//                    backgroundColor = Color.White,
//                    color = EateryBlue
//                )
//            }
//        }
//    }
//

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .then(Modifier.statusBarsPadding())
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.End)
                .size(32.dp),
            onClick = { onSettingsClicked() }) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Outlined.Settings,
                contentDescription = Icons.Outlined.Settings.name,
                tint = Color.Black
            )
        }

        Text(
            text = "Log in with Eatery",
            style = EateryBlueTypography.h3,
            color = EateryBlue
        )

        Text(
            text = "See your meal swipes, BRBs, and more",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = GraySix,
            modifier = Modifier.padding(top = 7.dp)
        )

        LoginPage(onSuccess = onLoginSuccess, onError = {
            attemptAutoLogin = false
        }, onAutoLoginFail = {
            attemptAutoLogin = false
        }) {
            attemptAutoLogin = false
        }
    }
}
