package com.appdev.eateryblueandroid.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.profile.Switch
import com.appdev.eateryblueandroid.ui.screens.SettingsLineSeparator
import com.appdev.eateryblueandroid.ui.screens.SettingsOption
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

private var paused = mutableStateOf(false)

@Composable
fun NotificationsScreen(profileViewModel: ProfileViewModel) {
    fun onBack() {
        profileViewModel.transitionSettings()
    }

    Column(
        modifier = Modifier
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 5.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_leftarrow),
                contentDescription = null,
                tint = colorResource(id = R.color.black),
                modifier = Modifier
                    .clickable { onBack() }
                    .clip(CircleShape)

            )
        }

        Text(
            text = "Notifications",
            color = colorResource(id = R.color.eateryBlue),
            textStyle = TextStyle.HEADER_H1,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = "Manage item and promotional notifications",
            textStyle = TextStyle.APPDEV_BODY_MEDIUM,
            color = colorResource(id = R.color.gray06),
            modifier = Modifier.padding(top = 7.dp, bottom = 12.dp)
        )

        SwitchOption(
            "Pause All Notifications",
            "",
            disableOnPause = false,
            initialValue = false,
            onCheckedChange = { switched ->
                paused.value = switched
            })
        SettingsLineSeparator()
        SwitchOption(
            "Favorite Item Notifications",
            "Get notified when favorite items are served",
            {})
        SettingsLineSeparator()
        SwitchOption(
            "Cornell AppDev Notifications",
            "Get notified about new releases and feedback",
            {})
        SettingsLineSeparator()
        SwitchOption(
            "Cornell Dining Notifications",
            "Get notified about special menus and meals",
            {})
        SettingsLineSeparator()
        SwitchOption("Account Notifications", "Get notified about account security and privacy", {})
        SettingsLineSeparator()
        SettingsOption(
            icon = null,
            title = "Privacy Settings",
            description = "",
            onClick = { profileViewModel.transitionPrivacy() }
        )
    }
}

@Composable
fun SwitchOption(
    title: String,
    description: String,
    onCheckedChange: (Boolean) -> Unit,
    disableOnPause: Boolean = true,
    initialValue : Boolean = true
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
            enabled = (!paused.value || paused.value && !disableOnPause),
            checkedThumbColor = colorResource(id = R.color.white),
            uncheckedThumbColor = colorResource(id = R.color.white),
            checkedTrackColor = colorResource(id = R.color.eateryBlue),
            uncheckedTrackColor = colorResource(id = R.color.gray01),
        )
    }
}