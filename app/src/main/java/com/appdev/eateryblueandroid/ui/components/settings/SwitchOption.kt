package com.appdev.eateryblueandroid.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.profile.Switch
import com.appdev.eateryblueandroid.ui.screens.settings.pausedNotifications

@Composable
fun SwitchOption(
    title: String,
    description: String,
    onCheckedChange: (Boolean) -> Unit,
    disableOnPause: Boolean = true,
    initialValue: Boolean = true
) {
    var switched by remember { mutableStateOf(initialValue) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    title,
                    textStyle = TextStyle.HEADER_H4,
                    modifier = if (description.isNotEmpty()) Modifier.padding(top = 16.dp)
                    else Modifier.padding(top = 25.dp, bottom = 25.dp)
                )
                if (description.isNotEmpty())
                    Text(
                        text = description,
                        textStyle = TextStyle.LABEL_SEMIBOLD,
                        color = colorResource(id = R.color.gray05),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
            }
        }
        Switch(
            initialValue = switched,
            onCheckedChange = {
                switched = !switched
                onCheckedChange(switched)
            },
            enabled = (!pausedNotifications.value || pausedNotifications.value && !disableOnPause),
            checkedThumbColor = colorResource(id = R.color.white),
            uncheckedThumbColor = colorResource(id = R.color.white),
            checkedTrackColor = colorResource(id = R.color.eateryBlue),
            uncheckedTrackColor = colorResource(id = R.color.gray01),
        )
    }
}