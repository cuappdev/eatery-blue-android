package com.appdev.eateryblueandroid.ui.components.core

import android.view.KeyEvent
import android.view.RoundedCorner
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.theme.sfProTextFontFamily
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import androidx.compose.material.TextField as AndroidTextField
import androidx.compose.ui.text.TextStyle as AndroidTextStyle

@Composable
fun TextField(
    value: String,
    onValueChange: (updated: String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onSubmit: () -> Unit = {},
    backgroundColor: Color = colorResource(id = R.color.white),
    isPassword: Boolean = false
) {
    AndroidTextField(
        value = value,
        onValueChange = onValueChange,
        maxLines = 1,
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(8.dp),
        textStyle = AndroidTextStyle(
            color = Color.Black,
            fontFamily = sfProTextFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = backgroundColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = colorResource(id = R.color.black)
        ),
        placeholder = { Text(
            text = placeholder,
            textStyle = TextStyle.BODY_MEDIUM,
            color = colorResource(id = R.color.gray05)
        )},
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrect = false,
            keyboardType = KeyboardType.Text,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onSubmit()
            }
        ),
        modifier = modifier.onKeyEvent {
            if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                onSubmit()
                true
            } else {
                false
            }
        }
    )
}