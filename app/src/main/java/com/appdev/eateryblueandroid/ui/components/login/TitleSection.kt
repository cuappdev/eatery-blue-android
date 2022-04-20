package com.appdev.eateryblueandroid.ui.components.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel

@Composable
fun TitleSection(transitionSettings: () -> Unit, isLoggingIn: Boolean) {
    val interactionSource = MutableInteractionSource()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = null,
            tint = colorResource(id = R.color.black),
            modifier = Modifier.then(
                if (isLoggingIn) Modifier
                else Modifier.clickable(
                    onClick = { transitionSettings() },
                    interactionSource = interactionSource,
                    indication = rememberRipple(radius = 12.dp)
                )
            )
        )
    }
    Text(
        text = "Log in with Eatery",
        textStyle = TextStyle.HEADER_H2,
        color = colorResource(id = R.color.eateryBlue),
        modifier = Modifier.padding(top = 2.dp)
    )
    Text(
        text = "See your meal swipes, BRBs, and more",
        textStyle = TextStyle.APPDEV_BODY_MEDIUM,
        color = colorResource(id = R.color.gray06),
        modifier = Modifier.padding(top = 7.dp)
    )
}