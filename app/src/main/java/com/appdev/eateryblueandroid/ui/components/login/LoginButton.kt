package com.appdev.eateryblueandroid.ui.components.login

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

@Composable
fun LoginButton(
    profileViewModel: ProfileViewModel,
    login: () -> Unit,
    clickable: Boolean = true,
) {

    val display = profileViewModel.display.collectAsState()
    val authenticating = (display.value as ProfileViewModel.Display.Login).authenticating
    val progress = (display.value as ProfileViewModel.Display.Login).progress
    val interactionSource = remember { MutableInteractionSource() }

    val offset = animateIntOffsetAsState(targetValue = (display.value as ProfileViewModel.Display.Login).intOffset)

    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    Surface(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .offset {
                offset.value
            }
    ) {
        Row(
            modifier = Modifier
                .background(colorResource(id = if (clickable) R.color.eateryBlue else R.color.gray00)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (authenticating) {
                Row(modifier = Modifier.padding(10.dp)) {
                    CircularProgressIndicator(
                        progress = animatedProgress,
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.size(30.dp)
                    )
                }
            } else {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .then(if (clickable && (display.value as ProfileViewModel.Display.Login).intOffset.x == 0) Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple()
                        ) { login() }
                    else Modifier)
                    .then(Modifier.padding(top = 13.dp, bottom = 13.dp)),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Log in",
                        color = colorResource(id = if (clickable) R.color.white else R.color.gray03),
                        textStyle = TextStyle.HEADER_H4
                    )
                }
            }
        }
    }
}