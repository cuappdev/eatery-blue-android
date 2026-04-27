package com.cornellappdev.android.eatery.ui.components.general

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.currentColors
import com.cornellappdev.android.eatery.util.DualModePreview
import com.cornellappdev.android.eatery.util.EateryPreview
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    placeholderText: String,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    enabled: Boolean = true,
    inputDebounceMillis: Long = 0,
) {
    var showCancel by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(searchText)) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(textFieldValue.text, inputDebounceMillis) {
        if (textFieldValue.text != searchText) {
            if (inputDebounceMillis > 0) {
                delay(inputDebounceMillis)
            }
            if (textFieldValue.text != searchText) {
                onSearchTextChange(textFieldValue.text)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val onSubmit = {
            focusManager.clearFocus()
        }

        TextField(
            value = textFieldValue,
            onValueChange = { updatedValue ->
                textFieldValue = updatedValue
            },
            enabled = enabled,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    showCancel = it.isFocused
                },
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true,
            textStyle = TextStyle(
                color = currentColors.textSecondary,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            shape = RoundedCornerShape(8.dp),
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.a11y_search_icon),
                    tint = currentColors.textSecondary
                )
            },
            placeholder = {
                Text(
                    text = placeholderText,
                    color = currentColors.textSecondary
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = currentColors.accentPrimary,
                unfocusedContainerColor = currentColors.accentPrimary,
                disabledContainerColor = currentColors.accentPrimary,
                cursorColor = currentColors.textPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        if (showCancel) {
            TextButton(
                modifier = Modifier.padding(start = 12.dp),
                onClick = {
                    onCancelClicked()
                    focusManager.clearFocus()
                }
            ) {
                Text(
                    text = stringResource(R.string.search_cancel),
                    style = EateryBlueTypography.subtitle2,
                    color = currentColors.textSecondary
                )
            }
        }
    }
}

@DualModePreview
@Composable
fun SearchBarPreview() = EateryPreview {
    SearchBar(
        searchText = "",
        onSearchTextChange = {},
        placeholderText = stringResource(R.string.search_placeholder_menu),
        onCancelClicked = {}
    )
}
