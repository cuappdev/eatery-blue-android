package com.cornellappdev.android.eateryblue.ui.components.general

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayFive
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    placeholderText: String,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    enabled: Boolean = true,
) {
    var showCancel by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val onSubmit = {
            focusManager.clearFocus()
        }

        val interactionSource = remember { MutableInteractionSource() }
        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            enabled = enabled,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    showCancel = it.isFocused
                },
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            interactionSource = interactionSource,
            singleLine = true,
            textStyle = TextStyle(
                color = GrayFive,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        ) { innerTextField ->
            Surface(shape = RoundedCornerShape(8.dp), color = GrayZero) {
                TextFieldDefaults.TextFieldDecorationBox(
                    value = searchText,
                    innerTextField = innerTextField,
                    singleLine = true,
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(0.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GrayFive
                        )

                    },
                    placeholder = {
                        Text(
                            text = placeholderText,
                            color = GrayFive
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    enabled = enabled,
                    visualTransformation = VisualTransformation.None,
                )
            }
        }

        if (showCancel) {
            TextButton(
                modifier = Modifier.padding(start = 12.dp),
                onClick = {
                    onCancelClicked()
                    focusManager.clearFocus()
                }
            ) {
                Text(
                    text = "Cancel",
                    style = EateryBlueTypography.subtitle2,
                    color = GrayFive
                )
            }
        }
    }
}
