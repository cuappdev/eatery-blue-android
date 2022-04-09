package com.appdev.eateryblueandroid.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.settings.SettingsOption
import com.appdev.eateryblueandroid.ui.components.settings.SwitchOption
import com.appdev.eateryblueandroid.ui.screens.SettingsLineSeparator
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel

var pausedNotifications = mutableStateOf(false)

@Composable
fun NotificationsScreen(profileViewModel: ProfileViewModel) {
    fun onBack() {
        profileViewModel.transitionSettings()
    }

    val interactionSource = MutableInteractionSource()
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
                    .clickable(
                        onClick = { onBack() },
                        interactionSource = interactionSource,
                        indication = null
                    )
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
                pausedNotifications.value = switched
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

    BackHandler {
        onBack()
    }
}