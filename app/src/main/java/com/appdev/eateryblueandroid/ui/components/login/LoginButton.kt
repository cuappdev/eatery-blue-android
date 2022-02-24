package com.appdev.eateryblueandroid.ui.components.login

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun LoginButton(
    profileViewModel: ProfileViewModel,
    login: () -> Unit
) {

    val display = profileViewModel.display.collectAsState()
    val authenticating = (display.value as ProfileViewModel.Display.Login).authenticating
    val progress = (display.value as ProfileViewModel.Display.Login).progress

    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    Surface(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .background(colorResource(id = R.color.eateryBlue))
                .padding(top = 13.dp, bottom = 13.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (authenticating) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    color = colorResource(id = R.color.white)
                )
            } else {
                Row(modifier = Modifier.clickable{login()}
                ) {
                    Text(
                        text = "Log in",
                        color = colorResource(id = R.color.white),
                        textStyle = TextStyle.HEADER_H4
                    )
                }
            }
        }
    }
}