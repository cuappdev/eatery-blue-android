package com.cornellappdev.android.eateryblue.ui.components.general

import android.view.KeyEvent.KEYCODE_ENTER
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayThree

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (updated: String) -> Unit,
    placeholder: String,
    onSubmit: () -> Unit = {},
    backgroundColor: Color = Color.White,
    enabled: Boolean = true,
    focusRequester: FocusRequester? = null,
    isPassword: Boolean = false,
    leftIcon: Painter? = null,
    singleLine: Boolean = true,
    isSentence: Boolean = false,
) {
    val interactionSource = MutableInteractionSource()
    var passwordVisible by remember { mutableStateOf(false) }
    val passwordVisualTransformation =
        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()

    Surface(
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxWidth()
                .then(
                    if (singleLine)
                        Modifier
                    else
                        Modifier.fillMaxHeight(.7f)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leftIcon != null) {
                Icon(
                    painter = leftIcon,
                    contentDescription = null,
                    tint = GrayFive,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            BasicTextField(
                value = value,
                onValueChange = {
                    onValueChange(if (singleLine) it.filter { c -> c != '\n' } else it)
                },
                enabled = enabled,
                maxLines = 1,
                singleLine = singleLine,
                visualTransformation = if (isPassword) passwordVisualTransformation else VisualTransformation.None,
                textStyle = TextStyle(
                    color = if (!enabled) GrayThree else Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.TextFieldDecorationBox(
                        value = value,
                        innerTextField = innerTextField,
                        singleLine = singleLine,
                        enabled = enabled,
                        visualTransformation = if (isPassword) passwordVisualTransformation else VisualTransformation.None,
                        trailingIcon = {
                            if (isPassword) {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Filled.Visibility
                                        else Icons.Filled.VisibilityOff,
                                        if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            }
                        },
                        placeholder = {
                            Text(
                                text = placeholder,
                                style = EateryBlueTypography.subtitle2,
                                color = GrayFive,
                            )
                        },
                        interactionSource = interactionSource,
                        contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                            12.dp,
                            12.dp,
                            12.dp,
                            12.dp
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = if (isSentence) KeyboardCapitalization.Sentences else KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSubmit()
                    }
                ),
                modifier = modifier
                    .then(Modifier.onKeyEvent {
                        if (it.nativeKeyEvent.keyCode == KEYCODE_ENTER) {
                            onSubmit()
                            true
                        } else {
                            false
                        }
                    })
                    .then(
                        if (focusRequester != null)
                            Modifier.focusRequester(focusRequester)
                        else Modifier
                    )
                    .fillMaxWidth()
                    .then(
                        if (singleLine)
                            Modifier
                        else
                            Modifier.fillMaxHeight()
                    )
            )
        }
    }
}
