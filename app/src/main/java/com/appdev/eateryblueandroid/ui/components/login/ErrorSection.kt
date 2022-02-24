package com.appdev.eateryblueandroid.ui.components.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.LoginFailureType
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

@Composable
fun ErrorSection(error: LoginFailureType) {
    val errorText: String = if (error == LoginFailureType.FETCH_USER_FAILURE) {
        "Request to fetch user failed"
    } else if (error == LoginFailureType.USERNAME_PASSWORD_INVALID) {
        "NetID and/or password incorrect, please try again"
    } else if (error == LoginFailureType.FETCH_ACCOUNTS_FAILURE) {
        "Request to fetch user accounts failed"
    } else if (error == LoginFailureType.FETCH_TRANSACTION_HISTORY_FAILURE) {
        "Request to fetch transaction history failed"
    } else {
        "Internal error"
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .background(colorResource(id = R.color.redLight))
                .padding(top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_x_hex),
                contentDescription = null,
                tint = colorResource(id = R.color.red),
                modifier = Modifier.padding(top = 1.dp)
            )
            Text(
                text = errorText,
                color= colorResource(id = R.color.red),
                textStyle = TextStyle.LABEL_SEMIBOLD,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
