package com.appdev.eateryblueandroid.ui.components.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun TitleSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = null,
            tint = colorResource(id = R.color.black)
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