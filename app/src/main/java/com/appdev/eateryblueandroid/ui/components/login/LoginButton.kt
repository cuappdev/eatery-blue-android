package com.appdev.eateryblueandroid.ui.components.login

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun LoginButton(
    profileViewModel: ProfileViewModel,
    login: () -> Unit,
    clickable: Boolean = true,
) {

    val display = profileViewModel.display.collectAsState()
    val authenticating = (display.value as? ProfileViewModel.Display.Login)?.authenticating ?: false
    val progress = (display.value as? ProfileViewModel.Display.Login)?.progress ?: 0f

    val offset =
        animateIntOffsetAsState(
            targetValue = (display.value as? ProfileViewModel.Display.Login)?.intOffset
                ?: IntOffset.Zero
        )

    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    Button(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .height(48.dp)
            .offset {
                offset.value
            },
        onClick = login,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(R.color.eateryBlue),
            disabledBackgroundColor = colorResource(R.color.gray00)
        ),
        enabled = clickable,
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
    ) {
        if (authenticating) {
            CircularProgressIndicator(
                progress = animatedProgress,
                color = colorResource(id = R.color.white),
                modifier = Modifier.size(30.dp)
            )
        } else {
            Text(
                text = "Log in",
                color = colorResource(id = if (clickable) R.color.white else R.color.gray03),
                textStyle = TextStyle.HEADER_H4
            )
        }
    }
}