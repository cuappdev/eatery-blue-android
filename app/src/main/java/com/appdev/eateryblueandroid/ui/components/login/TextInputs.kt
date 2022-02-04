package com.appdev.eateryblueandroid.ui.components.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextField
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun TextInputs(
    netid: String,
    password: String,
    setNetId: (String) -> Unit,
    setPassword: (String) -> Unit,
    login: () -> Unit,
    passwordFocus: FocusRequester,
    hideKeyboard: () -> Unit

) {
    Text(
        text="NetID",
        textStyle = TextStyle.HEADER_H4,
        color = colorResource(id = R.color.black),
        modifier = Modifier.padding(top = 37.dp)
    )
    TextField(
        value = netid,
        onValueChange = setNetId,
        placeholder = "Type your NetID (i.e. abc123)",
        backgroundColor = colorResource(R.color.gray00),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        onSubmit = {
            if (netid.isNotEmpty()) passwordFocus.requestFocus()
        }
    )

    Text(
        text="Password",
        textStyle = TextStyle.HEADER_H4,
        color = colorResource(id = R.color.black),
        modifier = Modifier.padding(top = 24.dp)
    )
    TextField(
        value = password,
        onValueChange = setPassword,
        placeholder = "Type your password...",
        backgroundColor = colorResource(R.color.gray00),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .focusRequester(passwordFocus),
        isPassword = true,
        onSubmit = {
            if (password.isNotEmpty()) {
                hideKeyboard()
                login()
            }
        }
    )
    Text(
        text="Forgot password?",
        color = colorResource(R.color.eateryBlue),
        textStyle = TextStyle.BODY_SEMIBOLD,
        modifier = Modifier.padding(top = 12.dp)
    )
}
